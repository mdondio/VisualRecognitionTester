/*
 * Funzioni dedicate alla personalizzazione degli header delle pagine html
 * 
 * */


//var path = ""; // generalizzazione del percorso delle pagine per il webcontent


/**
 * @returns Restituisce l'head delle pagine: inclusione di utilities, pagine css e titolo tab browser
 */
function createHead(){
	var utilities = ["<meta http-equiv='X-UA-Compatible' content='IE=edge'><meta name='viewport' content='width=device-width, initial-scale=1.0'><meta name='ROBOTS' content='INDEX,FOLLOW'><meta name='GOOGLEBOT' content='ARCHIVE'><link href='https://fonts.googleapis.com/icon?family=Material+Icons' rel='stylesheet'>"];
	var css1 = ["<link rel='stylesheet' type='text/css' href='css/default.css'/>"]; //css per le pagine
	var css2 = ["<link rel='stylesheet' type='text/css' href='css/navbar.css'/>"]; //css per il menu
	var css3 = ["<link rel='stylesheet' type='text/css' href='css/utilities.css'/>"]; //css per parti comuni della pagina
	var title = ["<title>Watson VR Test</title>"];

	var headfull = utilities.concat(css1, css2, css3, title);
	$('head').append(headfull);
}


/**
 * @returns Genera il menu, sia per desktop che per mobile
 */
function createHeader(){
	
	var menu = ["<nav id='cssmenu'>"];
	$('header').append(menu);
	
	var logo = ["<div class='logo'><img src='img/Avatar_new.png' height='40px'><a href='/'>Watson VR ROC Tester</a></div>"];
	$('#cssmenu').append(logo);
	
	var button = ["<div id='head-mobile'></div><div class='button'></div>"];
	$('#cssmenu').append(button);
	
	var begin = ["<ul id='begin'>"];
	$('#cssmenu').append(begin);
	
	var l1 = ["<li><a href='/home.html'>dashboard</a></li>"];
	$('#begin').append(l1);
	
	var l2 = ["<li><a href='/simulate.html'>simulate</a></li>"];
	$('#begin').append(l2);
	
	var l3 = ["<li><a href='/train.html'>train</a></li>"];
	$('#begin').append(l3);

	var l4 = ["<li><a href='/docs.html'>docs</a></li>"];
	$('#begin').append(l4);

	var l5 = ["<li><a href='/aboutus.html'>about us</a></li>"];
	$('#begin').append(l5);	
	
	var l6 = ["<li><a href='/logout.html'><img src='ico/power_32.png'></a></li>"];
	$('#begin').append(l6);
	
}


function refreshPage(){
    window.location.reload(); // refresh entire page onclick
}







/**
 * @returns Attiva il dropdown w il bottone nel menu e negli eventuali sottomenu su mobile
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
