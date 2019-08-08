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
public abstract class Message {
	
	public static final int TYPE_CHAT    = 0;
	public static final int TYPE_AUTH    = 1;
	public static final int TYPE_USER    = 2;
	public static final int TYPE_CHANNEL = 3;
	public static final int TYPE_SERVER  = 4;
	
	private int type = 0;
	private Client src;
	private Client target = null;
	
	private Object payload;
	private boolean bubble = true;
	
	
	public Message(int type, Object payload, Client src) {
		this.payload = payload;
		this.type = type;
		this.src = src;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
	
	
	
}
