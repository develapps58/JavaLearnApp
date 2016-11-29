/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
"use strict";

var LoadChapters = function (container, sectionId) {
    var Chapters = Model.create('chapters', true);
    
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
            name: 'content',
            title: 'Текст',
            content: function () {
                var node = document.createElement('textarea');
                node.setAttribute('class', 'textedit');
                node.setAttribute('id', 'id_content');
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
    
    var contentBehavior = function (item) {
        return function () {
            var node = document.createElement('span');
            node.setAttribute('class', 'data-link');
            node.textContent = '[Смотреть описание]';
            
            var content = document.createElement('div');
            content.innerHTML = item.content;
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
    
    var createChapter = function () {
        var node = document.createElement('div');
        node.setAttribute('class', 'operation_btn create_btn');
        
        node.onclick = function () {
            var container = createSaveFields (fields, function () {
                
                var Chapters = Model.create('chapters', true);
                var chapter = Chapters.init();
                
                chapter.title = document.getElementById('id_title').value;
                chapter.content = CKEDITOR.instances.id_content.getData();
                chapter.sectionid = sectionId;
                chapter.roworder = document.getElementById('id_order').value;
                
                var loadPanel = new LoadPanel();
                loadPanel.show();
                chapter.save(
                        function () { alert("Сохранение успешно!"); windowSave.close(); loadPanel.hide(); },
                        function (response) {alert("В ходе сохранения раздела произошли ошибки: " + response + " возможно дубликат номера"); loadPanel.hide();});
            });
            
            var windowSave = new JLModalWindow("Создание главы.", container);
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
            Chapters.removeModel('chapters');
            var historyOp = new HistoryOperations();
            historyOp.backEvent();
        };
        return {
            paint: function () {
                return node;
            }
        };
    };

    var editChapter = function (item) {
        return function () {
            var node = document.createElement('div');
            node.setAttribute('class', 'operation_btn edit_btn');
            node.onclick = function () {
                var current = item;
                var container = createSaveFields (fields, function () {
                    
                    current.title = document.getElementById('id_title').value;
                    current.content = CKEDITOR.instances.id_content.getData();
                    current.sectionid = sectionId;
                    current.roworder = document.getElementById('id_order').value;
                    
                    var loadPanel = new LoadPanel();
                    loadPanel.show();
                    current.save(
                            function () { alert("Сохранение успешно!"); windowSave.close(); loadPanel.hide(); },
                            function (response) {alert("В ходе сохранения раздела произошли ошибки: " + response + " возможно дубликат номера"); loadPanel.hide(); });
                }, current);
                var windowSave = new JLModalWindow("Редактирование главы '" + item.title + "'", container);
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
        textNode.textContent = '№ главы';
        
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
    
    var removeChapter = function (item) {
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
        if(!Chapters.IsLoad) {
            Chapters.remote = true;
            Chapters.attributes = [ 'title', 'content', 'sectionid', 'roworder' ];
            var rules = {
                roworder: {title: function () {return rowOrderBehavior();}, sort: true, style: {width: '110px', textAlign: 'center'}},
                title: {events: ['add'], title: 'Название главы'},
                sectionTitle: {events: ['add'], title: 'Раздел', action: function (item) { return sectionTitleBehavior(item); }},
                __content: {events: ['add', 'onload'], title: 'Контент главы', action: function (item) { return contentBehavior(item); }, style: {width: '200px', textAlign: 'center'}},
                __remove: {title: function () { return backEvent(); }, events: ['add', 'onload'], action: function (item) { return removeChapter(item); }, style: {width: '22px'}},
                __edit: {title: function () { return createChapter(); }, events: ['add', 'onload'], action: function (item) { return editChapter(item); }, style: {width: '22px'}}
            };
            var viewModel = new BindingViewModel(view.view, rules);
            viewModel.listenerAdd(Chapters.observer, 'add');
            viewModel.listenerChange(Chapters.observer, 'change');
            viewModel.listenerRemove(Chapters.observer, 'remove');
            
            var loadPanel = new LoadPanel();
            loadPanel.show();
            Chapters.loadRemote({sectionid: sectionId}, function () {loadPanel.hide();});
        }
        else {
            view.view.repaint();
        }
    };
};

