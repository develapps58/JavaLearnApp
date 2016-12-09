/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var Model = function () {};

//Функции экземпляра модели
Model.prototype = {
    //Инициализация объекта модели
    init: function () {},
    
    //Получить название ключевого поля
    keyName: function () { 
        return this.parent.key;
    },
    
    //Получить значение ключевого поля.
    //Если нет - null
    key: function () {
        if(!(this.keyName() in this) || isUndefined(this[this.keyName()])) return null;
        return this[this.keyName()];
    },
    create: function () {
        (this.parent.remote) ? this.createRemote() : this.parent.save(this);
    },
    update: function () {
        (this.parent.remote) ? this.updateRemote() : this.parent.save(this);
    },
    //Ф-я выполнения удаленного запроса создания
    createRemote: function () {
        var self = this;
        var request = prepareRequest(
                function (response) { self.parent.saveAction(response, self); },
                function (response) { self.parent.errorAction(response); });
        var user = new Users();
        if(user.GetAuthInfo()) {
            request.addHeader(["Authorization",  user.GetAuthInfo()]);
        }
        request.exec('POST', HOST + this.parent.getName(), this.rowData());
    },
    
    //Ф-я выполнения удаленного запроса редактирования
    updateRemote: function () {
        var self = this;
        var request = prepareRequest(
                function (response) { self.parent.saveAction(response, self); },
                function (response) { self.parent.errorAction(response); });
        var user = new Users();
        if(user.GetAuthInfo()) {
            request.addHeader(["Authorization",  user.GetAuthInfo()]);
        }
        request.addHeader(["If-Match", this._etag]);
        request.exec('PATCH', HOST + this.parent.getName() + '/' + this.key(), this.rowData());
    },
    
    //Ф-я выполнения удаленного запроса удаления
    removeRemote: function () {
        var self = this;
        var request = prepareRequest(
                function (response, code) { self.parent.removeAction(response, code, self); },
                function (response) { self.parent.errorAction(response); });
        var user = new Users();
        if(user.GetAuthInfo()) {
            request.addHeader(["Authorization",  user.GetAuthInfo()]);
        }
        request.addHeader(["If-Match", this._etag]);
        request.exec('DELETE', HOST + this.parent.getName() + '/' + this.key(), this.rowData());
    }
};

//Хранилище всех моделей
Model.models = {};

