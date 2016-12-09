/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var isUndefined = function (obj) {
    if(obj === undefined || obj === 'undefined') {
        return true;
    }
    return false;
};

var isUndefinedOrNull = function (obj) {
    if(isUndefined(obj)) {
        return true;
    };
    if(obj === null) {
        return true;
    }
    return false;
};

var isFunction = function (obj) {
    var getType = {};
    return obj && getType.toString.call(obj) === '[object Function]';
};

var isDOMNode = function (item) {
    if(isUndefinedOrNull(item)) return false;
    if(item.nodeName !== 'undefined' && item.nodeName !== undefined) {
        return true;
    }
    return false;
};

var getElementsByClassName = function (className, item) {
    var retnode = []; 
    var myclass = new RegExp('\\b' + className + '\\b'); 
    var elem = item.getElementsByTagName('*'); 
    for (var i = 0; i < elem.length; i++) { 
       var classes = elem[i].className; 
       if (myclass.test(classes)) { 
          retnode.push(elem[i]); 
       } 
    } 
    return retnode; 
};

var copyParams = function (o1, o2) {
    for(var k in o1) o2[k] = o1[k];
    return o2;
};

var fix_iOSClickFromHover = function () {
    var fixedItems = document.getElementsByClassName('fix_ios_click');
    for(var i = 0, n = fixedItems.length; i < n; i++) {
        (function (j) {
            var item = fixedItems[j];
            Event.add(item, 'click', function () {});
        })(i);
    }
};

function touchHandler(event, target) {
    var touch = event.changedTouches[0];
    if(touch.target === target) {
        var simulatedEvent = document.createEvent("MouseEvent");
            simulatedEvent.initMouseEvent({
            touchstart: "mousedown",
            touchmove: "mousemove",
            touchend: "mouseup"
        }[event.type], true, true, window, 1,
            touch.screenX, touch.screenY,
            touch.clientX, touch.clientY, false,
            false, false, false, 0, null);


        touch.target.dispatchEvent(simulatedEvent);
        event.preventDefault();
    }
}

function initTouchEvent(target) {
    document.addEventListener("touchstart", function (e) {
        touchHandler(e, target);
    }, true);
    document.addEventListener("touchmove", function (e) {
        touchHandler(e, target);
    }, true);
    document.addEventListener("touchend", function (e) {
        touchHandler(e, target);
    }, true);
    document.addEventListener("touchcancel", function (e) {
        touchHandler(e, target);
    }, true);
}

var xmlhttp = function () {
    if(typeof XMLHttpRequest !== 'undefined') {
        return new XMLHttpRequest();
    }
    try {
        return new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch (e) {
        try {
            return new ActiveXObject("Microsoft.XMLHTTP");
        }
        catch (e) {
            return null;
        }
    }
};

var preparePOSTBody = function (data) {
    var boundary = String(Math.random()).slice(2);
    var boundaryMiddle = '--' + boundary + '\r\n';
    var boundaryLast = '--' + boundary + '--\r\n';

    var body = ['\r\n'];
    for (var key in data) {
        body.push('Content-Disposition: form-data; name="' + key + '"\r\n\r\n' + data[key] + '\r\n');
    }
    body = body.join(boundaryMiddle) + boundaryLast;
    
    return [boundary, body];
};

var HTTPRequest = function () 
{
    var request = xmlhttp();
    if(request === null) return;    
    var headers = [];
    
    var addHeader = function (header)
    {
        headers.push(header);
    };
    
    var setHeaders = function (_headers)
    {
        headers = _headers;
    };
    
    var success, error;
    
    var onsuccess = function (f) {
        success = f;
    };
    var onerror = function (f) {
        error = f;
    };
    
    
    var exec  = function (method, url, data) 
    {
        request.open(method, url, true);
        for(var i = 0, n = headers.length; i < n; i++)
        {
            request.setRequestHeader(headers[i][0], headers[i][1]);
        }
        request.onreadystatechange = function () {
            if(request.readyState === 4) {
                try {
                    if(isFunction(success)) {
                        success(request.responseText, request.status);
                    }
                }
                catch (e) {
                    if(isFunction(error)) {
                        error(e);
                    };
                }
            }
        };
        data = data || {};
        request.send(JSON.stringify(data));
    };
    
    return {
        addHeader: addHeader,
        setHeaders: setHeaders,
        onsuccess: onsuccess,
        onerror: onerror,
        exec: exec
    };
    
    //var bodyData = preparePOSTBody(data);
    //request.setRequestHeader('Content-Type', 'multipart/form-data; boundary=' + bodyData[0]);
};

var prepareRequest = function (success, error) {
    var request = new HTTPRequest();
    
    request.addHeader(['Accept', 'application/json']);
    request.addHeader(['Content-Type', 'application/json; charset=utf-8']);
    /*request.addHeader(['Cache-Control', 'no-cache, no-store, must-revalidate']);
    request.addHeader(['Pragma', 'no-cache']);
    request.addHeader(['Expires', '0']);*/
    
    request.onsuccess(success);
    request.onerror(error);
    
    return request;
};