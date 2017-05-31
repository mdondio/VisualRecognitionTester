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
	var css_nav = ["<link rel='stylesheet' type='text/css' href='css/navbar.css'/>"]; //css per il menu
	var css_uti = ["<link rel='stylesheet' type='text/css' href='css/utilities.css'/>"]; //css per parti comuni della pagina
	var css_swa = ["<link rel='stylesheet' type='text/css' href='css/sweetalert2_mine.css'/>"]; //css per sweetalert2
	var css_dro = ["<link rel='stylesheet' type='text/css' href='css/dropzone.css'/>"]; //css per sweetalert2
	var js_form = ["<script src='//cdnjs.cloudflare.com/ajax/libs/jquery-form-validator/2.3.26/jquery.form-validator.min.js'>"];
	var js_save = ["<script src='js/FileSaver.min.js'>"];
	var js_swal = ["<script src='js/sweetalert2_mine.js'>"];
	var js_drop = ["<script src='js/dropzone.js'>"];
	var js_lazy = ["<script src='js/jquery.lazy.min.js'>"]; //not working at the moment!
	
	var title = ["<title>Bellosguardo</title>"];

	var headfull = met_sta.concat(css_car1, css_car2, css_fon, css_def, css_str, css_nav, css_uti, css_swa, css_dro, js_form, js_save, js_swal, js_drop, title);
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

function createCarbonHeader(){
	$(function(){
	      $("#carbon-header").load("head.html"); 
	    });
	
	$.get('head.html', function(result){
	    $result = $(result);
	    $result.find('script').appendTo('#carbon-header');
	}, 'html');
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
