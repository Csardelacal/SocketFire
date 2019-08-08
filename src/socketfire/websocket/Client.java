package socketfire.websocket;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*

s.send(JSON.stringify({type: 0, payload: "Hello World"}));

var sockets = new Array();
var counter = 0;
for (var i = 0; i < 10; i++) {
  var s = new WebSocket("ws://localhost:1337/test")
  sockets.push(s);
  s.onopen = function (e) {for (var i = 0; i < 20; i++) e.srcElement.send(""+i);}
  s.onmessage = function (e) {if (parseInt(e.data) == 19) e.srcElement.close(); counter++}
  s.onclose = function (e) {console.log("Socket closed");}
  s.onerror = function (e) {console.log(e);}
}
For testing
*/


import socketfire.websocket.Channel;
import socketfire.websocket.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import socketfire.Handshake;
import socketfire.PartialMessage;
import socketfire.Queue;
import socketfire.handshake.MalformedHeaderException;
import socketfire.message.ChannelMessage;
import socketfire.message.Message;
import socketfire.message.STDMessage;
import socketfire.message.ServerMessage;
import socketfire.message.UserMessage;

/**
 *
 * @author cesar
 */
public class Client extends Thread {
	
	private final Socket socket;
	private final SocketAdapter socketAdapter;
	private final BufferedReader in;
	private final PrintWriter out;
	private final Server server;
	private final Channel channel;
	private final Queue queue;
	private static int idCounter = 0;
	
	private String name;
	
	public static final int MASK_SIZE = 4;
	
	public Client(Server server, Socket s) throws IOException, MalformedHeaderException {
		this.server = server;
		this.socket = s;
		this.socketAdapter = new SocketAdapter(this.socket, this);
		this.in  = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.out = new PrintWriter(this.socket.getOutputStream(), true);
		
		this.name = "User - " + (Client.idCounter++);
		
		ArrayList<String> headers = new ArrayList<>();
		
		do {
			headers.add(this.in.readLine());
		}
		while (this.in.ready());
		
		Handshake handshake = new Handshake(headers);
		this.channel = this.server.getChannel("/test");
		this.queue = new Queue(this.socketAdapter);
		
		
		this.out.write(handshake.getHeaders());
		this.out.flush();
		this.channel.addClient(this);
		this.send(new STDMessage(null, "Welcome to the EQTV server"));
	}
	
	public void send(Message message) {
		this.queue.queueMessage(message);
	}
	
	public Message parseMessage(String s) {
		//System.out.println(s);
		JSONObject data;
		JSONArray argdata;
		String[] args;
		
		try {
			data = new JSONObject(s);
			switch(data.getInt("type")) {
				case Message.TYPE_CHAT:
					return new STDMessage(this, data.getString("payload"));
				case Message.TYPE_USER:
					argdata = data.getJSONArray("args");
					args = new String[10];
					for (int i = 0; i < argdata.length(); i++) args[i] = argdata.getString(i);
					return new UserMessage(this, data.getString("action"), args);
				case Message.TYPE_CHANNEL:
					argdata = data.getJSONArray("args");
					args = new String[10];
					for (int i = 0; i < argdata.length(); i++) args[i] = argdata.getString(i);
					return new ChannelMessage(this, data.getString("action"), args);
				case Message.TYPE_SERVER:
					argdata = data.getJSONArray("args");
					args = new String[10];
					for (int i = 0; i < argdata.length(); i++) args[i] = argdata.getString(i);
					return new ServerMessage(this, data.getString("action"), args);
			}
			//this.channel.broadcast(data.getString("payload"));
		}
		catch (JSONException e) {
			System.out.println("Corrupted data was received from client");
		}
		
		return null;
	}
	
	/**
	 * @see http://stackoverflow.com/questions/12702305/using-html5-client-with-a-server-in-java
	 */
	@Override
	public void run() {
		this.queue.start();
		
		PartialMessage str = null;
		while (null != (str = this.socketAdapter.read(str))) {
			if (str.isComplete()) {
				System.out.println(str.getMessage());
				str = null;
			}
		}
	}

	public synchronized void finish() {
		this.channel.dropClient(this);
		this.queue.interrupt();
		this.interrupt();
	}

	public Server getServer() {
		return server;
	}
	
	public String getClientName() {
		return this.name;
	}
	
	public void setClientName(String name) {
		this.name = name;
		this.setName("Client - " + this.name);
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
}