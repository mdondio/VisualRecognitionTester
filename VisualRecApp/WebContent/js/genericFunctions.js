/*
 * Funzioni dedicate alla personalizzazione degli header delle pagine html
 * 
 * */


//var path = ""; // generalizzazione del percorso delle pagine per il webcontent


/**
 * @returns Restituisce l'head delle pagine: inclusione di utilities, pagine css e titolo tab browser
 */
function createHead(){
	var met_sta = ["<meta http-equiv='X-UA-Compatible' content='IE=edge'><meta name='viewport' content='width=device-width, initial-scale=1.0'><meta name='ROBOTS' content='INDEX,FOLLOW'><meta name='GOOGLEBOT' content='ARCHIVE'><link href='https://fonts.googleapis.com/icon?family=Material+Icons' rel='stylesheet'>"];
	var css_car1 = ["<link rel='stylesheet' type='text/css' href='css/main.css'/>"]; //css per carbon design - main
	var css_car2 = ["<link rel='stylesheet' type='text/css' href='css/carbon-components.css'/>"]; //css per carbon design - components
	var css_fon = ["<link rel='stylesheet' type='text/css' href='css/fonts.css'/>"]; //css fonts
	var css_def = ["<link rel='stylesheet' type='text/css' href='css/default.css'/>"]; //css per le pagine
	var css_str = ["<link rel='stylesheet' type='text/css' href='css/structure.css'/>"]; //css per le pagine
	var css_uti = ["<link rel='stylesheet' type='text/css' href='css/utilities.css'/>"]; //css per parti comuni della pagina
	var css_swa = ["<link rel='stylesheet' type='text/css' href='css/sweetalert2_mine.css'/>"]; //css per sweetalert2
	var js_swal = ["<script src='js/sweetalert2_mine.js'>"];
	
	var title = ["<title>Bellosguardo</title>"];

	var headfull = met_sta.concat(css_car1, css_car2, css_fon, css_def, css_str, css_uti, css_swa, js_swal, title);
	$('head').append(headfull);
}

/**
 * @returns Genera il menu, sia per desktop che per mobile
 */
function createHeader(){
	
	$('header').append("<nav id='cssmenu'>");	
		$('#cssmenu').append("<div class='logo'><img src='img/Avatar_new.png' height='40px'><a href='/'>Bellosguardo</a></div>");
		$('#cssmenu').append("<div id='head-mobile'></div><div class='button'></div>");
		$('#cssmenu').append("<ul id='begin'>");
			$('#begin').append("<li><a href='home_old.html'>dashboard");
			$('#begin').append("<li><a href='simulate.html'>simulate</a><ul id='subs0'>");
				$('#subs0').append("<li><a href='simulate.html'><b>run</b> new test");
				$('#subs0').append("<li><a href='history.html'><b>see</b> previous tests");
			$('#begin').append("<li><a href='train.html'>train");
			$('#begin').append("<li><a href='doc.html'>docs");
			$('#begin').append("<li><a href='aboutus.html'>about us");	
			$('#begin').append("<li><a href='logoutservlet'>logout");
	
}


function createProgress(div_id, toColor, number){
	var bar = new ProgressBar.Circle(div_id, {
		  color: '#aaa',
		  // This has to be the same size as the maximum width to
		  // prevent clipping
		  strokeWidth: 4,
		  trailWidth: 1,
		  easing: 'easeInOut',
		  duration: 1400,
		  text: {
		    autoStyleContainer: false
		  },
		  from: { color: '#aaa', width: 1 },
		  to: { color: toColor, width: 4 },
		  // Set default step function for all animate calls
		  step: function(state, circle) {
		    circle.path.setAttribute('stroke', state.color);
		    circle.path.setAttribute('stroke-width', state.width);

		    var value = Math.round(circle.value() * 100);
		    if (value === 0) {
		      circle.setText('');
		    } else {
		      circle.setText(value+'&#37;');
		    }

		  }
		});
		bar.text.style.fontFamily = '"Raleway", Helvetica, sans-serif';
		bar.text.style.fontSize = '2rem';
		bar.animate(number);  // Number from 0.0 to 1.0
}


