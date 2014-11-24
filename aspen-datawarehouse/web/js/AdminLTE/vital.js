$(function() {

  //the piece of script that adjusts the navigation

  selectActiveMenuItem();
	
});


function selectActiveMenuItem() {
	
	var menu = $('.sidebar-menu');
	
	//determine webpage
	var path = window. window.location.pathname;
	
	if(path == "/") {
		path = "index.html";
	} else {
		if(path.indexOf("/") == 0) {
			path = path.substring(1);
		}
	}
	
	console.log("current page:", path)
	
	menu.find("a").each(function(index){
		var $this = $(this);
		var $parent = $this.parent();
		if( $this.attr('href') == path ) {
			$parent.addClass('active');
		} else {
			$parent.removeClass('active');
		}
	});
	
}

/**
 * Base class for popovers 
 */
VitalPopover = function(el) {
	this.el = el;
	
}


VitalPopover.prototype.validate = function(){
	
	//iterate over the form and mark empty fields etc
	
	var valid = true;
	
	this.el.find('.form-group').each(function(){
		var g = $(this);

		var v = g.find('input').val();
		
		if(v == null || v == '') {
			g.addClass('has-error');
			valid = false;
		} else {
			g.removeClass('has-error');
		}
		
		
	});
	
	return valid;
}

/**
 * Disables the modal and clears the message.
 */
VitalPopover.prototype.onAction = function() {
	this.el.find('input, button').attr('disabled', 'disabled');
	this.el.find('.error-message').text('').hide();
	this.el.find('.modal-body > *:not(.success-message)').show();
}

VitalPopover.prototype.onSuccess = function(msg) {
	
	this.el.find('input, button').removeAttr('disabled');
	
	this.el.find('.modal-body > *:not(.success-message)').hide();
	this.el.find('.success-message').text(msg).show();
	
	this.el.find('.modal-footer > *:not(.success-close)').hide();
	this.el.find('.modal-footer > .success-close').show();
	
}

VitalPopover.prototype.onError = function(message) {

	this.el.find('input, button').removeAttr('disabled');
	
	this.el.find('.error-message').text(message).show();
	
}

VitalPopover.prototype.open = function() {
	
	//constructor should reset state
	this.el.find('input, button').removeAttr('disabled');
	this.el.find('.form-group').removeClass('has-error');
	this.el.find('.success-message').text('').hide();
	this.el.find('.error-message').text('').hide();
	
	this.el.find('.modal-body > *:not(.success-message)').show();
	
	this.el.find('.modal-footer > *:not(.success-close)').show();
	this.el.find('.modal-footer > .success-close').hide();
	
	this.el.find('input[type=text], input[type=file]').val('');
	
	this.el.modal({
		show: true
	});
}

VitalPopover.prototype.close = function() {
	this.el.modal('hide');
}