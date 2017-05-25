'use strict';

var themeButtons = document.querySelectorAll('.bx--theme-switcher svg');
var lightIcon = document.querySelector('[data-theme="light"]');
var darkIcon = document.querySelector('[data-theme="dark"]');
var body = document.querySelector('body');

function toggleTheme(e) {
  var icon = e.currentTarget;
  var theme = e.currentTarget.dataset.theme;

  if (theme === 'light') {
    darkIcon.classList.remove('bx--theme-switcher__icon--active');
    icon.classList.add('bx--theme-switcher__icon--active');
    body.classList.add('bx--global-light-ui');
  } else {
    lightIcon.classList.remove('bx--theme-switcher__icon--active');
    icon.classList.add('bx--theme-switcher__icon--active');
    body.classList.remove('bx--global-light-ui');
  }
}

if (themeButtons.length > 0) {
  themeButtons.forEach(function (icon) {
    icon.addEventListener('click', toggleTheme);
  });
}
