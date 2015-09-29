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
DatePicker.prototype.show = function(options, cb) {

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
		mode : 'date',
		tz: null,
		date : '',
		minDate: 0,
		maxDate: 0,
    hideNowButton: false,
	};

	for (var key in defaults) {
		if (typeof options[key] !== "undefined") {
			defaults[key] = options[key];
		}
	}

	//this._callback = cb;

	var callback = function(message) {
    console.log('GOT ' +  message);
    var d;

    if(message === 'now') {
      d = new Date();
    }
    else if(message === 'cancel') {
      d = null;
    }
    else {
      d = new Date(parseFloat(message));
    }

		cb(d);
	};

	cordova.exec(callback,
		null,
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
