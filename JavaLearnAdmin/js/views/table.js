/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var Item = function (content, parent) {
    var view, altview, currentView;
    this.parent = parent;
    
    this.setContent = function (c) {
        if(isDOMNode(c) || isFunction(c)) {
            view = c;
        }
        else {
            view = document.createElement('span');
            view.textContent = c;
        };
        currentView = view;
    };
    
    this.setContent(content);
    
    this.getCurrentView = function () {
        if(!isFunction(currentView)) return currentView;
        var f = currentView(this);
        return ('paint' in f) ? f.paint() : document.createElement('span');
    };
    
    this.getParent = function () { return this.parent; };
    
    this.getView = function () { return view; };
    this.setView = function (v) { view = v; };
    this.getAltview = function () { return altview; };
    this.setAltview = function (v) { altview = v; };
    
    this.toggleView = function () { (currentView === view) ? (currentView = altview) : (currentView = view); this.parent.repaint(); this.focus(); };
    this.focus = function () { this.getCurrentView().focus(); };
};

var HeaderCell = function (value, parent) {
    this.parent = parent;
    var view = document.createElement('th');
    var item = new Item(value, this);
    var clear = function () { view.innerHTML = ''; };
    
    this.getView = function () { return view; };
    this.repaint = function () { clear(); view.appendChild(item.getCurrentView()); };

    this.repaint();
};

var Cell = function (content, parent) {
    this.parent = parent;
    var view = document.createElement('td');
    var item = new Item (content, this);
    var clear = function () { view.innerHTML = ''; };
    
    this.setContent = function (c) { item.setContent(c); };
    this.getView = function () { return view; };
    this.repaint = function () { clear(); view.appendChild(item.getCurrentView());};

    this.repaint();
};

var Row = function (id, parent) {
    var itemid = id;
    var view = document.createElement('tr');
    var cells = {};    
    var clear = function () { view.innerHTML = ''; };
    
    this.parent = parent;
    
    this.getView = function () { return view; };
    this.addCell = function (name, cell) { cells[name] = cell; };
    this.cellCount = function () { return cells.length; };
    this.cells = function () { return cells; };
    this.cell = function (index) { return cells[index]; };
    this.setId = function (id) { itemid = id; };
    this.getId = function () { return itemid; };
    this.repaint = function () {
        clear();
        for(var cell in cells) {
            cells[cell].repaint();
            view.appendChild(cells[cell].getView());
        }
    };
    this.reaintCell = function (name) { cells[name].repaint(); };
};

var Table = function (view) {
    this.view = view;
    this.rules = {};
    this.rows = {};
};

Table.prototype.clear = function () {
    this.view.innerHTML = '';
};

Table.prototype.addItem = function (row, paint) {
    this.rows[row.getId()] = row;
    if(paint) this.paintItem(row.getId());
};

Table.prototype.removeItem = function (item) {
    this.removeItemById(item.getId());
};

Table.prototype.removeItemById = function (id) {
    delete this.rows[id];
    this.repaint();
};

Table.prototype.getItemById = function (id) {
    return this.rows[id];
};

Table.prototype.paintItem = function (rowId) {
    this.rows[rowId].repaint();
    this.view.appendChild(this.rows[rowId].getView());
};

Table.prototype.repaint = function () {
    this.clear();
    this.drawHeaders();
    var rows = this.rowsAsArray();
    for(var i = 0, n = rows.length; i < n; i++) {
        this.view.appendChild(rows[i].getView());
    }
};

Table.prototype.rowsAsArray = function () {
    var rows = [];
    for(var row in this.rows) {
        rows.push(this.rows[row]);
    }
    return rows;
};

Table.prototype.getSortByField = function (fieldName) {
    var rows = this.rowsAsArray();
    
    var order = 0;
    
    rows.sort(function (a, b) {
        var p1 = a.cell(0).getValue();
        var p2 = b.cell(0).getValue();
        /*if(typeof filter === 'function') {
            return filter(p1, p2);
        }*/

        if(!isNaN(parseFloat(p1)) && !isNaN(parseFloat(p2))) {
            p1 = parseFloat(p1);
            p2 = parseFloat(p2);
        }            
        if(p1 > p2) {
            if(order === 0) return 1;
            else return -1;
        }
        if(p1 < p2) {
            if(order === 0) return -1;
            else return 1;
        }
        return 0;
    });
    
    return rows;
};

Table.prototype.sort = function () {
    
};

Table.prototype.drawHeaders = function () {
    var row = new Row('_header', this);
    for(var rule in this.rules) {
        var title = this.rules[rule].title || "";
        row.addCell(rule, new HeaderCell(title, row, this.rules[rule].sortable));
    };
    this.addItem(row, true);
};

Table.prototype.drawItem = function (item) {
    var row = this.getItemById(item.key());
    if(!isUndefinedOrNull(row)) {
        var cells = row.cells();
        for(var cellname in cells) {
            if(cellname in item) {
                cells[cellname].setContent(item[cellname]);
                cells[cellname].repaint();
            }
        }
        return ;
    }
    row = new Row(item.key(), this);
    for(var fieldname in this.rules) {
        if(!(fieldname in item)) continue;
        var cell = new Cell(item[fieldname], this);
        if(!isUndefinedOrNull(this.rules[fieldname].style)) setStyle(cell, this.rules[fieldname].style);
        row.addCell(fieldname, cell);
    };
    this.addItem(row, true);
};

var setStyle = function (item, styles) {
    var view = item.getView();
    for(var style in styles) {
        view.style[style] = styles[style];
    };
};