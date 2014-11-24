package ai.vital.aspen.datawarehouse.rest

import org.vertx.java.core.Vertx
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject;

import ai.vital.aspen.datawarehouse.mock.MockDao;

class JarsHandler extends AbstractHandler {

	Vertx vertx 
	
	public final static String error_no_name_param = 'error_no_name_param'
	
	public final static String error_jar_not_found = 'error_jar_not_found'
			
	public JarsHandler(Vertx vertx) {
		
		this.vertx = vertx
		
	}
	
	public void handleListJars(Message<JsonObject> message) {
	
		//some dummy data
		
		Map r = ['status': ok, list: MockDao.get().listJars()]
		
		//create some dummy jobs list
		
		message.reply(new JsonObject(r))
			
	}
	
	public void handleAddJar(Message<JsonObject> message) {
		
		
		
	}
	
	public void handleDeleteJar(Message<JsonObject> message) {
		
		JsonObject request = message.body()
		
		String name = request.getString('name', '')
		
		if(!name) {
			handleError message, error_no_name_param, 'missing name param'
			return
		}
		
		boolean success = MockDao.get().deleteJar(name)
		
		if(!success) {
			
			handleError message, error_jar_not_found, (String)"jar with name: ${name} not found"
			return 
		
		}
		
		handleOK message, "Jar with name: ${name} deleted successfully"
		
	}
	
}
