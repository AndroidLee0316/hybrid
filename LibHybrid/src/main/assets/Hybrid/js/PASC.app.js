(function (root, factory) {
  if (typeof define === 'function' && define.amd) {
    define(function() {
      return factory(root);
    });
  } else if (typeof exports === 'object') {
    module.exports = factory;
  } else {
    root.PASC = factory(root);
  }
})(this, function (root) {

  'use strict';

  var PASC = {
    app:{}
  };

  // var callback = function () {};

  function setupWebViewJavascriptBridge(callback) {
     console.log("setupWebViewJavascriptBridge",callback);
     if (window.PASCWebViewBridge) { return callback(window.PASCWebViewBridge); }
     if (window.PAJBCallbacks) { return window.PAJBCallbacks.push(callback); }
     window.PAJBCallbacks = [callback];
     var WVJBIframe = document.createElement('iframe');
     WVJBIframe.style.display = 'none';
     WVJBIframe.src = 'pasc://__REQUEST__BRIDGE__INJECT__';
     document.documentElement.appendChild(WVJBIframe);
     setTimeout(function() { document.documentElement.removeChild(WVJBIframe) }, 0)

  }

  //todo
  function setApiRouter(str) {
      var ifr = document.createElement("iframe");
      ifr.src = str; 
      ifr.style.display = "none"; 
      document.body.appendChild(ifr);
      window.setTimeout(function(){
        document.body.removeChild(ifr);
      },1*1000);
  }

  PASC.app.isSupportApi = function () {
    var ua = navigator.userAgent;
    var opts = {
        version: ua.toLocaleLowerCase().split('version:')[1]
    };
    if(opts && opts.version && opts.version >= '1.5.0') {
        return true;
    } else {
        return false;
    }
  } 

  PASC.app.share = function (opts, success, error) {
    opts = opts || {};
    if(window.smtinterface && window.smtinterface.share){
         window.smtinterface.share(JSON.stringify(opts));
    } else {
        setupWebViewJavascriptBridge(function(bridge) {

            bridge.callHandler('PASC.app.share',opts,function(resp){
                var result;
                try{
                    result = JSON.parse(resp);
                    if(result.code === 0) {
                        success(result);
                    } else {
                        error(result);
                    }
                } catch(err) {
                     error(err);
                }
            });
        });
    }
  }

  PASC.app.toast = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.toast',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  }

  PASC.app.getDeviceInfo = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.getDeviceInfo',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  }
 
  PASC.app.setToolBar = function (opts, success, error) {
    opts = opts || {};
    if(window.smtinterface && window.smtinterface.onTitleChange){
         window.smtinterface.onTitleChange(JSON.stringify(opts.title));
    } else {
        setupWebViewJavascriptBridge(function(bridge) {
            bridge.callHandler('PASC.app.setToolBar',opts,function(resp){
                var result;
                try{
                    result = JSON.parse(resp);
                    if(result.code === 0) {
                        success(result);
                    } else {
                        error(result);
                    }
                } catch(err) {
                     error(err);
                }
            });

            if (opts.leftBtns) {
                var leftBtns = opts.leftBtns;
                for (var i = 0; i < leftBtns.length; i ++) {
                    var leftBtn = leftBtns[i];
                    if (leftBtn.action) {
                        var action = leftBtn.action;
                        bridge.registerHandler(action, function(data,responseCallback){
                            if (leftBtn.callback) {
                                leftBtn.callback();
                            }
                            // responseCallback("button js callback");
                        });
                    }
                }
            }

            if (opts.rightBtns) {
                var rightBtns = opts.rightBtns;

                for (var i = 0; i < rightBtns.length; i ++) {
                    var rightBtn = rightBtns[i];
                    if (rightBtn.action) {
                        var action = rightBtn.action;
                        bridge.registerHandler(action, function(data,responseCallback){
                            if (rightBtn.callback) {
                                rightBtn.callback();
                            }
                            // responseCallback("button js callback");
                        });
                    }
                }
            }
        });
    }
  }

  PASC.app.log = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.log',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  }

  PASC.app.openNewWebView = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.openNewWebView',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  } 

  PASC.app.close = function (opts, success, error) {
    opts = opts || {};
    if(!PASC.app.isSupportApi()){
      setApiRouter('smt://app/close');
    } else {
      setupWebViewJavascriptBridge(function(bridge) {
          bridge.callHandler('PASC.app.close',opts,function(resp){
              var result;
              try{
                  result = JSON.parse(resp);
                  if(result.code === 0) {
                      success(result);
                  } else {
                      error(result);
                  }
              } catch(err) {
                   error(err);
              }
          });
      });
    }
  }

  PASC.app.closeWithBackHome = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.closeWithBackHome',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  }

  PASC.app.selectContact = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.selectContact',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  }

  PASC.app.getGpsInfo = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.getGpsInfo',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  }

  PASC.app.mapNavigation = function (opts, success, error) {
    opts = opts || {};
    if(!PASC.app.isSupportApi()){
        setApiRouter('smt://szyx/map?location');
    } else {
        setupWebViewJavascriptBridge(function(bridge) {
            bridge.callHandler('PASC.app.mapNavigation',opts,function(resp){
                var result;
                try{
                    result = JSON.parse(resp);
                    if(result.code === 0) {
                        success(result);
                    } else {
                        error(result);
                    }
                } catch(err) {
                     error(err);
                }
            });
        });
    }
  }

  PASC.app.webStatsPage = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.webStatsPage',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  }

  PASC.app.webStatsEvent = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.webStatsEvent',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  }

  PASC.app.browseFile = function (opts, success, error) {
      opts = opts || {};
      setupWebViewJavascriptBridge(function(bridge) {
          bridge.callHandler('PASC.app.browseFile',opts,function(resp){
              var result;
              try{
                  result = JSON.parse(resp);
                  if(result.code === 0) {
                      success(result);
                  } else {
                      error(result);
                  }
              } catch(err) {
                   error(err);
              }
          });
      });
    }

      PASC.app.callPhone = function (opts, success, error) {
          opts = opts || {};
          setupWebViewJavascriptBridge(function(bridge) {
              bridge.callHandler('PASC.app.callPhone',opts,function(resp){
                  var result;
                  try{
                      result = JSON.parse(resp);
                      if(result.code === 0) {
                          success(result);
                      } else {
                          error(result);
                      }
                  } catch(err) {
                       error(err);
                  }
              });
          });
        }

  PASC.app.openQRCode = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.openQRCode',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  }

  PASC.app.memoryCache = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.memoryCache',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  }

  PASC.app.diskCache = function (opts, success, error) {
    opts = opts || {};
    setupWebViewJavascriptBridge(function(bridge) {
        bridge.callHandler('PASC.app.diskCache',opts,function(resp){
            var result;
            try{
                result = JSON.parse(resp);
                if(result.code === 0) {
                    success(result);
                } else {
                    error(result);
                }
            } catch(err) {
                 error(err);
            }
        });
    });
  }

  PASC.app.nativeRoute = function (opts, success, error) {
    opts = opts || {};
    if(!PASC.app.isSupportApi()){
        if(window.smtinterface && window.smtinterface.onSearchHealthCenter) {
            window.smtinterface.onSearchHealthCenter();
        }
        if(opts && opts.path) {
            window.location.href = opts.path;
        }
    } else {
        setupWebViewJavascriptBridge(function(bridge) {
            bridge.callHandler('PASC.app.nativeRoute',opts,function(resp){
                var result;
                try{
                    result = JSON.parse(resp);
                    if(result.code === 0) {
                        success(result);
                    } else {
                        error(result);
                    }
                } catch(err) {
                     error(err);
                }
            });
        });
    }
  }

  PASC.app.getUserInfo = function (opts, success, error) {
    opts = opts || {};
    if(!PASC.app.isSupportApi()){
         setApiRouter('smt://getUserInfo');
    } else {
        setupWebViewJavascriptBridge(function(bridge) {
            bridge.callHandler('PASC.app.getUserInfo',opts,function(resp){
                var result;
                try{
                    result = JSON.parse(resp);
                    if(result.code === 0) {
                        success(result);
                    } else {
                        error(result);
                    }
                } catch(err) {
                     error(err);
                }
            });
        });
    }
  }

  PASC.app.goback = function (opts, success, error) {
    opts = opts || {};
    if(!PASC.app.isSupportApi()){
         setApiRouter('smt://app/close');
    } else {
        setupWebViewJavascriptBridge(function(bridge) {
            bridge.callHandler('PASC.app.goback',opts,function(resp){
                var result;
                try{
                    result = JSON.parse(resp);
                    if(result.code === 0) {
                        success(result);
                    } else {
                        error(result);
                    }
                } catch(err) {
                     error(err);
                }
            });
        });
    }
  }

  return PASC;

});