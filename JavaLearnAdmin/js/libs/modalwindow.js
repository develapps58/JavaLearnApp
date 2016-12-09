/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

"use strict";

var modalWindow = function () {
    var defaultParams = {
        width: "86%",
        height: "86%",
        top: "5%",
        left: "6%",
        background: "whitesmoke",
        border: "5px solid silver",
        zIndex: "100",
        closable: true,
        movable: true,
        resizable: true,
        header: true,
        headerStyle: {
            width: "100%",
            height: "40px",
            background: "silver",
            title: "My Window"
        },
        containerStyle: {
            overflowY: 'none',
            height: '100%'
        }
    };

    var window = null; 
    var header = null;
    var container = null;

    var initCallback = function () {};
    var showCallback = function () {};
    var closeCallback = function () {};
    var appendCallback  = function () {};
    var clearCallback = function () {};


    var createHeader = function (params) {
        header = document.createElement('div');
        header.setAttribute('class', 'modal-window-header');
        header.innerHTML = params['title'];
        for(var k in params) {
            header.style[k] = params[k];
        }
        window.appendChild(header);
    };

    var createCloseButton = function () {
        if(header !== null) {
            var closeButton = document.createElement('div');
            closeButton.setAttribute('class', 'modal-window-header-close-button');
            closeButton.innerHTML = "X";
            header.appendChild(closeButton);
            (function (w, c) {
                Event.add(c, 'click', function () {
                    document.body.removeChild(w);
                    setUserSelectHtml('text');
                    closeCallback();
                });
            })(window, closeButton);

        }
    };
    
    var makeMovable = function () {
        if(header === null) {
            return ;
        }
        setUserSelectHtml('none');
        header.style.cursor = 'move';
        (function (h, w) {
            var h = h, w = w;
            var startX, startY, x, y, pressFlag = 0, moveFlag = 0;
            var currZ = parseInt(w.style.zIndex);
            
            Event.add(h, 'mousedown', function (e) {
                e = e || global.event;
                startX = e.pageX;
                startY = e.pageY;
                x = e.pageX - w.offsetLeft;
                y = e.pageY - w.offsetTop;
                pressFlag = 1;
                w.style.zIndex = currZ + 1;
            });
            Event.add(h, 'mousemove', function (e) {
                if(pressFlag === 0) {
                    return ;
                }
                if(Math.abs(startX-e.pageX) > 2 || Math.abs(startY-e.pageY) > 2) {
                    moveFlag = 1;
                }
            });
            
            Event.add(document.body, 'mouseup', function (e) {
                pressFlag = 0;
                moveFlag = 0;
                w.style.zIndex = currZ - 1;
            });
            
            Event.add(document.body, 'mousemove', function (e) {
               if(moveFlag === 0) {
                    return ;
               } 
               e = e || global.event;
               var shiftX = e.pageX - x;
               var shiftY = e.pageY - y;
               w.style.left = shiftX + "px";
               w.style.top = shiftY + "px";
            });
            
        })(header, window);
    };
    
    
    var makeFullScreen = function () {
        (function (w, h) {
            var w = w, h = h;
            Event.add(h, 'dblclick', function () {
                if(w.full_screen === 1) {
                    w.style.width = defaultParams.width;
                    w.style.height = defaultParams.height;
                    w.style.top = defaultParams.top;
                    w.style.left = defaultParams.left;
                    w.full_screen = 0;
                }
                if(w.full_screen === 0) {
                    w.style.width = '100%';
                    w.style.height = '100%';
                    w.style.top = '0';
                    w.style.left = '0';
                    w.full_screen = 1;
                }
            });
        })(window, header);
    };
    
    var setUserSelectHtml = function (value) {
        var html = document.getElementsByTagName('html')[0];
        if(value === 'none') {
            html.style.MozUserSelect = 'none';
            html.style.WebkitUserSelect = 'none';
            html.style.KhtmlUserSelect = 'none';
            html.style.userSelect = 'none';
        }
        else {
            html.style.MozUserSelect = 'text';
            html.style.WebkitUserSelect = 'text';
            html.style.KhtmlUserSelect = 'text';
            html.style.userSelect = 'text';
        }
    };
    
    var createContainer = function () {
        if(container === null || window === null) {
            return ;
        }
        container.setAttribute('class', 'modal-window-container');
        
        for(var k in defaultParams['containerStyle']) {
            container.style[k] = defaultParams['containerStyle'][k];
        }
        var h = 0;
        if(header !== null) {
            h = parseInt(header.style.height);
        }
        container.style.height = window.clientHeight - h + 'px';
        window.appendChild(container);
    };
    
    return {
        init: function (params, callback) {
            window = document.createElement('div');
            window.setAttribute('class', 'modal-window');
            window.full_screen = 0;
            container = document.createElement('div');
                        
            params = params || defaultParams;
            if(params !== defaultParams) {
                for(var k in params) {
                    if(typeof params[k] === 'object') {
                        for(var l in params[k]) {
                            defaultParams[k][l] = params[k][l];
                        }
                    }
                    else {
                        defaultParams[k] = params[k];
                    }
                }
            }
            params = defaultParams;
            for(var k in params) {
                window.style[k] = params[k];
            }
            if(params['header'] === true) {
                createHeader(params['headerStyle']);
            }
            if(params['closable'] === true) {
                createCloseButton();
            }
            if(params['movable'] === true) {
                makeMovable();
            }
            
            //makeFullScreen();
        },
        show: function (callback) {
            document.body.appendChild(window);
            createContainer();
        },
        close: function (callback) {
            document.body.removeChild(window);
            setUserSelectHtml('text');

            if(typeof callback === 'function') {
                closeCallback = callback;
            }
            
        },
        getContainerBaseRect: function () {
            if(container !== null) {
                return container.getBoundingClientRect();
            }
            return 0;
        },
        append: function (content, callback) {
            container.appendChild(content);
        },
        clear: function (callback) {
            var childs = container.childNodes;
            while (childs[0]) {
                container.removeChild(childs[0]);
            }
        }
    };
}


