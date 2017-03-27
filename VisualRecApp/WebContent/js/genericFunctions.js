/*
 * Funzioni dedicate alla personalizzazione degli header delle pagine html
 * 
 * */


//var path = ""; // generalizzazione del percorso delle pagine per il webcontent


/**
 * @returns Restituisce l'head delle pagine: inclusione di utilities, pagine css e titolo tab browser
 */
function createHead(){
	var start = ["<meta http-equiv='X-UA-Compatible' content='IE=edge'><meta name='viewport' content='width=device-width, initial-scale=1.0'><meta name='ROBOTS' content='INDEX,FOLLOW'><meta name='GOOGLEBOT' content='ARCHIVE'><link href='https://fonts.googleapis.com/icon?family=Material+Icons' rel='stylesheet'>"];
	var fon = ["<link rel='stylesheet' type='text/css' href='css/fonts.css'/>"]; //css fonts
	var def = ["<link rel='stylesheet' type='text/css' href='css/default.css'/>"]; //css per le pagine
	var str = ["<link rel='stylesheet' type='text/css' href='css/structure.css'/>"]; //css per le pagine
	var nav = ["<link rel='stylesheet' type='text/css' href='css/navbar.css'/>"]; //css per il menu
	var uti = ["<link rel='stylesheet' type='text/css' href='css/utilities.css'/>"]; //css per parti comuni della pagina
	var title = ["<title>Watson VR Test</title>"];

	var headfull = start.concat(uti, fon, def, str, nav, uti, title);
	$('head').append(headfull);
}


/**
 * @returns Genera il menu, sia per desktop che per mobile
 */
function createHeader(){
	
	$('header').append("<nav id='cssmenu'>");	
		$('#cssmenu').append("<div class='logo'><img src='img/Avatar_new.png' height='40px'><a href='/'>Watson VR ROC Tester</a></div>");
		$('#cssmenu').append("<div id='head-mobile'></div><div class='button'></div>");
		$('#cssmenu').append("<ul id='begin'>");
			$('#begin').append("<li><a href='home.html'>dashboard");
			$('#begin').append("<li><a href='simulate.html'>simulate");
			$('#begin').append("<li><a href='train.html'>train");
			$('#begin').append("<li><a href='doc.html'>docs");
			$('#begin').append("<li><a href='aboutus.html'>about us");	
			$('#begin').append("<li><a href='logoutservlet'>logout");
	
}

function createHeaderforShow(){
	
	$('header').append("<nav id='cssmenu'>");	
		$('#cssmenu').append("<div class='logo'><img src='img/Avatar_new.png' height='40px'><a href='/'>Watson VR ROC Tester</a></div>");
		$('#cssmenu').append("<div id='head-mobile'></div><div class='button'></div>");
		$('#cssmenu').append("<ul id='begin'>");
			$('#begin').append("<li><a href='simulate.html'>go back");

	
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
var mediasize = 700;
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
