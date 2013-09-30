package socketfire;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import socketfire.handshake.MalformedHeaderException;

/**
 *
 * @author cesar
 */
public class Server extends Thread {
	
	private int port;
	private HashMap<String, Channel> channels = new HashMap<>();
	private ArrayList<Client> clients = new ArrayList<>();

	public Server(int port) {
		this.port = port;
	}
	
	public void broadcast(String message) {
		int l = this.clients.size();
		for (int i = 0; i < l; i++) {
			this.clients.get(i).send(message);
		}
	}
	
	@Override
	public void run() {
		try {
			ServerSocket s = new ServerSocket(this.port);
			Client client;
		
			while (true) {
				try {
					client = new Client(this, s.accept());
					System.out.println("Client connected");
					client.start();
					System.out.println("Client started");
					this.clients.add(client);
				} catch (MalformedHeaderException ex) {
					Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
			Channel channel = new Channel(this, id);
			this.channels.put(id, channel);
			return channel;
		}
	}

	public void dropClient(Client client) {
		this.clients.remove(client);
	}
}
