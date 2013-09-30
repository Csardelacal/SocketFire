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
	
	private final Socket socket;
	private final ArrayList<String> messages = new ArrayList<>();
	public static final int SINGLE_FRAME_UNMASKED = 0x81;
	
	public Queue(Socket socket) {
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
		
		this.send(this.messages.remove(0));
		
	}
	
	public synchronized void queueMessage(String message) {
		this.messages.add(message);
		notify();
	}
	
	public void send(String message) throws IOException {
		
		byte[] msg = message.getBytes();
		//System.out.println("Sending to client");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream os = new BufferedOutputStream(socket.getOutputStream());
		//first byte is kind of frame
		baos.write(SINGLE_FRAME_UNMASKED);

		//Next byte is length of payload
		baos.write(msg.length);

		//Then goes the message
		baos.write(msg);
		baos.flush();
		baos.close();
		//This function only prints the byte representation of the frame in hex to console
		//convertAndPrint(baos.toByteArray());

		//Send the frame to the client
		os.write(baos.toByteArray(), 0, baos.size());
		os.flush();
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
