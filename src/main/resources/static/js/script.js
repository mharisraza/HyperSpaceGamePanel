$(document).ready(function () {
	

	$(function() {
		$('.dropdown').hover(function() {
		  $(this).addClass('show');
		  $(this).find('.dropdown-menu').addClass('show');
		}, function() {
		  $(this).removeClass('show');
		  $(this).find('.dropdown-menu').removeClass('show');
		});
		$('.dropdown > a').click(function() {
		  location.href = this.href;
		});
	  });

	  var navLinks = document.querySelectorAll('.nav-link');
       var currentUrl = window.location.href;
	   

navLinks.forEach(link => {

	
  if (link.href === currentUrl) {
    link.classList.add('active');
  }

  link.addEventListener('click', function() {
    // Remove active class from all links
    navLinks.forEach(link => {
      link.classList.remove('active');
    });

    // Add active class to clicked link
    this.classList.add('active');
  });
});

	  

	// add the class when modal is active:

    $('.modal').on('shown.bs.modal', function () {
        $('.main').addClass('blur');
    });

    $('.modal').on('hidden.bs.modal', function () {
        $('.main').removeClass('blur');
    });


	window.addEventListener("submit", function() {
		showLoader();
	});

	// show modal for dependencies installer in vps
	$(document).ready(function() {
		var urlParams = new URLSearchParams(window.location.search);
		if (urlParams.get('dependencyInstallRequired')) {
		  $('#dependencyInstallerModal').modal().show();
		}
	  });

	// call expiration date countdown immediately
	expirationCountDown();

	// Use setInterval to call the updateCountdown function every second
      const countdownInterval = setInterval(expirationCountDown, 1000);

	  // call server info method immediately to update info as soon as possible
	  serverInfoUpdate();

	  // Use setInterval to call the 'serverInfoUpdate' method every 3 seconds
	  setInterval(serverInfoUpdate, 3000);

	// do not let select previous date
	const today = new Date().toISOString().split('T')[0];
    document.getElementById("serverExpirationDate").setAttribute("min", today);


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
		var itemlength = $('#myCarousel .carousel-item').length;
		var triggerheight = Math.round(boxheight / itemlength + 1);
		$('#myCarousel .list-group-item').outerHeight(triggerheight);
	
		var clickEvent = false;
		$('#myCarousel').carousel({
			interval: 2500
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


const expirationCountDown = () => {

// Set the expiration date
const expirationDateField = document.getElementById('expiredDate');
const expirationDate = expirationDateField.getAttribute('expiredDate');


// Split the string into date and time parts
const [datePart, timePart] = expirationDate.split(' ');

// Parse the date part into a Date object
const [year, month, day] = datePart.split('-').map(Number);
const expirationDateTime = new Date(year, month - 1, day);


// Calculate the time remaining until the expiration date
const timeRemaining = expirationDateTime.getTime() - Date.now();

// Convert the time remaining to days, hours, and minutes
const daysRemaining = Math.floor(timeRemaining / (1000 * 60 * 60 * 24));
const hoursRemaining = Math.floor((timeRemaining / (1000 * 60 * 60)) % 24);
const minutesRemaining = Math.floor((timeRemaining / (1000 * 60)) % 60);
const secondsRemaining = Math.floor((timeRemaining / 1000) % 60);

// Output the time remaining
expirationDateField.innerHTML = `${daysRemaining} days, ${hoursRemaining} hours, ${minutesRemaining} minutes, ${secondsRemaining} seconds left`;
}	


const serverInfoUpdate = () => {
	const serverId = document.getElementById("serverIdforServerInfo").value;

	if(window.location.href.endsWith(`/view/${serverId}`)) {

	// 	var gameServerStatus = document.getElementById("gameServerStatus");
	//     var gameServerMap = document.getElementById("gameServerMap");
	//    var gameServerPlayers = document.getElementById("gameServerPlayers");

	const serverRestartButton = document.getElementById("serverRestartButton");
	const serverStopButton = document.getElementById("serverStopButton");
	const serverStartButton = document.getElementById("serverStartButton");

	fetch(`/serverInfo?serverId=${serverId}`).then(response => response.json())
	.then(data => {

		console.log(data)

          if(data.serverOnline) {

			// gameServerStatus.innerHTML = "Online";
			// gameServerStatus.classList.remove("text-danger");
			// gameServerStatus.classList.add("text-success");

			serverRestartButton.classList.remove("disabled");
			serverStopButton.classList.remove("disabled");
			serverStartButton.classList.add("disabled");

		  } else {
			// gameServerStatus.innerHTML = "Offline";
			// gameServerStatus.classList.remove("text-success");
			// gameServerStatus.classList.add("text-danger");

			serverRestartButton.classList.add("disabled");
			serverStopButton.classList.add("disabled");
			serverStartButton.classList.remove("disabled");
		  }

		  if(data.mapName === 'Map not available, server is offline') {
              gameServerMap.innerHTML = data.mapName;
			  gameServerMap.classList.add("text-danger");
		  } else {
			gameServerMap.innerHTML = data.mapName;
			gameServerMap.classList.add("text-white");
		  }

		  if(data.maxPlayers === 'Cannot get players, server is offline.') {
            gameServerPlayers.innerHTML = data.maxPlayers;
		  } else {
			gameServerPlayers.innerHTML = `${data.players}/${data.maxPlayers}`;
		  }


	}).catch(error => {
         console.log(error);
	});
	}
}

const showLoader = () => {
	document.getElementById("loader").style.display = "block";
	document.getElementById("main").classList.add("overlay");
}

	