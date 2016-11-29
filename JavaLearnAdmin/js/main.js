/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
"use strict";

//SETTINGS
var HOST = 'http://pi3.duckdns.org/';

window.onload = function ()
{
    var storage = localStorage;
    
    CKEDITOR.config.protectedSource.push( /<script[\s\S]*?script>/g ); /* script tags */
    CKEDITOR.config.allowedContent = true; /* all tags */
    
    CKEDITOR.config.resize_minWidth = '100%';
    CKEDITOR.config.resize_maxWidth = '100%';
    CKEDITOR.config.width = '100%';
    
    //CKEDITOR OPEN FILE BUTTON INIT
    CKEDITOR.on('dialogDefinition', function (ev) {
        var dialogName = ev.data.name;
        var dialogDefinition = ev.data.definition;
        if (dialogName === 'image') {
            var infoTab = dialogDefinition.getContents('info');
            infoTab.remove('browse');
            infoTab.remove('htmlPreview');
            infoTab.remove('cmbAlign');
            infoTab.remove('ratioLock');
            
            infoTab.add({
                type : 'button',
                label : 'Выбрать файл',
                id : 'ck_file',
                default : 'ck_file',

                onClick: function (v) {
                    /*var dialog = CKEDITOR.dialog.getCurrent();
                    ActiveManager.view.show(v.sender, function () {
                        dialog.setValueOf('info','txtUrl',  v.sender.value);
                    }, 'content');*/
                }
                
            });
        }
    });
    
    
    
    var user = new User();
    if(!isUndefinedOrNull(storage.getItem('authdata'))) {
        auth(storage.getItem('authdata'));
    }
    else if(!user.IsAuth()) {
        showAuthWindow();
    }
};

var HistoryOperations = function () {
    var operations = [];
    var self = this;
 
    this.add = function (f, p) {
        var obj = {func: f, params: p};
        operations.push(obj);
    };
    
    this.backEvent = function () {
        var obj = operations[operations.length-2];
        if(!isUndefinedOrNull(obj) && isFunction(obj.func)) {
            obj.func.apply(null, obj.params);
        };
        if(operations.length > 1) {
            operations.pop();
        }
    };
 
    HistoryOperations = function () {
        return self;
    };
};

var showAuthWindow = function () {
    
    var authWindow = document.createElement('div');
    authWindow.setAttribute('id', 'auth-window');
    
    document.body.appendChild(authWindow);
    
    var loginField = document.createElement('input');
    loginField.setAttribute('placeholder', 'Ваш логин');
    loginField.setAttribute('type', 'text');

    var passwordField = document.createElement('input');
    passwordField.setAttribute('placeholder', 'Ваш проль');
    passwordField.setAttribute('type', 'password');
    
    var loginButton = document.createElement('button');
    loginButton.textContent = 'Log in!';
    
    var isSave = document.createElement('input');
    isSave.setAttribute('type', 'checkbox');
    isSave.setAttribute('id', 'is_save_checkbox');
    
    var labelIsSave = document.createElement('label');
    labelIsSave.setAttribute('id', 'is_save_label');
    labelIsSave.textContent = 'Запомнить пароль?';
    
    
    authWindow.appendChild(loginField);
    authWindow.appendChild(passwordField);
    authWindow.appendChild(loginButton);
    authWindow.appendChild(isSave);
    authWindow.appendChild(labelIsSave);
    
    loginButton.onclick = function () {
        var basicAuthInfo = window.btoa(loginField.value + ":" + passwordField.value );
        auth(basicAuthInfo);
    };
    
};

var auth = function (basicAuthInfo) {
    var l_p = window.atob(basicAuthInfo).split(':');
    var request = prepareRequest(function (response) { resultAuth(response, basicAuthInfo); });
    request.addHeader(["Authorization", "Basic " + basicAuthInfo ]);
    request.addHeader(['Cache-Control', 'no-cache, no-store, must-revalidate']);
    request.addHeader(['Pragma', 'no-cache']);
    request.addHeader(['Expires', '0']);
    var params = '?where={"login":"'+l_p[0]+'"}';
    request.exec('GET', HOST + "users" + params, null);
};

var resultAuth = function (response, basicAuthInfo) {
    var responseData = JSON.parse(response);
    if('_error' in responseData) {
        alert("Ошибка авторизации");
        return ;
    }
    else {
        var user = new User();
        user.SetBasicAuthData(basicAuthInfo);

        var savePassword = document.getElementById('is_save_checkbox');
        
        if(!isUndefinedOrNull(savePassword)) {
            var storage = localStorage;
            if(savePassword.checked) {
                storage.setItem('authdata', basicAuthInfo);
            } 
            else {
                if(!isUndefinedOrNull(storage.getItem('authdata'))) {
                    storage.removeItem('authdata');
                }
            }
        }
        
        var loginWindow = document.getElementById('auth-window');
        if(!isUndefinedOrNull(loginWindow))
            document.body.removeChild(loginWindow);

        showToolBar();
        showSectionsData();
    }
};

var showToolBar = function () {
   
    var toolbar = document.getElementById('toolbar');
    toolbar.style.display = 'block';
    
    var logout = document.getElementById('logout');
    var setupdate = document.getElementById('set_update');
    var users = document.getElementById('users');
    
    logout.onclick = function () {
        var storage = localStorage;
        if(!isUndefinedOrNull(storage.getItem('authdata'))) {
            storage.removeItem('authdata');
        }
        document.location.href = '/';
    };
    setupdate.onclick = function () {
        var request = prepareRequest(
                function (response) { alert("Обновление зафиксировано!"); },
                function (response) {  alert("В ходе выполнения операции произошли ошибки!\n" + response); });
        var user = new User();
        if(user.GetAuthInfo()) {
            request.addHeader(["Authorization",  user.GetAuthInfo()]);
        }
        var changes = {guid: ''};
        request.exec('POST', HOST + 'changes', changes);
    };
    users.onclick = function () {
        var tableContainer = document.getElementById('data-table');

        var loadUser = new LoadUsers(tableContainer);
        loadUser.load();

        var historyOp = new HistoryOperations();
        historyOp.add(loadUser.load);
    };
};

var showSectionsData = function () {
    var tableContainer = document.getElementById('data-table');
    
    var loadSections = new LoadSections(tableContainer);
    loadSections.load();
    
    var historyOp = new HistoryOperations();
    historyOp.add(loadSections.load);
};

var LoadPanel = function () {
    var loadPanel = document.getElementById('loading-panel');
    var self = this;
    this.show = function () {
        loadPanel.style.display = 'block';
    };
    this.hide = function () {
        loadPanel.style.display = 'none';
    };
    LoadPanel = function () {
        return self;
    };
};