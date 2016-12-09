var Observer = function () {
    this.subscribers = {};
};

Observer.prototype.addListener = function (event) {
    if(!this.subscribers.hasOwnProperty(event)) {
        this.subscribers[event] = [];
    }
};

Observer.prototype.removeListener = function (event) {
    if(this.subscribers.hasOwnProperty(event)) {
        delete this.subscribers[event];
    }
};

Observer.prototype.addCallback = function (event, callback) {
    if(this.subscribers.hasOwnProperty(event)) {
        this.subscribers[event].push(callback);
    }
};

Observer.prototype.initEvent = function (event, args) {
    if(this.subscribers.hasOwnProperty(event)) {
        for(var i = 0, n = this.subscribers[event].length; i < n; i++) {
            this.subscribers[event][i].apply(null, args);
        }
    }
};