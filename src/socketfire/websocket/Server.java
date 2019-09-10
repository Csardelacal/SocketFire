package socketfire.websocket;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocketFactory;
import socketfire.Handshake;
import socketfire.Queue;
import socketfire.handshake.MalformedHeaderException;
import socketfire.http.RequestFactory;
import socketfire.webserver.Request;

/**
 *
 * @author cesar
 */
public class Server extends Dispatcher implements Runnable {
	
	private int port = 1337;
	private final HashMap<String, Channel> channels = new HashMap<>();

	public Server(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		
		try {
			SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			ServerSocket s = ssf.createServerSocket(this.port);
			
			RequestFactory rf = new RequestFactory();
			
			Socket clientsock = null;
			Client client;
		
			while (true) {
				try {
					clientsock = s.accept();
					Request r = rf.receive(clientsock);
					
					
					Handshake handshake = new Handshake(r.getHeaders());
					
					BufferedReader in  = new BufferedReader(new InputStreamReader(clientsock.getInputStream()));
					PrintWriter out = new PrintWriter(clientsock.getOutputStream(), true);
					out.write(handshake.getHeaders());
					out.flush();
					
					SocketAdapter sa = new SocketAdapter(clientsock);
					client = new Client(this, sa);
					sa.setClient(client);
					client.setChannel(this.getChannel(r.getPath()));
					
					System.out.println("Client connected");
					client.start();
					System.out.println("Client started");
					this.addClient(client);
				} 
				catch (MalformedHeaderException ex) {
					if (clientsock != null && !clientsock.isClosed()) clientsock.close();
					Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
				} 
				catch (IOException e) {
					System.out.println("Client handshake failed");
					System.out.println("> " + e.getMessage());
				}
			}
			
		} catch (IOException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public Channel getChannel(String id) {
		if (this.channels.containsKey(id)) {
			return this.channels.get(id);
		}
		else {
			Channel channel = new Channel(id);
			this.channels.put(id, channel);
			return channel;
		}
	}
}
