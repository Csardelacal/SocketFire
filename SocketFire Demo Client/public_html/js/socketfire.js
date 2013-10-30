
function SocketFire(server, channel) {
	
	this.server  = server;
	this.channel = channel;
	this.socket  = new WebSocket('ws://' + server + '/' + channel);
	
	
	this.bind = function (fn) {
		var ctx = this;
		return function () {
			fn.apply(ctx, arguments);
		};
	};
	
	this.messageHandler = function (message) {
		var data = JSON.parse(message.data);
		console.log(data);
		if (data.type === 0 && this.onSTDMessage !== undefined) return this.onSTDMessage.call(this, data);
		if (data.type === 3 && this.onChannelMessage !== undefined) return this.onChannelMessage.call(this, data);
	};
	
	this.sendSTDMessage = function (str) {
		this.socket.send(JSON.stringify({type: 0, payload: str}));
	};
	
	this.sendChannelMessage = function (action, args) {
		if (args === undefined) args = [];
		console.log({'type': 3, 'action': action, 'args': args});
		this.socket.send(JSON.stringify({'type': 3, 'action': action, 'args': args}));
	};
	
	
	this.socket.onmessage = this.bind(this.messageHandler);
	this.socket.onerror   = function (e)  {console.log(e);};
}