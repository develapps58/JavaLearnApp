/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
"use strict";

var LoadSections = function (container) {
 
    var Sections = Model.create('sections', true);
    
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
            title: 'Заголовок', 
            content: function () {
                var node = document.createElement('input');
                node.setAttribute('class', 'text');
                node.setAttribute('type', 'text');
                node.setAttribute('id', 'id_title');
                return node;
            }
        },
        {
            name: 'description',
            title: 'Текст',
            content: function () {
                var node = document.createElement('textarea');
                node.setAttribute('class', 'textedit');
                node.setAttribute('id', 'id_description');
                CKEDITOR.replace(node);
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
 
    var descriptionBehavior = function (item) {
        return function () {
            var node = document.createElement('span');
            node.setAttribute('class', 'data-link');
            node.textContent = '[Смотреть описание]';
            
            var content = document.createElement('div');
            content.innerHTML = item.description;
            content.style.borderLeft = '15px solid white';
            
            var windowContent = new JLModalWindow(item.title, content);
            
            node.onclick = function () {
                windowContent.show();
            };
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };

    var chaptersLink = function (item) {
        return function () {
            var node = document.createElement('span');
            node.textContent = '[Перейти к главам]';
            node.setAttribute('class', 'data-link');
            
            node.onclick = function () {
                view.view.clear();
                var loadChapters = new LoadChapters(document.getElementById('data-table'), item._id);
                loadChapters.load();
                
                var historyOp = new HistoryOperations();
                historyOp.add(loadChapters.load);
            };
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };
    
    var questionsLink = function (item) {
        return function () {
            var node = document.createElement('span');
            node.textContent = '[Перейти к вопросам]';
            node.setAttribute('class', 'data-link');
            
            node.onclick = function () {
                view.view.clear();
                var loadQuestions = new LoadQuestions(document.getElementById('data-table'), item._id);
                loadQuestions.load();
                
                var historyOp = new HistoryOperations();
                historyOp.add(loadQuestions.load);
            };
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };    

    var createSection = function () {
        var node = document.createElement('div');
        node.setAttribute('class', 'operation_btn create_btn');
        
        node.onclick = function () {
            var container = createSaveFields (fields, function () {
                
                var Sections = Model.create('sections', true);
                var section = Sections.init();
                
                section.title = document.getElementById('id_title').value;
                section.description = CKEDITOR.instances.id_description.getData();
                section.roworder = document.getElementById('id_order').value;
                
                var loadPanel = new LoadPanel();
                loadPanel.show();
                section.save(
                        function () { alert("Сохранение успешно!"); windowSave.close(); loadPanel.hide(); },
                        function (response) {alert("В ходе сохранения раздела произошли ошибки: " + response);windowSave.close(); loadPanel.hide();});
            });
            
            var windowSave = new JLModalWindow("Создание раздела.", container);
            windowSave.show();
        };

        return {
            paint: function () {
                return node;
            }
        };
    };

    var editSection = function (item) {
        return function () {
            var node = document.createElement('div');
            node.setAttribute('class', 'operation_btn edit_btn');
            node.onclick = function () {
                var current = item;
                var container = createSaveFields (fields, function () {
                    
                    current.title = document.getElementById('id_title').value;
                    current.description = CKEDITOR.instances.id_description.getData();
                    current.roworder = document.getElementById('id_order').value;
                    
                    var loadPanel = new LoadPanel();
                    loadPanel.show();
                    current.save(
                            function () { alert("Сохранение успешно!"); windowSave.close(); loadPanel.hide(); },
                            function (response) {alert("В ходе сохранения раздела произошли ошибки: " + response); windowSave.close(); loadPanel.hide();});
                }, current);
                var windowSave = new JLModalWindow("Редактирование раздела '" + item.title + "'", container);
                windowSave.show();
            };
            return {
                paint: function () {
                    return node;
                }
            };
        };
    };

    var removeSection = function (item) {
        return function () {
            var node = document.createElement('div');
            node.setAttribute('class', 'operation_btn remove_btn');
            node.onclick = function () {
                if(confirm("Удалить раздел '" + item.title + "'")) {
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
    
    var rowOrderBehavior = function () {
        var currentSortOrder = 0;
        
        var node = document.createElement('div');
        node.setAttribute('class', 'sort-header');
        
        var textNode = document.createElement('span');
        textNode.textContent = '№ раздела';
        
        var arrowImg = document.createElement('img');
        arrowImg.width = '20';
        arrowImg.height = '20';
        arrowImg.src = 'images/no-sort.png';
        
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

    this.load = function () {
        if(!Sections.IsLoad) {
            Sections.remote = true;
            Sections.attributes = [ 'title', 'description', 'roworder' ];
            var rules = {
                roworder: {title: function () {return rowOrderBehavior();}, sort: true, style: {width: '110px', textAlign: 'center'}},
                title: {title: "Название раздела"},
                __description: {events: ['add'], title: 'Описание раздела', action: function (item) { return descriptionBehavior(item); }, style: {width: '150px', textAlign: 'center'}},
                __chaptersLink: {events: ['add'], title: 'Главы', action: function (item) { return chaptersLink (item); }, style: {width: '150px', textAlign: 'center'} },
                __questionsLink: {events: ['add'], title: 'Вопросы', action: function (item) { return questionsLink (item); }, style: {width: '150px', textAlign: 'center'} },
                __remove: {events: ['add'], action: function (item) { return removeSection(item); }, style: {width: '22px'}},
                __edit: {
                    title: function () { return createSection(); },
                    events: ['add'],
                    action: function (item) {
                        return editSection(item);
                    },
                    style: {width: '22px'}
                }
            };
            var viewModel = new BindingViewModel(view.view, rules);

            viewModel.listenerAdd(Sections.observer, 'add');
            viewModel.listenerChange(Sections.observer, 'change');
            viewModel.listenerRemove(Sections.observer, 'remove');
            
            var loadPanel = new LoadPanel();
            loadPanel.show();
            Sections.loadRemote({}, function () {loadPanel.hide();});
        }
        else {
            view.view.repaint();
        }
    };
};