package ai.vital.aspen.datawarehouse.service

import org.vertx.java.core.http.RouteMatcher;

public class WebServer extends WebServerBase {

  @Override
  protected RouteMatcher routeMatcher() {
	RouteMatcher matcher = new RouteMatcher();
	
	String webRoot = getOptionalStringConfig("web_root", DEFAULT_WEB_ROOT);
	
	boolean cacheSite = getMandatoryBooleanConfig("cache_site");
	
	HTMLTemplatePageHandler htmlTemplatePageHandler = new HTMLTemplatePageHandler(webRoot, cacheSite);
	
	//all html pages are template based!
	
	REST_VitalServiceHandler vsHandler = new REST_VitalServiceHandler()
	
	//same as file upload
	matcher.allWithRegEx("\\/java\\/.*", vsHandler);
	
	matcher.get("/", htmlTemplatePageHandler);
	
	matcher.getWithRegEx(".*\\.html", htmlTemplatePageHandler)
	
//    matcher.getWithRegEx("\\/mobile/(index\\.html)?", mobilePageHandler);
	
	matcher.noMatch(staticHandler());
	
	return matcher;
  }
}
