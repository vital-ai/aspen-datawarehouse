/*
* Copyright 2011-2012 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package ai.vital.aspen.datawarehouse.rest;

import org.vertx.java.core.http.RouteMatcher;


/**
* A simple web server module that implements a provides
* a default RouteMatcher that simply serves static files.
*
* @author <a href="http://tfox.org">Tim Fox</a>
* @author pidster
*/
public class WebServer extends WebServerBase {


	
  @Override
  protected RouteMatcher routeMatcher() {
    RouteMatcher matcher = new RouteMatcher();
    
    String webRoot = getOptionalStringConfig("web_root", DEFAULT_WEB_ROOT);
    
    boolean cacheSite = getMandatoryBooleanConfig("cache_site");
    
	HTMLTemplatePageHandler htmlTemplatePageHandler = new HTMLTemplatePageHandler(webRoot, cacheSite);
    
	//all html pages are template based!
	
	REST_JarsHandler jarsHandler = new REST_JarsHandler()
	
	//same as file upload
	matcher.allWithRegEx("\\/jars\\/.*", jarsHandler);
	
	matcher.get("/", htmlTemplatePageHandler);
	
	matcher.getWithRegEx(".*\\.html", htmlTemplatePageHandler)
	
	
    
//    matcher.getWithRegEx("\\/mobile/(index\\.html)?", mobilePageHandler);
    
    matcher.noMatch(staticHandler());
    return matcher;
  }

}