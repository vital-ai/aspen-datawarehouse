package ai.vital.aspen.datawarehouse.service

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

abstract class AbstractHandler {

	public final static String ok = 'ok'
	
	public void handleError(Message<JsonObject> msg, String status, String errorMessage) {
		msg.reply(new JsonObject([status: status, message: errorMessage]))
	}
	
	public void handleOK(Message<JsonObject> msg, String okMessage) {
		msg.reply(new JsonObject([status: ok, message: okMessage]))
	}
}