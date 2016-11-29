/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
"use strict";

var LoadQuestions = function (container, sectionId) {
    var Questions = Model.create('questions', true);
    
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
            title: 'Вопрос', 
            content: function () {
                var node = document.createElement('input');
                node.setAttribute('class', 'text');
                node.setAttribute('type', 'text');
                node.setAttribute('id', 'id_title');
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
    
    var answerLink = function (item) {
        return function () {
            var node = document.createElement('span');
            node.textContent = '[Перейти к ответам]';
            node.setAttribute('class', 'data-link');
            
            node.onclick = function () {
                view.view.clear();
                var loadAnswers = new LoadAnswers(document.getElementById('data-table'), item._id);
                loadAnswers.load();
                
                var historyOp = new HistoryOperations();
                historyOp.add(loadAnswers.load);
            };
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };    
    
    var sectionTitleBehavior = function (item) {
        return function () {
            var node = document.createElement('span');
            var Sections = Model.create('sections', true);
            var section = Sections.findById(sectionId);
            node.textContent = section.title;
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };
    
    var createQuestion = function () {
        var node = document.createElement('div');
        node.setAttribute('class', 'operation_btn create_btn');
        
        node.onclick = function () {
            var container = createSaveFields (fields, function () {
                
                var Questions = Model.create('questions', true);
                var question = Questions.init();
                
                question.title = document.getElementById('id_title').value;
                question.sectionid = sectionId;
                question.roworder = document.getElementById('id_order').value;
                
                var loadPanel = new LoadPanel();
                loadPanel.show();
                question.save(function () { alert("Сохранение успешно!"); windowSave.close(); loadPanel.hide(); }, function (response) {alert("В ходе сохранения раздела произошли ошибки: " + response)});
            });
            
            var windowSave = new JLModalWindow("Создание вопроса.", container);
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
            Questions.removeModel('questions');
            var historyOp = new HistoryOperations();
            historyOp.backEvent();
        };
        return {
            paint: function () {
                return node;
            }
        };
    };

    var editQuestion = function (item) {
        return function () {
            var node = document.createElement('div');
            node.setAttribute('class', 'operation_btn edit_btn');
            node.onclick = function () {
                var current = item;
                var container = createSaveFields (fields, function () {
                    
                    current.title = document.getElementById('id_title').value;
                    current.sectionid = sectionId;
                    current.roworder = document.getElementById('id_order').value;
                    
                    var loadPanel = new LoadPanel();
                    loadPanel.show();
                    current.save(function () { alert("Сохранение успешно!"); windowSave.close(); loadPanel.hide(); }, function (response) {alert("В ходе сохранения раздела произошли ошибки: " + response)});
                }, current);
                var windowSave = new JLModalWindow("Редактирование вопроса '" + item.title + "'", container);
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
        textNode.textContent = '№ вопроса';
        
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
    
    var removeQuestion = function (item) {
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
        if(!Questions.IsLoad) {
            Questions.remote = true;
            Questions.attributes = [ 'title', 'sectionid', 'roworder' ];
            var rules = {
                roworder: {title: function () {return rowOrderBehavior();}, sort: true, style: {width: '110px', textAlign: 'center'}},
                title: {events: ['add'], title: 'Текст вопроса'},
                __answersLink: {events: ['add'], title: 'Ответы', action: function (item) { return answerLink(item); }, style: {width: '150px', textAlign: 'center'} },
                sectionTitle: {events: ['add'], title: 'Раздел', action: function (item) { return sectionTitleBehavior(item); }},
                __remove: {title: function () { return backEvent(); }, events: ['add', 'onload'], action: function (item) { return removeQuestion(item); }, style: {width: '22px'}},
                __edit: {title: function () { return createQuestion(); }, events: ['add', 'onload'], action: function (item) { return editQuestion(item); }, style: {width: '22px'}}
            };
            var viewModel = new BindingViewModel(view.view, rules);
            viewModel.listenerAdd(Questions.observer, 'add');
            viewModel.listenerChange(Questions.observer, 'change');
            viewModel.listenerRemove(Questions.observer, 'remove');
            
            var loadPanel = new LoadPanel();
            loadPanel.show();
            Questions.loadRemote({sectionid: sectionId}, function () {loadPanel.hide();});
        }
        else {
            view.view.repaint();
        }
    };
};

