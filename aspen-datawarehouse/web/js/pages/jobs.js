$(function(){

	$(document).bind(EVENTBUS_CONNECT, function(){

		eventbus_jobs_list(onJobsListReady, onJobsListError);
	
	});

});

var jobRowTemplate = null

function onJobsListReady(result) {

  if( jobRowTemplate == null ) {
  
    jobRowTemplate = $('#job-template').remove().removeAttr('id').css('display', '');
    
  }
  
  handleJobsList($('#running-jobs'), result.running);
  
  handleJobsList($('#complete-jobs'), result.complete);
  
  handleJobsList($('#failed-jobs'), result.failed);
  
  
  
  
  

}

function onJobsListError(status, message) {
  alert(status + ' - ' + message);
}

function handleJobsList(tableEl, jobs) {

  tableEl.find('tr:not(:first)').remove();
  
  for(var i = 1 ; i <= jobs.length; i++ ) {
    
    var job = jobs[i-1];
    
    var t = jobRowTemplate.clone();
    
    t.find('.index').text( i + '.');
    t.find('.job-id').text( job.id );
    t.find('.classpath').text(job.cp);
    t.find('.context').text(job.context);
    t.find('.start-time').text(job.startTime);
    t.find('.duration').text(job.duration);
    
    tableEl.append(t);
    
  }


}
