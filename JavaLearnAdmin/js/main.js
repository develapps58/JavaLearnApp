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
    
    
    
    var user = new Users();
    if(!user.IsAuth()) {
        showAuthWindow();
        return ;
    }
    
    showSectionsData();
};

var HistoryOperations = function () {
    var operations = [];
    var self = this;
 
    this.add = function (f, p) {
        var obj = {func: f, params: p};
        operations.push(obj);
    };
    
    this.backEvent = function () {
        //var obj = operations.pop();
        var obj = operations[operations.length-1];
        if(isFunction(obj.func)) {
            obj.func.apply(null, obj.params);
        };
        return obj;
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
    
    authWindow.appendChild(loginField);
    authWindow.appendChild(passwordField);
    authWindow.appendChild(loginButton);
    
    loginButton.onclick = function () {
        var basicAuthInfo = "Basic " +  window.btoa(loginField.value + ":" + passwordField.value );
        var request = prepareRequest(function (response) { resultAuth(response, basicAuthInfo) });
        request.addHeader(["Authorization",  basicAuthInfo ]);
        request.addHeader(['Cache-Control', 'no-cache, no-store, must-revalidate']);
        request.addHeader(['Pragma', 'no-cache']);
        request.addHeader(['Expires', '0']);
        var params = '?where={"login":"'+loginField.value+'"}';
        request.exec('GET', HOST + "users" + params, null);
    };
    
};

var resultAuth = function (response, basicAuthInfo) {
    var responseData = JSON.parse(response);
    if('_error' in responseData) {
        alert("Ошибка авторизации");
        return ;
    }
    else {
        var user = new Users();
        user.SetBasicAuthData(basicAuthInfo);
        
        var loginWindow = document.getElementById('auth-window');
        document.body.removeChild(loginWindow);
        
        showSectionsData();
    }
};

var showSectionsData = function () {
    var tableContainer = document.getElementById('data-table');
    
    var loadSections = new LoadSections(tableContainer);
    loadSections.load();
    
    var historyOp = new HistoryOperations();
    historyOp.add(loadSections.load);
};