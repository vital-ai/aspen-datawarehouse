package ai.vital.aspen.datawarehouse.service

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.EventBusBridgeHook;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.core.sockjs.SockJSSocket;


public abstract class WebServerBase extends BusModBase {
	
	  public static final int DEFAULT_PORT = 80;
	
	  public static final String DEFAULT_ADDRESS = "0.0.0.0";
	
	  public static final String DEFAULT_WEB_ROOT = "web";
	
	  public static final String DEFAULT_INDEX_PAGE = "index.html";
	
	  public static final String DEFAULT_AUTH_ADDRESS = "vertx.basicauthmanager.authorise";
	
	  public static final long DEFAULT_AUTH_TIMEOUT = 5 * 60 * 1000;
	
	  private final static Logger log = LoggerFactory.getLogger(WebServerBase.class);
	  
	  @Override
	  public void start(final Future<Void> result) {
		start();
	
		HttpServer server = vertx.createHttpServer();
		
		server.setCompressionSupported(true);
		server.setTCPKeepAlive(true);
	
		boolean ssl = getOptionalBooleanConfig("ssl", false);
		
		if( ssl ) {
		  server.setSSL(true).setKeyStorePassword(getOptionalStringConfig("key_store_password", "wibble"))
							 .setKeyStorePath(getOptionalStringConfig("key_store_path", "server-keystore.jks"));
		}
	
		
		if (getOptionalBooleanConfig("route_matcher", false)) {
		  server.requestHandler(routeMatcher());
		}
		else if (getOptionalBooleanConfig("static_files", true)) {
		  server.requestHandler(staticHandler());
		}
		
	
		
		int port = getOptionalIntConfig("port", DEFAULT_PORT);
		int websocketPort = getOptionalIntConfig("websocketPort", DEFAULT_PORT);
		
		String host =  getOptionalStringConfig("host", DEFAULT_ADDRESS);
	
		// Must always bridge AFTER setting request handlers
		boolean bridge = getOptionalBooleanConfig("bridge", false);
		if (bridge) {
			
			container.logger().info("Setting up sockjs bridge now...");
			
			HttpServer websocketServer = null;
			
			if(port == websocketPort) {
			
				container.logger().info("Http and websocket ports are the same: " + port);
				
				websocketServer = server;
				
			} else {
				
				container.logger().info("Different http and websocket ports: " + port + " vs. " + websocketPort + " extra websocket server necessary");
				
				websocketServer = vertx.createHttpServer();
				websocketServer.setCompressionSupported(true);
				websocketServer.setTCPKeepAlive(true);
				
				if(ssl) {
					websocketServer.setSSL(true).setKeyStorePassword(getOptionalStringConfig("key_store_password", "wibble"))
					  .setKeyStorePath(getOptionalStringConfig("key_store_path", "server-keystore.jks"));
				}
	
				//no route matchers on this instance
				
			}
			
			SockJSServer sjsServer = vertx.createSockJSServer(websocketServer);
	
			
			//add sockjs hooks ?
			
			JsonArray inboundPermitted = getOptionalArrayConfig("inbound_permitted", new JsonArray());
			JsonArray outboundPermitted = getOptionalArrayConfig("outbound_permitted", new JsonArray());
		
			sjsServer.bridge(getOptionalObjectConfig("sjs_config", new JsonObject().putString("prefix", "/eventbus")),
					inboundPermitted, outboundPermitted,
					getOptionalLongConfig("auth_timeout", DEFAULT_AUTH_TIMEOUT),
					getOptionalStringConfig("auth_address", DEFAULT_AUTH_ADDRESS));
	
			if(port != websocketPort) {
				
				websocketServer.listen(websocketPort, host, new AsyncResultHandler<HttpServer>() {
					@Override
					public void handle(AsyncResult<HttpServer> ar) {
					  if (!ar.succeeded()) {
						result.setFailure(ar.cause());
					  } else {
						result.setResult(null);
					  }
					}
				  });
				
			}
			
		}
		
		server.listen(port, host, new AsyncResultHandler<HttpServer>() {
		  @Override
		  public void handle(AsyncResult<HttpServer> ar) {
			if (!ar.succeeded()) {
			  result.setFailure(ar.cause());
			} else {
			  result.setResult(null);
			}
		  }
		});
		
	
		//TODO register handlers here!
	
		//JobsHandler jobsHandler = new JobsHandler(vertx)
		//JarsHandler jarsHandler = new JarsHandler(vertx)
	
	  /*
	
		  vertx.eventBus().registerHandler("jobs.list") { Message<JsonObject> msg ->
			jobsHandler.handleListJobs(msg)
		}
		
		  
		  
		vertx.eventBus().registerHandler("jars.list") { Message<JsonObject> msg ->
			jarsHandler.handleListJars(msg)
		}
		
		vertx.eventBus().registerHandler("jars.delete") { Message<JsonObject> msg ->
			jarsHandler.handleDeleteJar(msg)
		}
	
		*/
	  
		  
	  }
	
	  /**
	* @return RouteMatcher
	*/
	  protected abstract RouteMatcher routeMatcher();
	
	  /**
	* @return Handler for serving static files
	*/
	  protected Handler<HttpServerRequest> staticHandler() {
		String webRoot = getOptionalStringConfig("web_root", DEFAULT_WEB_ROOT);
		String index = getOptionalStringConfig("index_page", DEFAULT_INDEX_PAGE);
		String webRootPrefix = webRoot + File.separator;
		String indexPage = webRootPrefix + index;
		boolean gzipFiles = getOptionalBooleanConfig("gzip_files", false);
		boolean caching = getOptionalBooleanConfig("caching", false);
	
		return new StaticFileHandler(vertx, webRootPrefix, indexPage, gzipFiles, caching);
	  }
	
	
	
}
