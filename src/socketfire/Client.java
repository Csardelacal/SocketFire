package socketfire;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
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
import socketfire.handshake.MalformedHeaderException;

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
	
	public static final int MASK_SIZE = 4;
	
	public Client(Server server, Socket s) throws IOException, MalformedHeaderException {
		this.server = server;
		this.socket = s;
		this.socketAdapter = new SocketAdapter(this.socket);
		this.in  = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.out = new PrintWriter(this.socket.getOutputStream(), true);
		
		String headers = "";
		String read;
		
		do {
			read = this.in.readLine();
			headers += read + "\n";
		}
		while (read != null && !read.trim().equals(""));
		
		Handshake handshake = new Handshake(headers);
		headers = handshake.getHeaders();
		this.channel = this.server.getChannel(handshake.getRequestHeader("_location").getValue());
		this.queue = new Queue(this.socketAdapter);
		
		
		this.out.write(headers);
		this.out.flush();
		this.channel.registerClient(this);
		this.send("Welcome to the EQTV server");
		//this.out = null;
	}
	
	public void send(String message) {
		this.queue.queueMessage(message);
	}
	
	public void parseMessage(String s) {
		//System.out.println(s);
		this.channel.broadcast(s);
	}
	
	/**
	 * @see http://stackoverflow.com/questions/12702305/using-html5-client-with-a-server-in-java
	 */
	@Override
	public void run() {
		this.queue.start();
		
		String str;
		while (null != (str = this.socketAdapter.read())) {
			this.parseMessage(str);
		}
	}
	
}
