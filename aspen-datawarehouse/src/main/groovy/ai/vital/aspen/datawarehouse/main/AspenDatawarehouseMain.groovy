package ai.vital.aspen.datawarehouse.main

import ai.vital.aspen.datawarehouse.mock.MockDao;
import ai.vital.aspen.datawarehouse.service.WebServer;

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import org.vertx.java.core.AsyncResult
import org.vertx.java.core.AsyncResultHandler
import org.vertx.java.core.json.JsonObject
import org.vertx.java.platform.PlatformLocator
import org.vertx.java.platform.PlatformManager


/**
 * Main aspen datawarehouse vert.x app starter
 * @author Derek
 *
 */
class AspenDatawarehouseMain {

	static main(args) {
	
		if(args.length != 1) {
			System.err.println("No config file!")
			System.exit(1)
		}
	
		PlatformManager platformManager = PlatformLocator.factory.createPlatformManager();
		
		File configFile = new File(args[0])
		
		println "Config file: ${configFile.absolutePath}"
		
		Config config = ConfigFactory.parseFile(configFile)
		
		Config webServerCfg = config.getConfig("webserver")
	
		Config daoConfig = config.getConfig("mockdao");
		
		MockDao.init(daoConfig)
		
		platformManager.deployVerticle(WebServer.class.getCanonicalName(), new JsonObject(webServerCfg.root().unwrapped()), new URL[0], 1, null, new AsyncResultHandler<String>() {
			public void handle(AsyncResult<String> asyncResult) {
				if (asyncResult.succeeded()) {
					println(WebServer.class.getCanonicalName() + " deployment ID is " + asyncResult.result());
				} else {
					System.err.println "Couldn't deploy webserver verticle: " + asyncResult.cause().localizedMessage;
					System.exit(1)
				}
			}
		})
		
		//prevent the app from exiting
		
		while(true) {
			
			Thread.sleep(60000)
			
		}
		
	}

}
