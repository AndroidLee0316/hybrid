;(function () {

        PASCJSBridge.statsEvent = function (opts) {
            window.PASCJSBridge._callHandler('PASC.app.webStatsEvent', opts);
            //window.PASCJSBridge.callHandler('PASC.app.webStatsEvent1', opts,"");
        };
        
        var FILTER_TAGS = ['a', 'button', 'li', 'div', 'input', 'form', 'img'];
        
        function getTargetInfo(target, needText) {
            var id = target.id,
            tagName = target.tagName.toLowerCase(),
            className = target.className,
            title = target.title,
            href = target.href,
            src = target.src,
            alt = target.alt,
            parentNode = target.parentNode,
            info = {
            tagName: tagName
            };
            
            if (tagName === 'html' || tagName === 'body') return null;
            if (FILTER_TAGS.indexOf(tagName) == -1 && needText) return parentNode ?  getTargetInfo(parentNode, needText) : null;
            if (parentNode && parentNode.tagName.toLowerCase() !== 'body') info.parentNode = getTargetInfo(parentNode);
            
            if (id) info.id = id;
            if (className) info.className = className;
            if (title) info.title = title;
            if (alt) info.alt = alt;
            if (href) info.href = href;
            if (src) info.src = src;
            if (needText) info.innerText = target.innerText;
            
            return info;
        }
        
        function statsEvent(e) {
            var info = getTargetInfo(e.target, true);
            
            if (!info) return;
            
            var opts = {
            eventId: '第三方页面-事件上报',
            label: document.title,
            map: {
            info: JSON.stringify(info)
            }
            };
            
            console.log(info);
            window.PASCJSBridge.statsEvent(opts);
        }
        
        document.addEventListener('click', statsEvent, true);
        document.addEventListener('submit', statsEvent, true);
        
    })();