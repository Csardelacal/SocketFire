/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire;

import java.util.ArrayList;
import socketfire.message.Message;

/**
 * Any class that inherits this one is a class that holds a list of clients and 
 * wants to allow sending data to those.
 *
 * @author cesar
 */
abstract public class Dispatcher {
	
	private final ArrayList<Client> clients = new ArrayList<>();
	private boolean silenced = false;
	
	public synchronized void addClient(Client c) {
		this.clients.add(c);
	}
	
	public void dropClient(Client c) {
		synchronized(this.clients) {
			this.clients.remove(c);
		}
	}
	
	public void dispatch(Message m) {
		
		if (this.isSilenced()) {
			return;
		}
		
		if (m.getTarget() != null) {
			if (this.clients.contains(m.getTarget())) {
				m.getTarget().send(m);
				return;
			}
		}
		
		synchronized(this.clients) {
			int l = this.clients.size();
			for (int i = 0; i < l; i++) {
				this.clients.get(i).send(m);
			}
		}
	}
	
	public void setSilenced(boolean silenced) {
		this.silenced = silenced;
	}

	public boolean isSilenced() {
		return this.silenced;
	}
	
}
