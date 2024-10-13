;(function () {
    if (window.PASCJSBridge) {
        return;
    }

    var PASCJSBridge = {};

    PASCJSBridge._setupJSBridge = function _setupJSBridge(callback) {
        if (window.PASCWebViewBridge) {
            callback && callback(window.PASCWebViewBridge);
            return;
        }
        if (window.PAJBCallbacks) {
            callback && window.PAJBCallbacks.push(callback);
            return;
        }
        if (callback) {
            window.PAJBCallbacks = [callback];
        }
        var WVJBIframe = document.createElement('iframe');
        WVJBIframe.style.display = 'none';
        WVJBIframe.src = 'pasc://__REQUEST__BRIDGE__INJECT__';
        document.documentElement.appendChild(WVJBIframe);
        setTimeout(function () {
            document.documentElement.removeChild(WVJBIframe)
        }, 0);
    };

    PASCJSBridge._getJSBridge = function _getJSBridge(callback) {
        if (window.PASCJSBridge._isSMT()) {
            var failedTimoutChecker = setTimeout(function () {
                failedTimoutChecker = null;
                callback && callback(null);
                callback = null;
            }, 300);
            window.PASCJSBridge._setupJSBridge(function (bridge) {
                failedTimoutChecker && clearTimeout(failedTimoutChecker);
                callback && callback(bridge);
                callback = null;
            });
        } else {
            callback && callback(null);
        }
    };

    PASCJSBridge._isSMT = function _isSMT() {
        var ua = navigator.userAgent.toLocaleLowerCase();
        return ua.indexOf('smt') >= 0;
    };

    PASCJSBridge._callHandler = function _callHandler(handler, opts, callback) {
        opts = opts || {};
        window.PASCJSBridge._getJSBridge(function (bridge) {
            if (bridge) {
                bridge.callHandler(handler, opts, function (resp) {
                    var result = JSON.parse(resp);
                    callback && callback(result);
                    if (result.code === 0) {
                        opts.success && opts.success(result.data);
                    }
                    else {
                        opts.fail && opts.fail(result);
                    }
                });
            }
        });
    };

    PASCJSBridge.close = function () {
        window.PASCJSBridge._callHandler('PASC.app.close');
    };

    PASCJSBridge.goback = function () {
        window.PASCJSBridge._callHandler('PASC.app.goback');
    };

    PASCJSBridge.share = function (opts) {
        opts.shareTypes = [
            {platformID: 1},
            {platformID: 2},
            {platformID: 3},
            {platformID: 4},
            {platformID: 0},
        ];
        window.PASCJSBridge._callHandler('PASC.app.share', opts);
    };

    PASCJSBridge.statsEvent = function (opts) {
        window.PASCJSBridge._callHandler('PASC.app.webStatsEvent', opts);
    }

    window.PASCJSBridge = PASCJSBridge;

    //简单调用
    window.pasc = {

        close: function () {
            window.PASCJSBridge.close();
        },

        goback: function () {
            window.PASCJSBridge.goback();
        },

        share: function (opts) {
            window.PASCJSBridge.share(opts);
        },

    };

    //兼容
    if (window.paAccountApp) {
        window.paAccountApp.goBack = function () {
            window.PASCJSBridge.close();
        }
    }
    else {
        window.paAccountApp = {
            goBack: function () {
                window.PASCJSBridge.close();
            }
        };
    }

    if (window.client) {
        window.client.goBack = function () {
            window.PASCJSBridge.close();
        }
    }
    else {
        window.client = {
            goBack: function () {
                window.PASCJSBridge.close();
            }
        };
    }

    if (window.smtinterface) {
        window.smtinterface.goBack = function () {
            window.PASCJSBridge.close();
        }
    }
    else {
        window.smtinterface = {
            goBack: function () {
                window.PASCJSBridge.close();
            }
        };
    }

})();























