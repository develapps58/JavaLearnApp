/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
"use strict";

var LoadAnswers= function (container, questionId) {
    var Answers = Model.create('answers', true);
    
    var GetView = function (container) {
        var self = this;
        this.view = new Table(container);
        GetView = function () {
            return self;
        };
    };
    
    var fields = [
        {
            name: 'title',
            title: 'Вариант ответа', 
            content: function () {
                var node = document.createElement('input');
                node.setAttribute('class', 'text');
                node.setAttribute('type', 'text');
                node.setAttribute('id', 'id_title');
                return node;
            }
        },
        {
            name: 'iscorrect',
            title: 'Является правильным ответом', 
            content: function () {
                var node = document.createElement('input');
                node.setAttribute('class', 'text');
                node.setAttribute('type', 'checkbox');
                node.setAttribute('id', 'id_iscorrect');
                return node;
            }
        },
        {
            name: 'roworder',
            title: 'Порядок',
            content: function () {
                var node = document.createElement('input');
                node.setAttribute('class', 'text');
                node.setAttribute('type', 'Number');
                node.setAttribute('id', 'id_order');
                return node;
            }
        }
    ];
    
    var view = new GetView(container);
    
    var questionTitleBehavior = function (item) {
        return function () {
            var node = document.createElement('span');
            var Questions = Model.create('questions', true);
            var question = Questions.findById(questionId);
            node.textContent = question.title;
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };
    
    var isCorrectBehavior  = function (item) {
        return function () {
            var node = document.createElement('span');
            node.textContent = (item.iscorrect) ? 'ДА' : 'Нет';
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };
    
    var createAnswer = function () {
        var node = document.createElement('div');
        node.setAttribute('class', 'operation_btn create_btn');
        
        node.onclick = function () {
            var container = createSaveFields (fields, function () {
                
                var Answers = Model.create('answers', true);
                var answer = Answers.init();
                
                answer.title = document.getElementById('id_title').value;
                answer.iscorrect = document.getElementById('id_iscorrect').checked;
                answer.questionid = questionId;
                answer.roworder = document.getElementById('id_order').value;

                var loadPanel = new LoadPanel();
                loadPanel.show();
                answer.save(function () { alert("Сохранение успешно!"); windowSave.close(); loadPanel.hide(); }, function (response) {alert("В ходе сохранения раздела произошли ошибки: " + response)});
            });
            
            var windowSave = new JLModalWindow("Создание варианта ответа.", container);
            windowSave.show();
        };

        return {
            paint: function () {
                return node;
            }
        };
    };
    var backEvent = function () {
        var node = document.createElement('div');
        node.setAttribute('class', 'operation_btn back_btn');
        node.onclick = function () {
            Answers.removeModel('answers');
            var historyOp = new HistoryOperations();
            historyOp.backEvent();
        };
        return {
            paint: function () {
                return node;
            }
        };
    };

    var editAnswer = function (item) {
        return function () {
            var node = document.createElement('div');
            node.setAttribute('class', 'operation_btn edit_btn');
            node.onclick = function () {
                var current = item;
                var container = createSaveFields (fields, function () {
                    
                    current.title = document.getElementById('id_title').value;
                    current.iscorrect = document.getElementById('id_iscorrect').checked;
                    current.questionid = questionId;
                    current.roworder = document.getElementById('id_order').value;
                    
                    var loadPanel = new LoadPanel();
                    loadPanel.show();
                    current.save(function () { alert("Сохранение успешно!"); windowSave.close(); loadPanel.hide(); }, function (response) {alert("В ходе сохранения раздела произошли ошибки: " + response)});
                }, current);
                var windowSave = new JLModalWindow("Редактирование варианта ответа '" + item.title + "'", container);
                windowSave.show();
            };
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };
    
    var rowOrderBehavior = function () {
        var currentSortOrder = 0;
        
        var node = document.createElement('div');
        node.setAttribute('class', 'sort-header');
        
        var textNode = document.createElement('span');
        textNode.textContent = '№ ответа';
        
        var arrowImg = document.createElement('img');
        arrowImg.width = '20';
        arrowImg.height = '20';
        
        node.appendChild(textNode);
        node.appendChild(arrowImg);
        
        var view = new GetView().view;
        
        node.onclick = function () {
            if(currentSortOrder === 0) {
                currentSortOrder = 1;
                arrowImg.src = 'images/up.png';
                view.sort('roworder', currentSortOrder);
                return ;
            } 
            if(currentSortOrder === 1) {
                arrowImg.src = 'images/down.png';
                currentSortOrder = -1;
            }
            else {
                arrowImg.src = 'images/up.png';
                currentSortOrder = 1;
            }
            view.sort('roworder', currentSortOrder);
        };
        
        return {
            paint: function () {
                return node;
            }
        };
    };
    
    var removeAnswer = function (item) {
        return function () {
            var node = document.createElement('div');
            node.setAttribute('class', 'operation_btn remove_btn');
            node.onclick = function () {
                if(confirm("Удалить запись '" + item.title + "'")) {
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
                if(field.type === 'checkbox') field.checked = item[rules[i].name];
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
    
    this.load = function () {
        if(!Answers.IsLoad) {
            Answers.remote = true;
            Answers.attributes = [ 'title', 'iscorrect', 'questionid', 'roworder' ];
            var rules = {
                roworder: {title: function () {return rowOrderBehavior();}, sort: true, style: {width: '110px', textAlign: 'center'}},
                questionTitle: {events: ['add'], title: 'Вопрос', action: function (item) { return questionTitleBehavior(item); }},
                title: {events: ['add'], title: 'Вариант ответа'},
                isCorrect: {events: ['add'], title: 'Корректный ответ', action: function (item) { return isCorrectBehavior(item);  }},
                __remove: {title: function () { return backEvent(); }, events: ['add', 'onload'], action: function (item) { return removeAnswer(item); }, style: {width: '22px'}},
                __edit: {title: function () { return createAnswer(); }, events: ['add', 'onload'], action: function (item) { return editAnswer(item); }, style: {width: '22px'}}
            };
            var viewModel = new BindingViewModel(view.view, rules);
            viewModel.listenerAdd(Answers.observer, 'add');
            viewModel.listenerChange(Answers.observer, 'change');
            viewModel.listenerRemove(Answers.observer, 'remove');
            
            var loadPanel = new LoadPanel();
            loadPanel.show();
            Answers.loadRemote({questionid: questionId}, function () {loadPanel.hide();});
        }
        else {
            view.view.repaint();
        }
    };
};

