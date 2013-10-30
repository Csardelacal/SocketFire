/* jslint browser: true */

var Lysine = {},
    HTMLElement = HTMLElement,
	 document = document;

/**
 * Creates a new Lysine view that handles the user's HTML and accepts objects as
 * data to fill in the said HTML. If no data is set the view will behave like an
 * interface for a form that exists inside the HTML.
 * 
 * @param HTMLElement|String id
 */
Lysine.view = function (id) {
	"use strict";
	
	var view, 
		 html,
	    data = {},
	    adapters = {},
	    attributeAdapters = [];

	/*
	 * First we receive the id and check whether it is a string or a HTMLElement
	 * this way we can handle several types of arguments received there.
	 */
	if (id instanceof HTMLElement) {
		view = id;
	}
	else {
		view = document.querySelector('*[data-lysine-view="'+ id +'"]');
	}

	html = view.cloneNode(true);

	//Privileged methods
	this.setData = function setData (newData) {
		data = newData;
		this.exportData();
	};

	this.getData = function getData () {
		this.importData();
		return data;
	};

	this.getValue = this.getData;
	this.setValue = this.setData;

	this.importData = function () {
		var i;

		for (i in adapters) {
			if (adapters.hasOwnProperty(i)) {
				data[i] = adapters[i].getValue();
			}
		}

	};

	this.exportData = function (parent) {
		var i;
		
		for (i in adapters) {
			if (adapters.hasOwnProperty(i)) {
				adapters[i].setValue(data[i]);
			}
		}
		
		for (i = 0; i < attributeAdapters.length; i+=1) {
			attributeAdapters[i].fetchData(this);
		}
	};
	
	this.fetchAdapters = function fetchAdapters(parent) {
		//Argument validation
		parent = (parent !== undefined)? parent : html;
		
		var elements = Array.prototype.slice.call(parent.childNodes, 0),
			 i, v, attrAdapter;
		
		
		for (i = 0; i < elements.length; i+=1) {
			if (elements[i].getAttribute && elements[i].getAttribute('data-for')) {
				if (elements[i].hasAttribute('data-lysine-view')) {
					v = new Lysine.ArrayAdapter(elements[i]);
					adapters[elements[i].getAttribute('data-for')] = v;
				}
				else {
					adapters[elements[i].getAttribute('data-for')] = this.getAdapter(elements[i], null);
				}
			}
			else if (elements[i].nodeType !== 3) {
				this.fetchAdapters(elements[i]);
			}
			
			if (elements[i].nodeType !== 3) {
				attrAdapter = new Lysine.AttributeAdapter(elements[i]);
				if (attrAdapter.hasLysine()) {
					attributeAdapters.push(attrAdapter);
				}
			}
		}
	};

	this.getHTML = function getHTML() {
		return html;
	};

	this.getElement = this.getHTML;

	this.destroy = function destroy() {
		html.parentNode.removeChild(html);
		return this;
	};

	this.getAdapter = function getAdapter(element, value) {
		var adapter;

		if (element.tagName.toLowerCase() === "input") {
			adapter = new Lysine.InputAdapter(element);
		}
		else {
			adapter = new Lysine.HTMLNodeAdapter(element);
		}
		adapter.setValue(value);
		return adapter;
	};

	//Constructor tasks
	html.removeAttribute('data-lysine-view');
	this.fetchAdapters();
	view.parentNode.insertBefore(html, view);
};

function Adapter() {
	"use strict";
	
	this.element = null;

	//abstract getValue()
	//abstract setValue()

	this.setElement = function (e) {
		this.element = e;
	};

	this.getElement = function () {
		return this.element;
	};
}


Lysine.InputAdapter = function InputAdapter(element) {
	"use strict";
	
	this.setElement(element);
	
	this.getValue = function () {
		return this.getElement().value;
	};
	
	this.setValue = function (val) {
		if (val === undefined) val = '';
		this.getElement().value = val;
	};
};

Lysine.InputAdapter.prototype = new Adapter();
Lysine.InputAdapter.prototype.constructor = Lysine.InputAdapter;

Lysine.ArrayAdapter = function ArrayAdapter(view) {
	"use strict";
	this.views = [];
	this.base  = view;
	
	this.getValue = function () {
		var ret = [],
			 i;
  
		for (i = 0; i < this.views.length; i+=1) {
			ret.push(this.views[i].getValue());
		}
		return ret;
	};
	
	this.setValue = function (val) {
		
		var i, v;
		
		if (val === undefined) {
			return;
		}
		
		for (i = 0; i < this.views.length; i+=1) {
			this.views[i].destroy();
		}
		
		this.views = [];
		for (i = 0; i < val.length; i+=1) {
			v = new Lysine.view(this.base);
			this.views.push(v);
			v.setValue(val[i]);
		}
	};
};

Lysine.ArrayAdapter.prototype = new Adapter();
Lysine.ArrayAdapter.prototype.constructor = Lysine.ArrayAdapter;

Lysine.HTMLNodeAdapter = function HTMLNodeAdapter(element) {
	"use strict";
	
	this.setElement(element);
	
	this.getValue = function () {
		return this.getElement().innerHTML;
	};
	
	this.setValue = function (val) {
		this.getElement().innerHTML = val;
	};
};

Lysine.HTMLNodeAdapter.prototype = new Adapter();
Lysine.HTMLNodeAdapter.prototype.constructor = Lysine.HTMLNodeAdapter;

Lysine.AttributeAdapter = function AttributeAdapter(element) {
	"use strict";
	
	this.setElement(element);
	
	this.fetchData = function (view) {
		var dataset = this.getElement().dataset,
			 data = view.getData(),
			 j,
			 i,
			 value;
  
		for (i in dataset) {
			if (dataset.hasOwnProperty(i)) {
				if (i.search(/^lysine/) !== -1) {
					value = dataset[i];
					for (j in data) {
						if (data.hasOwnProperty(j)) {
							value = value.replace(new RegExp("\\{" + j + "\\}"), data[j]);
						}
					}
					
					this.getElement().setAttribute(
						i.replace(/^lysine/, '').toLowerCase(), 
						value
					);
				}
			}
		}
	};
	
	this.hasLysine = function() {
		var dataset = this.getElement().dataset,
			 i;
  
		for (i in dataset) {
			if (dataset.hasOwnProperty(i)) {
				if (i.search(/^lysine/) !== -1) {
					return true;
				}
			}
		}
		return false;
	};
};

Lysine.AttributeAdapter.prototype = new Adapter();
Lysine.AttributeAdapter.prototype.constructor = Lysine.AttributeAdapter;

Lysine.request = function (url, base) {
	
	var views = [];
	
	this.putView = function (view) {
		views.push(view);
	};
	
	this.getViews = function () {
		return views;
	};
	
	//Constructor
	var context = this;
	this.r = new XMLHttpRequest();
	
	this.r.onreadystatechange = function (e) {
		if (e.target.readyState === 4 && e.target.status === 200) {
			var content = JSON.parse(e.target.responseText);
			for (var i = 0; i < content.length; i++) {
				var v = new Lysine.view(base);
				v.setData(content[i]);
				context.putView(v);
			}
			
			if (typeof context.onready === 'function') this.onready(this);
		}
	}
	
	this.r.open('GET', url, true);
	this.r.send();
}


//Hide the unneeded view prototypes
var style = document.createElement('style');
style.type = "text/css";
style.innerHTML = "*[data-lysine-view] { display: none !important;}";
document.head.appendChild(style);