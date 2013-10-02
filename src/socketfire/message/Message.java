/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire.message;

import socketfire.Client;

/**
 *
 * @author cesar
 */
public abstract class Message {
	
	public static final int TYPE_CHAT    = 0;
	public static final int TYPE_AUTH    = 1;
	public static final int TYPE_CHANNEL = 2;
	public static final int TYPE_SERVER  = 3;
	
	private Object payload;
	private int type;
	private Client src;
	
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
	
}
