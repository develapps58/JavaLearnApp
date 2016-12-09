/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


var BindingViewModel = function (view, rules) {
    this.view = view;
    this.rules = rules;
    this.view.rules = this.rules;
    
    this.view.drawHeaders();
};

BindingViewModel.prototype.prepareBehaviors = function (listenerName, item) {
    for(var rule in this.rules) {
        if(!('events' in this.rules[rule])) continue;
        if(this.rules[rule].events.indexOf(listenerName) === -1) continue;
        if(!('action' in this.rules[rule])) continue;
        if(!isFunction(this.rules[rule]['action'])) continue;
        item[rule] = this.rules[rule].action(item);
    }
};

BindingViewModel.prototype.listenerOnLoad = function (listener, name) {
    var self = this;
    listener.addCallback(name, function (model) {
        for(var _item in model) {
            (function (item) {
                self.prepareBehaviors(name, item);
                self.view.drawItem(item);
            })(model[_item]);
        }
    });
};

BindingViewModel.prototype.listenerAdd = function (listener, name) {
    var self = this;
    listener.addCallback(name, function (item) {
        self.prepareBehaviors(name, item);
        self.view.drawItem(item);
    });
};

BindingViewModel.prototype.listenerChange = function (listener, name) {
    var self = this;
    listener.addCallback(name, function (item) {
        self.view.drawItem(item);
    });
};

BindingViewModel.prototype.listenerRemove = function (listener, name) {
    var self = this;
    listener.addCallback(name, function (item) {
        self.view.removeItemById(item.key());
    });
};