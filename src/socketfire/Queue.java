/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cesar
 */
public class Queue extends Thread {
	
	private final SocketAdapter socket;
	private final ArrayList<String> messages = new ArrayList<>();
	public static final int SINGLE_FRAME_UNMASKED = 0x81;
	
	public Queue(SocketAdapter socket) {
		this.socket = socket;
	}
	
	public synchronized void dispatch() throws IOException {
		
		while (this.messages.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ex) {
				//The thread was interrupted, silent failure.
			}
		}
		
		this.socket.write(this.messages.remove(0));
		
	}
	
	public synchronized void queueMessage(String message) {
		this.messages.add(message);
		notify();
	}
	
	public void run() {
		try {
			while (true) {
					this.dispatch();
			}
		} catch (IOException ex) {
			Logger.getLogger(Queue.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
