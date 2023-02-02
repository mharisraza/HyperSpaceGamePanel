$(document).ready(function(){
	
	// Offcanvas support
	$("[data-toggle='offcanvas']").on("click touchstart", function(e){	
		var offcanvas = $(this).data("target");
		$("body").toggleClass("overlay");
		$(offcanvas).toggleClass("offcanvas-open");
		return false;
	}); 
	
	// Hide offcanvas when clicking outside of it
	$(document).on("click touchstart", function(e){
		if($(e.target).hasClass("navbar")){
			$("body").toggleClass("overlay");
			$($(e.target).find(".navbar-offcanvas")).toggleClass("offcanvas-open");
			return false;
		}
	});
	
	// Multi-level submenu support
	$(".dropdown-menu > li > a").on("click", function(e){
		if($(this).siblings(".sub-menu").length > 0){
			$(this).closest(".dropdown").addClass("open");
			$(this).parent("li").addClass("has-submenu").toggleClass("open");
			return false;
		}
		else{
			$(this).parent("li").siblings("li").removeClass("open");
		}
	});
	
	// Fix for megamenus
	$(document).on("click", ".dropdown-megamenu .dropdown-menu", function(e) {
		e.stopPropagation()
	})

});

$(document).ready(function() {
    var boxheight = $('#myCarousel .carousel-inner').innerHeight();
    var itemlength = $('#myCarousel .item').length;
    var triggerheight = Math.round(boxheight/itemlength+1);
	$('#myCarousel .list-group-item').outerHeight(triggerheight);
});


$(document).ready(function(){
    
	var clickEvent = false;
	$('#myCarousel').carousel({
		interval:   4000	
	}).on('click', '.list-group li', function() {
			clickEvent = true;
			$('.list-group li').removeClass('active');
			$(this).addClass('active');		
	}).on('slid.bs.carousel', function(e) {
		if(!clickEvent) {
			var count = $('.list-group').children().length -1;
			var current = $('.list-group li.active');
			current.removeClass('active').next().addClass('active');
			var id = parseInt(current.data('slide-to'));
			if(count == id) {
				$('.list-group li').first().addClass('active');	
			}
		}
		clickEvent = false;
	});
})

