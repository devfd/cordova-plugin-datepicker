
module.exports = function(context) {
  var Q = context.requireCordovaModule('q');
  var defer = new Q.defer(),
      fs = require('fs'),
      plugDir = context.opts.plugin.dir,
      layoutPath = 'platforms/android/res/layout';

  if(context.opts.plugin.platform !== 'android') {
    defer.reject('Only for android');
    return defer.promise;
  }

  if(fs.existsSync(layoutPath + '/datetime.xml')) {
    fs.unlinkSync(layoutPath + '/datetime.xml');
  }

  defer.resolve();
  console.log('Datetimepicker layout removed');

  return defer.promise;
};