//Функции модели
Model.create = function (name, remote) {
    if(isUndefinedOrNull(name) || name === '') {
        return null;
    }
    //Если модель с данным именем уже есть
    if(name in this.models) return this.models[name];
    
    var modelObject = Object.create(this);
    this.models[name] = modelObject;
    
    //Имя модели
    var modelName = name;
    this.getName = function () {
        return modelName;
    } ;
    
    //Хранилище записей модели
    modelObject.model = {};

    //Список аттрибутов модели
    modelObject.attributes = [];
    //Название ключевого поля
    modelObject.key = '_id';
    //Флаг, указывающий что модель 'удаленная'
    modelObject.remote = remote || false;
    
    //Флаг, показывающий была ли выполнена загрузка данных с сервера
    modelObject.IsLoad = false;
    
    //Ф-ии обратного вызова для операций сохранения/удаления/ошибок
    //Ф-я обратного вызова для операций сохранения
    modelObject.saveAction = function (response, item) {
        var responseAsJson = JSON.parse(response);
        if(!('_id' in responseAsJson)) {
            modelObject.errorAction(response); return;
        }
        item._id = responseAsJson._id;
        item._etag = responseAsJson._etag;
        item.parent.save(item);
    };
    //Ф-я обратного вызова для операций удаления
    modelObject.removeAction = function (response, code, item) {
        if(code === 204) {
            item.parent.remove(item);
        };
    };
    //Ф-я обратного вызова для ошибки
    modelObject.errorAction = function (response) {
        console.log(response);
    };
    
    //Наблюдатели событий
    modelObject.observer = new Observer();
    modelObject.observer.addListener('onload');
    modelObject.observer.addListener('add');
    modelObject.observer.addListener('change');
    modelObject.observer.addListener('remove');
    
    var isModelItem = function (item) {
        if(!('parent' in item) || item.parent.getName() !== modelName) return false;
        return true;
    };

    //Ф-я удаленной загрузки
    modelObject.loadRemote = function (p) {
        var self = this;
        var params = "";
        var request = prepareRequest(function (response) {             
            var responseAsJson = JSON.parse(response);
            if(!('_items' in responseAsJson)) return ;
            var items = responseAsJson['_items'];
            for(var i = 0, n = items.length; i < n; i++) {
                self.save(self.createItem(items[i]));
            }
            self.IsLoad = true;
            self.observer.initEvent('onload', [self.model]);
        });
        if(!isUndefinedOrNull(p)) {
            params = '?where=';
            for(var paramskey in p) {
                params += '{"' + paramskey + '":"' + p[paramskey] + '"}';
            }
        }
        var user = new Users();
        if(user.GetAuthInfo()) {
            request.addHeader(["Authorization",  user.GetAuthInfo()]);
        }
        request.exec('GET', HOST + modelName + params, null);
    };
    
    //Ф-я добавления 
    modelObject.save = function (item) {
        if(!isModelItem(item)) return false;
        if(!(item[this.key] in this.model)) {
            this.model[item[this.key]] = item;
            this.observer.initEvent('add', [item]);
        }
        else {
            this.model[item[this.key]] = item;
            this.observer.initEvent('change', [item]);
        }
        return true;
    };
    
    modelObject.remove = function (item) {
        if(!isModelItem(item)) return false;
        delete this.model[item[this.key]];
        this.observer.initEvent('remove', [item]);
        return true;
    };
    
    return modelObject;
};

Model.init = function () {
    var instance = Object.create(this.prototype);
    instance.parent = this;    
    instance.init.apply(instance, arguments);
    return instance;
};

Model.removeModel = function (name) {
    delete Model.models[name];
};

//Методы экземпляра модели

//Метод сохранения записи
Model.prototype.save = function () {
    (this.key() === null) ? this.create() : this.update();
};

//Метод удаления записи
Model.prototype.remove = function () {
    (this.parent.remote) ? this.removeRemote() : this.parent.remove(this);
};

//Метод замены данных записи из другой записи
Model.prototype.replace = function (item) {
    for(var i = 0, n = this.parent.attributes.length; i < n; i++) {
        var attribute = this.parent.attributes[i];
        if((attribute in this) && (attribute in item))
            this[attribute] = item[attribute];
    }
};

//Создание дубликата записи
Model.prototype.duplicate = function () {
    var result = {};
    for(var k in this) result[k] = this[k];
    return result;
};

//Метод получения 'чистых данных' записи
Model.prototype.rowData = function () {
    var result = {};
    for(var i = 0, n = this.parent.attributes.length; i < n; i++) {
        var attribute = this.parent.attributes[i];
        if((attribute in this) && !isUndefined(this[attribute]))
            result[attribute] = this[attribute];
    }
    return result;
};

//Метод обновления ключа
Model.prototype.updateKey = function (key) {
    if(this[this.parent.key] === key) return ;
    var record = this.parent.init();
    for(var k in this) {
        record[k] = this[k];
    }
    record[this.parent.key] = key;   
    record.save();
    this.remove();
};

//Методы модели
Model.clear = function () {
    for (var row in model) delete this.model[row];
};
Model.getModelByName = function (name) {
    return this.models[name];
};
Model.getName = function () {
    return this.modelName;
};
Model.getAttributes = function () {
    return this.attributes;
};
Model.findById = function (id) {
    return this.model[id];
};
Model.findByField = function (key, value) {
    var model = {};
    for(var k in this.model) {
        if(this.model[k][key] === value)
            model[k] = this.model[k];
    }
    return model;
};
Model.createItem = function (data) {
    var item = this.init();
    for(var k in data) {
        item[k] = data[k];
    }
    return item;
};