/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire.message;

import socketfire.websocket.Client;

/**
 *
 * @author cesar
 */
public class Message {
	
	private Client src;
	private Client target = null;
	
	private Object payload;
	private boolean bubble = true;
	
	
	public Message(Object payload, Client src) {
		this.payload = payload;
		this.src = src;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}
	
	public void stopPropagation() {
		this.bubble = false;
	}

	public boolean doesBubble() {
		return this.bubble;
	}

	public Client getSrc() {
		return src;
	}

	public void setSrc(Client src) {
		this.src = src;
	}

	public Client getTarget() {
		return target;
	}

	public void setTarget(Client target) {
		this.target = target;
	}
	
	public String getMessage() {
		return (String)this.getPayload();
	}
	
}
