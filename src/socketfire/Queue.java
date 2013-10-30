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
import org.json.JSONException;
import org.json.JSONObject;
import socketfire.message.Message;

/**
 *
 * @author cesar
 */
public class Queue extends Thread {
	
	private final SocketAdapter socket;
	private final ArrayList<Message> messages = new ArrayList<>();
	public static final int SINGLE_FRAME_UNMASKED = 0x81;
	public static int id = 0;
	
	public Queue(SocketAdapter socket) {
		this.socket = socket;
		this.setName("Queue" + (id++));
	}
	
	public synchronized boolean dispatch() throws IOException {
		
		try {
			while (this.messages.isEmpty()) {
				wait();
			} 
		} catch (InterruptedException ex) {
			//The thread was interrupted, silent failure.
			System.out.println("Thread interrupted");
			return false;
		}
		
		Message msg = this.messages.remove(0);
		JSONObject send = new JSONObject();
		try {
			send.put("type",    msg.getType());
			
			if (msg.getPayload() instanceof String) {
				send.put("payload", msg.getPayload());
			} else {
				JSONObject payload = (JSONObject)msg.getPayload();
				send.put("action", payload.get("action"));
				send.put("args",   payload.get("args"));
			}
			
			if (msg.getSrc() != null) {
				send.put("src",     msg.getSrc().getClientName());
			}
		} catch (JSONException e) {}
		this.socket.write(send.toString());
		
		return true;
		
	}
	
	public synchronized void queueMessage(Message message) {
		this.messages.add(message);
		notify();
	}
	
	public void run() {
		try {
			while (this.dispatch()) {}
			System.out.println("Finished queue");
		} catch (IOException ex) {
			Logger.getLogger(Queue.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
