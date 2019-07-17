package socketfire;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocketFactory;
import socketfire.handshake.MalformedHeaderException;

/**
 *
 * @author cesar
 */
public class Server extends Dispatcher implements Runnable {
	
	private int port = 1337;
	private HashMap<String, Channel> channels = new HashMap<>();

	public Server(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		
		try {
			SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			ServerSocket s = ssf.createServerSocket(this.port);
			
			Socket clientsock = null;
			Client client;
		
			while (true) {
				try {
					client = new Client(this, clientsock = s.accept());
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
					Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Captured IO Exception while initializing the connection", e);
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
