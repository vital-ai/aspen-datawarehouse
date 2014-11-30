$(function() {

	$(document).bind(EVENTBUS_CONNECT, function() {

		refreshJarsList();

	});

	//singleton
	var uploadPopover = new JarUploadPopover($('#add-jar-popup'));
	$('#jar-upload-confirm').click(function(){
		uploadPopover.doUploadJar();
	});
	
	$('#jar-upload-button').click(function(){
		uploadPopover.open();
	});
	

	
});

var jarRowTemplate = null

function refreshJarsList() {
	
	eventbus_jars_list(onJarsListReady, onJarsListError);
	
}

function onJarsListReady(result) {

	if (jarRowTemplate == null) {

		jarRowTemplate = $('#jar-template').remove().removeAttr('id').css(
				'display', '');

	}

	handleJarsList($('#jars-list'), result.list);

}

function onJarsListError(status, message) {
	alert(status + ' - ' + message);
}

function handleJarsList(tableEl, jars) {

	tableEl.find('tr:not(:first)').remove();

	for (var i = 1; i <= jars.length; i++) {

		var jar = jars[i - 1];

		var t = jarRowTemplate.clone();

		t.find('.jar-index').text(i + '.');
		t.find('.jar-name').text(jar.name);
		t.find('.jar-file-name').text(jar.filename);
		t.find('.jar-upload-time').text(jar.uploadTime);
		t.find('.jar-size').text(jar.size);

		t.find('.jar-delete-button').click(handleDeleteJar);
		t.find('.jar-download-button').click(handleDownloadJar);
		
		tableEl.append(t);

	}

	if (jars.length == 0) {

		tableEl.append(
			$('<tr>').append(
				$('<td>').attr('colspan', '5').text('(no jars)')
			)
		);

	}

}

function handleDeleteJar() {
	
	var row = $(this).closest('tr');
	var jarName = row.find('.jar-name').text();
	
	console.log('delete clicked: ', jarName);

	var _modal = $('#jar-delete-confirm');
	
	new JarDeletePopover(_modal, jarName).open();

}

function handleDownloadJar() {
	
	var row = $(this).closest('tr');
	
	var jarName = row.find('.jar-name').text();
	
	console.log('download clicked: ', jarName);
	
	//download linkrest
	//content disposition
	var form = $('#jar-download-form');
	
	form.find('[name=name]').val(jarName);
	
	form.submit();
	
}




JarUploadPopover = function(el) {
	this.el = el;
}

JarUploadPopover.prototype = new VitalPopover();
	
JarUploadPopover.prototype.constructor = JarUploadPopover;

JarUploadPopover.prototype.doUploadJar = function() {

	var _this = this;
	
	if( ! this.validate() ) return;
	
	var formData = new FormData($('#jar-form')[0]);
	this.onAction();
	console.log('form-data', formData);
	$.ajax({
            url: 'jars/upload',  //server script to process data
            type: 'POST',
            xhr: function() {  // custom xhr
                myXhr = $.ajaxSettings.xhr();
                if(myXhr.upload){ // if upload property exists
//                    myXhr.upload.addEventListener('progress', progressHandlingFunction, false); // progressbar
                }
                return myXhr;
            },
            //Ajax events
            success: completeHandler = function(data) {
            	
            	console.log("upload success", data);
            	
            	_this.onSuccess(data);
                
            	refreshJarsList();
            	
            },
            error: errorHandler = function(jqXHR, error, exception) {
            	console.error("jqHHR", jqXHR);
            	console.error("jqerror", error);
            	console.error("jqexception", exception);
            	_this.onError(jqXHR.status + ' - ' + jqXHR.responseText);
            },
            // Form data
            data: formData,
            //Options to tell JQuery not tos process data or worry about content-type
            cache: false,
            contentType: false,
            processData: false
        }, 'json');
	
}




JarDeletePopover = function(el, jarName) {
	this.el = el;
	this.jarName = jarName;
	
	this.el.find('.jar-name').text(jarName);
	var _this = this;
	var b = this.el.find('#jar-delete-confirm-button');
	b.off('click');
	b.click(function(){
		_this.doDeleteJar();
	});
	
}
JarDeletePopover.prototype = new VitalPopover();
JarDeletePopover.prototype.constructor = JarDeletePopover;

JarDeletePopover.prototype.doDeleteJar = function() {
	
	var _this = this;
	
	_this.onAction();
	
	eventbus_jars_delete(_this.jarName, function(result){

		_this.onSuccess(result.message);
		
		refreshJarsList();
		
	}, function(status, message){
		
		_this.onError(status + ' - ' + message);
		
	});
	
}

