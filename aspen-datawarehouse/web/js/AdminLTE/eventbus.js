//global variable

var eb = new vertx.EventBus(window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/eventbus');

//event triggered on document object when the connection is established
var EVENTBUS_CONNECT = "EVENTBUS_CONNECT";

eb.onopen = function() {
	
	console.log("Event bus connected, checking session...");
	
	$(document).trigger(EVENTBUS_CONNECT);
	
};



function eventbus_jobs_list(successCB, errorCB) {
	
	console.log('eventbus_jobs_list');
	
	eb.send('jobs.list', {}, function(result) {
		
		console.log("eventbus_jobs_list response: ", result);
		
		if( result.status == 'ok' ) {
		
			successCB(result);
		
		} else {
			
			errorCB(result.message);
			
		}
		
	});
	
}