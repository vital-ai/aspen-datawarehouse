$(function(){

	$(document).bind(EVENTBUS_CONNECT, function(){

		//eventbus_jars_list(onJarsListReady, onJarsListError);
		
	
	});

});

var jarRowTemplate = null

function onJarsListReady(result) {

  if( jarRowTemplate == null ) {
  
    jarRowTemplate = $('#jar-template').remove().removeAttr('id').css('display', '');
    
  }
  
  handleJarsList($('#jars-list'), result.list);

}

function onJobsListError(message) {
  alert(message);
}

function handleJarsList(tableEl, jars) {

  tableEl.find('tr:not(:first)').remove();
  
  for(var i = 1 ; i <= jars.length; i++ ) {
    
    var jar = jars[i-1];
    
    var t = jarRowTemplate.clone();
    
    t.find('.index').text( i + '.');
    t.find('.jar-name').text( jar.name );
    t.find('.jar-file-name').text(jar.filename);
    t.find('.jar-upload-time').text(jar.uploadTime);
    
    tableEl.append(t);
    
  }


}
