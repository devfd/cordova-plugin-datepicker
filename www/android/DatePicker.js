/**
 * Phonegap DatePicker Plugin Copyright (c) Greg Allen 2011 MIT Licensed
 * Reused and ported to Android plugin by Daniel van 't Oever
 */

/**
 * Constructor
 */
function DatePicker() {
  //this._callback;
}

/**
 * show - true to show the ad, false to hide the ad
 */
DatePicker.prototype.show = function(options, cb, errCb) {

  if (options.date) {
    options.date = (options.date.getMonth() + 1) + "/" +
             (options.date.getDate()) + "/" +
             (options.date.getFullYear()) + "/" +
             (options.date.getHours()) + "/" +
             (options.date.getMinutes());
  }

  if(options.minDate) {
    options.minDate = options.minDate.getTime();
  }

  if(options.maxDate) {
    options.maxDate = options.maxDate.getTime();
  }

  var defaults = {
    mode : 'datetime',
    tz: null,
    date : '',
    minDate: null,
    maxDate: null,
    hideNowButton: false,
    is24Hour: true,
    androidTheme: 5,
    okText: 'Ok',
    cancelText: 'Cancel',
  };

  for (var key in defaults) {
    if (typeof options[key] !== "undefined") {
      defaults[key] = options[key];
    }
  }

  //this._callback = cb;

  var callback = function(message) {
    if(message != 'error') {
      var timestamp = Date.parse(message);
      if(isNaN(timestamp) == false) {
        cb(new Date(message));
      }
    }
    else {
      cb(null);
    }
  }

  var errCallback = function(message) {
    if (typeof errCb === 'function') {
      errCb(message);
    }
  }

  cordova.exec(callback,
    errCallback,
    "DatePickerPlugin",
    defaults.mode,
    [defaults]
  );
};

var datePicker = new DatePicker();
module.exports = datePicker;

// Make plugin work under window.plugins
if (!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.datePicker) {
    window.plugins.datePicker = datePicker;
}
