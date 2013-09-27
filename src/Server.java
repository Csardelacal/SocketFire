/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cesar
 */
public class Server extends Thread {
	
	private int port;
	private ArrayList<Client> clients = new ArrayList<>();

	public Server(int port) {
		this.port = port;
	}
	
	public void broadcast(String message) {
		int l = this.clients.size();
		for (int i = 0; i < l; i++) {
			try {
				this.clients.get(i).send(message);
			} catch (IOException ex) {
				Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	@Override
	public void run() {
		try {
			ServerSocket s = new ServerSocket(this.port);
			Client client;
		
			while (true) {
				client = new Client(this, s.accept());
				System.out.println("Client connected");
				client.start();
				System.out.println("Client started");
				this.clients.add(client);
			}
			
		} catch (IOException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
