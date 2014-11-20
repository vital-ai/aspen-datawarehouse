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
