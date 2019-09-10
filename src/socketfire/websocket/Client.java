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
import socketfire.Incoming;
import socketfire.Queue;
import socketfire.handshake.MalformedHeaderException;
import socketfire.message.Message;

/**
 *
 * @author cesar
 */
public class Client extends Thread {
	
	private final SocketAdapter socketAdapter;
	private final Server server;
	private final Queue queue;
	private Channel channel;
	private static int idCounter = 0;
	
	private String name;
	
	public static final int MASK_SIZE = 4;
	
	public Client(Server server, SocketAdapter sa) throws IOException, MalformedHeaderException {
		this.server = server;
		this.socketAdapter = sa;
		
		this.name = "User - " + (Client.idCounter++);
		this.queue = new Queue(this.socketAdapter);
		
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
			return new Message(data.getString("payload"), this);
			
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
		
		Incoming str = null;
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
	
	public Client setChannel(Channel channel) {
		this.channel = channel;
		this.channel.addClient(this);
		this.send(new Message("Welcome to the EQTV server", null));
		return this;
	}
	
	
	
}