function createCircle(div_id, toColor, number){
	var bar = new ProgressBar.Circle(div_id, {
		  color: '#aaa',
		  // This has to be the same size as the maximum width to
		  // prevent clipping
		  strokeWidth: 4,
		  trailWidth: 1,
		  easing: 'easeInOut',
		  duration: 1400,
		  text: {
		    autoStyleContainer: false
		  },
		  from: { color: '#aaa', width: 1 },
		  to: { color: toColor, width: 4 },
		  // Set default step function for all animate calls
		  step: function(state, circle) {
		    circle.path.setAttribute('stroke', state.color);
		    circle.path.setAttribute('stroke-width', state.width);

//		    var value = Math.round(circle.value() * 100);
//		    if (number === 0) {
//		      circle.setText('');
//		    } else {
		      circle.setText(number);
//		    }

		  }
		});
		bar.text.style.fontFamily = '"Raleway", Helvetica, sans-serif';
		bar.text.style.fontSize = '2rem';
		bar.animate(1.0);  // Always full circle
}


function createCarbonHeader(){
	$(function(){
		  $("#carbon-header").load("head.html", function () {
		    BluemixComponents.LeftNav.init();
		    BluemixComponents.ProfileSwitcher.init();
		  }); 
		});
}

function createHeaderforLogin(){
	
		$('header').append("<nav id='cssmenu'>");	
		$('#cssmenu').append("<div class='logo'><img src='img/Avatar_new.png' height='40px'><a href='/'>Bellosguardo</a></div>");
		$('#cssmenu').append("<div id='head-mobile'></div><div class='button'></div>");
		$('#cssmenu').append("<ul id='begin'>");
		$('#begin').append("<li><a href='doc.html'>docs");
		$('#begin').append("<li><a href='aboutus.html'>about us");	
	
}

function refreshPage(){
    window.location.reload(); // refresh entire page onclick
}



/**
 * @returns Attiva il dropdown e il bottone nel menu e negli eventuali sottomenu su mobile
 */
(function($) {
$.fn.menumaker = function(options) {  
var cssmenu = $(this), settings = $.extend({
format: "dropdown",
sticky: false
}, options);
return this.each(function() {
$(this).find(".button").on('click', function(){
  $(this).toggleClass('menu-opened');
  var mainmenu = $(this).next('ul');
  if (mainmenu.hasClass('open')) { 
    mainmenu.slideToggle().removeClass('open');
  }
  else {
    mainmenu.slideToggle().addClass('open');
    if (settings.format === "dropdown") {
      mainmenu.find('ul').show();
    }
  }
});
cssmenu.find('li ul').parent().addClass('has-sub');
multiTg = function() {
  cssmenu.find(".has-sub").prepend('<span class="submenu-button"></span>');
  cssmenu.find('.submenu-button').on('click', function() {
    $(this).toggleClass('submenu-opened');
    if ($(this).siblings('ul').hasClass('open')) {
      $(this).siblings('ul').removeClass('open').slideToggle();
    }
    else {
      $(this).siblings('ul').addClass('open').slideToggle();
    }
  });
};
if (settings.format === 'multitoggle') multiTg();
else cssmenu.addClass('dropdown');
if (settings.sticky === true) cssmenu.css('position', 'fixed');
resizeFix = function() {
var mediasize = 768;
  if ($( window ).width() > mediasize) {
    cssmenu.find('ul').show();
  }
  if ($(window).width() <= mediasize) {
    cssmenu.find('ul').hide().removeClass('open');
  }
};
resizeFix();
return $(window).on('resize', resizeFix);
});
};
})(jQuery);

(function($){
$(document).ready(function(){
$("#cssmenu").menumaker({
format: "multitoggle"
});
});
})(jQuery);	
