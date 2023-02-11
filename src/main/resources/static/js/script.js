$(document).ready(function () {

	window.addEventListener("submit", function() {
		showLoader();
	})


	 // order page
	 const select = document.getElementById("game-select");
	 const urlParams = new URLSearchParams(window.location.search);
	 const selectedValue = urlParams.get("gameserver");
	 if (selectedValue) {
		 select.value = selectedValue;
	 }
	 select.addEventListener("change", function() {
		 const selectedValue = this.value;
		 const currentUrl = new URL(window.location);
		 currentUrl.searchParams.set("gameserver", selectedValue);
		 window.history.pushState({}, "", currentUrl.toString());
	   });
	});

	$(document).ready(function () {

		var boxheight = $('#myCarousel .carousel-inner').innerHeight();
		var itemlength = $('#myCarousel .item').length;
		var triggerheight = Math.round(boxheight / itemlength + 1);
		$('#myCarousel .list-group-item').outerHeight(triggerheight);
	
		var clickEvent = false;
		$('#myCarousel').carousel({
			interval: 4000
		}).on('click', '.list-group li', function () {
			clickEvent = true;
			$('.list-group li').removeClass('active');
			$(this).addClass('active');
		}).on('slid.bs.carousel', function (e) {
			if (!clickEvent) {
				var count = $('.list-group').children().length - 1;
				var current = $('.list-group li.active');
				current.removeClass('active').next().addClass('active');
				var id = parseInt(current.data('slide-to'));
				if (count == id) {
					$('.list-group li').first().addClass('active');
				}
			}
			clickEvent = false;
		});

	});


	function showLoader() {
		document.getElementById("loader").style.display = "block";
		document.getElementById("main").classList.add("overlay");
		setTimeout(function(){
			document.getElementById("loader").style.display = "none";
			document.getElementById("main").classList.remove("overlay");
			document.querySelector(".overlay").style.display = "none";
		}, 1000); // 3000 milliseconds = 3 seconds
	}
	

	