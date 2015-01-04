package ai.vital.aspen.datawarehouse.rest;

import groovy.json.JsonSlurper;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

public class HTMLTemplatePageHandler implements Handler<HttpServerRequest> {

	private final static Logger log = LoggerFactory.getLogger(HTMLTemplatePageHandler.class);
	
	private String webRoot;
	
	private boolean cacheSite;
	
	private Map<String, String> cachedSites = Collections.synchronizedMap(new HashMap<String, String>())
	
	private JsonSlurper slurper = new JsonSlurper()
	
	static Pattern templatePattern = Pattern.compile("@@@(.+)@@@");
	
	static Pattern variablePattern = Pattern.compile("__(.+)__");
	
	public HTMLTemplatePageHandler(String webRoot, boolean cacheSite) {
		super();
		this.webRoot = webRoot;
		this.cacheSite = cacheSite;
	}


	private String loadContent(String path) throws IOException {
		
		String content = null;
		
		if(cachedSites) {
			
			content = cachedSites.get(path)
			
			if(content != null) return content
			
		}
		
		
		File f = new File(webRoot, path)
			
		if(!f.exists()) return null
			
		content = FileUtils.readFileToString(f, "UTF-8");
		
		Matcher matcher = templatePattern.matcher(content);
		
		StringBuilder replaced = null
		
		int lastEnd = 0
		
		while(matcher.find()) {
		
			//update String 
			
			if(replaced == null) replaced = new StringBuilder()
			
			int start = matcher.start()
			int end = matcher.end()

			replaced.append(content.substring(lastEnd, start))
			
			lastEnd = end
						
			
			String templatePath = matcher.group(1).trim()
			
			int indexOfJSONContext = templatePath.indexOf('|')
			
			Map params = [:]
			
			if(indexOfJSONContext > 0) {
				
				//parse json map
				params = slurper.parseText(templatePath.substring(indexOfJSONContext+1))
				
				templatePath = templatePath.substring(0, indexOfJSONContext).trim()
				
			}
			
			String substitute = loadContent(templatePath)
			
			//post process substitute
			
			
			if(substitute == null) throw new IOException("Template path not found: " + templatePath)
			
			Matcher substituteMatcher = variablePattern.matcher(substitute)
			
			StringBuilder sb = null
			
			int lastSBEnd = 0
			
			while(substituteMatcher.find()) {
				
				if(sb == null) sb = new StringBuilder()
				
				int sb_start = substituteMatcher.start()
				int sb_end = substituteMatcher.end()
				
				sb.append(substitute.substring(lastSBEnd, sb_start))
				
				String variable = substituteMatcher.group(1)	
				
				String val = params.get(variable)
				if(val == null) val = substituteMatcher.group()
				
				sb.append(val)
				
				lastSBEnd = sb_end
				
			}
			
			if(sb != null) {
				sb.append(substitute.substring(lastSBEnd))
				substitute = sb.toString()
			}
			
			replaced.append(substitute)
			
		}
		
		if(replaced != null) {
			replaced.append(content.substring(lastEnd))
			content = replaced.toString()
		}
		
		if(cacheSite) {
			
			cachedSites.put(path, content)
			
		} else {
		
			log.warn("WARNING - the site ${path} is not cached - it is only advised in development only!");
		
		}
		
		return content;
		
	}

	@Override
	public void handle(HttpServerRequest request) {

		String uri = request.uri()

		if(uri == '' || uri == '/') {
			uri = "index.html"
		}		
 
	    String content = null;
	    
	    int bytesLength = 0;
	    
	    try {
	    	
			if(uri.startsWith("/")) uri = uri.substring(1)
			
	    	content = loadContent(uri);

	    } catch (Exception e) {
	    	e.printStackTrace();
	    	request.response().setStatusCode(500).write(e.getLocalizedMessage()).end();
	    	return
	    }
		
		if(content == null) {
			//404 status
			request.response().setStatusCode(404).end("NOT FOUND");
			return
		}
		
		bytesLength = content.getBytes("UTF-8").length;
	    
	    request.response().putHeader(Headers.CONTENT_LENGTH, "" + bytesLength).putHeader(Headers.CONTENT_TYPE, "text/html;charset=UTF-8").write(content, "UTF-8").end();
		
	}

}
