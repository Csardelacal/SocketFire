/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cesar
 */
public class Channel {
	
	private Server server;
	private String name;
	private final ArrayList<Client> clients = new ArrayList<>();

	public Channel(Server server, String name) {
		this.server = server;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void registerClient(Client client) {
		synchronized(this) {
			if (!this.clients.contains(client)) {
				this.clients.add(client);
			}
		}
	}
	
	public void dropClient(Client client) {
		synchronized(this) {
			if (this.clients.contains(client)) {
				this.clients.remove(client);
				this.server.dropClient(client);
			}
		}
	}

	public ArrayList<Client> getClients() {
		return clients;
	}

	void broadcast(String s) {
		synchronized(this) {
			int l = this.clients.size();
			for (int i = 0; i < l; i++) {
				this.clients.get(i).send(s);
			}
		}
	}
	
}
