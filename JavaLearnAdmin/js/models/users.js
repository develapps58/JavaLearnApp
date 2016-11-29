/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
"use strict";

var LoadUsers = function (container) {
 
    var Users = Model.create('users', true);
    
    var GetView = function (container) {
        var self = this;
        this.view = new Table(container);
        GetView = function () {
            return self;
        };
    };
    
    
    var fields = [
        {
            name: 'full_name',
            title: 'ФИО', 
            content: function () {
                var node = document.createElement('input');
                node.setAttribute('class', 'text');
                node.setAttribute('type', 'text');
                node.setAttribute('id', 'id_full_name');
                return node;
            }
        },
        {
            name: 'login',
            title: 'Логин',
            content: function () {
                var node = document.createElement('input');
                node.setAttribute('class', 'text');
                node.setAttribute('type', 'text');
                node.setAttribute('id', 'id_login');
                return node;
            }
        },
        {
            name: 'password',
            title: 'Пароль',
            content: function () {
                var node = document.createElement('input');
                node.setAttribute('class', 'text');
                node.setAttribute('type', 'text');
                node.setAttribute('id', 'id_password');
                return node;
            }
        }
    ];
    
    var view = new GetView(container);
    view.view.clear();
    
    var createUser = function () {
        var node = document.createElement('div');
        node.setAttribute('class', 'operation_btn create_btn');
        
        node.onclick = function () {
            var container = createSaveFields (fields, function () {
                
                var Users = Model.create('users', true);
                var user = Users.init();
                
                user.full_name = document.getElementById('id_full_name').value;
                user.login = document.getElementById('id_login').value;
                user.password = document.getElementById('id_password').value;
                user.issystem = false;
                
                var loadPanel = new LoadPanel();
                loadPanel.show();
                user.save(
                        function () { alert("Сохранение успешно!"); windowSave.close(); loadPanel.hide(); },
                        function (response) {alert("В ходе сохранения раздела произошли ошибки: " + response); loadPanel.hide();});
            });
            
            var windowSave = new JLModalWindow("Создание пользователя.", container);
            windowSave.show();
        };

        return {
            paint: function () {
                return node;
            }
        };
    };

    var updateUser = function (item) {
        return function () {
            var node = document.createElement('div');
            node.setAttribute('class', 'operation_btn edit_btn');
            node.onclick = function () {
                var current = item;
                var container = createSaveFields (fields, function () {
                    
                    current.full_name = document.getElementById('id_full_name').value;
                    current.login = document.getElementById('id_login').value;
                    
                    var loadPanel = new LoadPanel();
                    loadPanel.show();
                    current.save(function () { alert("Сохранение успешно!"); windowSave.close(); loadPanel.hide(); }, function (response) {alert("В ходе сохранения раздела произошли ошибки: " + response)});
                }, current);
                var windowSave = new JLModalWindow("Редактирование пользователя '" + item.login + "'", container);
                windowSave.show();
            };
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };

    var removeUser = function (item) {
        return function () {
            var node = document.createElement('div');
            node.setAttribute('class', 'operation_btn remove_btn');
            node.onclick = function () {
                if(confirm("Удалить пользователя '" + item.full_name + "'")) {
                    item.remove();
                }
            };
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };

    var createSaveFields = function (rules, savecallback, item) {
        var container = document.createElement('div');
        container.setAttribute('class', 'save-fields-container');
        
        var table = document.createElement('table');
        for(var i = 0, n = rules.length; i < n; i++) {
            var tr = document.createElement('tr')
            var td_name = document.createElement('td');
            var td_content = document.createElement('td');
            td_name.textContent = rules[i]['title'];
            
            var field = rules[i]['content']();
            if(!isUndefinedOrNull(item) && (rules[i].name in item)) {
                field.value = item[rules[i].name];
            }
            
            td_content.appendChild(field);
            tr.appendChild(td_name);
            tr.appendChild(td_content);
            table.appendChild(tr);
        }
        container.appendChild(table);
        var saveButton = document.createElement('button');
        saveButton.setAttribute('class', 'save-button');
        saveButton.textContent = 'Сохранить';
        container.appendChild(saveButton);
        saveButton.onclick = function () {
            savecallback();
        };

        return container;
    };
    
    var backEvent = function () {
        var node = document.createElement('div');
        node.setAttribute('class', 'operation_btn back_btn');
        node.onclick = function () {
            Users.removeModel('users');
            var historyOp = new HistoryOperations();
            historyOp.backEvent();
        };
        return {
            paint: function () {
                return node;
            }
        };
    };
    
    var issystemBehavior = function (item) {
        return function () {
            var node = document.createElement('span');
            node.textContent = (item.issystem) ? 'ДА' : 'Нет';
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };
    
    this.load = function () {
        if(!Users.IsLoad) {
            Users.remote = true;
            Users.attributes = [ 'full_name', 'login', 'issystem', 'password' ];
            var rules = {
                full_name: {title: 'Имя пользователя'},
                login: {title: 'Логин пользователя'},
                __issystem: {events: ['add'], title: 'Системный', action: function (item) { return issystemBehavior(item); }, style: {width: '40px'}},
                __remove: {title: function () { return backEvent(); }, events: ['add'], action: function (item) { return removeUser(item); }, style: {width: '22px'}},
                __edit: {
                    title: function () { return createUser(); },
                    events: ['add'],
                    action: function (item) {
                        return updateUser(item);
                    },
                    style: {width: '22px'}
                }
            };
            var viewModel = new BindingViewModel(view.view, rules);

            viewModel.listenerAdd(Users.observer, 'add');
            viewModel.listenerChange(Users.observer, 'change');
            viewModel.listenerRemove(Users.observer, 'remove');
            
            var loadPanel = new LoadPanel();
            loadPanel.show();
            Users.loadRemote({}, function () {loadPanel.hide();});
        }
        else {
            view.view.repaint();
        }
    };
};