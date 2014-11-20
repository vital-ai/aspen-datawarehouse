package ai.vital.aspen.datawarehouse.rest

import org.vertx.java.core.Vertx
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject;
class JobsHandler {

	Vertx vertx 
	
	long currentDate
	
	public JobsHandler(Vertx vertx) {
		
		this.vertx = vertx
		this.currentDate = System.currentTimeMillis()
	}
	
	public void handleListJobs(Message<JsonObject> message) {
	
		//some dummy data
		
		Map r = ['status': 'ok']
		
		//create some dummy jobs list
		r.running = [
			[id: 'job123', 'classpath': 'cp1', 'context': 'ctx1', 'startTime': new Date(currentDate -6000000).toString(), 'duration': '(still running)'],
			[id: 'job321', 'classpath': 'cp2', 'context': 'ctx2', 'startTime': new Date(currentDate -250000).toString(), 'duration': '(still running)'],
			[id: 'job456', 'classpath': 'cp3', 'context': 'ctx3', 'startTime': new Date(currentDate - 123).toString(), 'duration': '(still running)']
		]
		
		r.complete = [
			[id: 'job478', 'classpath': 'cp1', 'context': 'ctx1', 'startTime': new Date(currentDate -16000000).toString(), 'duration': '5 hours 3 minutes'],
		]
		
		r.failed = [
			[id: 'job666', 'classpath': 'cp3', 'context': 'ctx3', 'startTime': new Date(currentDate -26000000).toString(), 'duration': '1.1 seconds'],
		]
		
		
		message.reply(new JsonObject(r))
			
	}
	
}
