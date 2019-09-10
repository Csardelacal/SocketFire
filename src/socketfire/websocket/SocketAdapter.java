/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire.websocket;

import socketfire.websocket.Client;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import socketfire.Incoming;
import static socketfire.websocket.Client.MASK_SIZE;
import static socketfire.Queue.SINGLE_FRAME_UNMASKED;

/**
 *
 * @author cesaradelacalbretschneider
 */
public class SocketAdapter {
	
	private Socket socket;
	private BufferedOutputStream out;
	private InputStreamReader in;
	private boolean requestedClosing = false;
	private Client client;
	
	public SocketAdapter(Socket socket) throws IOException {
		this.socket = socket;
		this.client = null;
		this.out = new BufferedOutputStream(socket.getOutputStream());
		this.in  = new InputStreamReader(socket.getInputStream());
	}
	
	public void write(String message) throws IOException {
		synchronized (this) {
			
			if (this.socket.isClosed()) {
				this.shutdown();
				return;
			}
			
			byte[] msg = message.getBytes();
			//System.out.println("Sending to client");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedOutputStream os = this.out;
			//first byte is kind of frame
			baos.write(SINGLE_FRAME_UNMASKED);

			//Next byte is length of payload
			System.out.println("Length" + msg.length);
			if (msg.length > 65536) {
				baos.write(127);
				baos.write((msg.length / (int)Math.pow(2, 56)) & 0x7F);
				baos.write((msg.length / (int)Math.pow(2, 48)) & 0xFF);
				baos.write((msg.length / (int)Math.pow(2, 40)) & 0xFF);
				baos.write((msg.length / (int)Math.pow(2, 32)) & 0xFF);
				baos.write((msg.length / (int)Math.pow(2, 24)) & 0xFF);
				baos.write((msg.length / (int)Math.pow(2, 16)) & 0xFF);
				baos.write((msg.length / (int)Math.pow(2,  8)) & 0xFF);
				baos.write(msg.length & 0xFF);
			}
			else if (msg.length > 125) {
				baos.write(126);
				baos.write(msg.length / 256);
				baos.write(msg.length & 0xFF);
			}
			else {
				baos.write(msg.length);
			}

			//Then goes the message
			baos.write(msg);
			baos.flush();
			baos.close();

			//Send the frame to the client
			int max = 4096;
			int current = 0;
			int length = baos.size();
			byte[] ba = baos.toByteArray();
			
			while (current < length) {
				os.write(ba, current, Math.min(length, current + max) - current);
				os.flush();
				current+= max;
			} 
		}
	}
	
	public Incoming read(Incoming read) {
		
		try {
			int buffLength  = 4096 * 2;

			int len = 0;            
			byte[] b = new byte[buffLength];
			//rawIn is a Socket.getInputStream();
			while(!this.socket.isClosed()){    //Read the first two bytes of the message, the frame type byte - and the payload length byte
				byte[] buf = readBytes(2);
				//System.out.println("Headers:");
				//Print them in nice hex to console
				//convertAndPrint(buf);
				//And it with 00001111 to get four lower bits only, which is the opcode
				int opcode = buf[0] & 0x0F;
				int msgend = buf[0] & 0x80;

				//Opcode 8 is close connection
				if (opcode == 8) {
					//Client want to close connection!
					System.out.println("Client closed!");
					//this.queue.interrupt();
					this.requestedClosing = true;
					socket.shutdownInput();
					this.shutdown();
					return null;
					//TODO: Server should unregister the client
				} 
				else if  (opcode == 0) {
					final int payloadSize = getSizeOfPayload(buf[1]);
					//System.out.println("Payloadsize: " + payloadSize);

					//Read the mask, which is 4 bytes, and than the payload
					buf = readBytes(MASK_SIZE + payloadSize);
					//System.out.println("Payload:");
					//convertAndPrint(buf);
					//method continues below!    
					buf = unMask(Arrays.copyOfRange(buf, 0, 4), Arrays.copyOfRange(buf, 4, buf.length));
					String message = new String(buf);
					read = new Incoming(message);
					read.setComplete(msgend != 0);
					
					return read;
				}
				//Else I just assume it's a single framed text message (opcode 1)
				else {
					int payloadSize = getSizeOfPayload(buf[1]);
					if (payloadSize == 126) {
						byte[] eps = new byte[2];
						socket.getInputStream().read(eps);
						payloadSize = (eps[0] & 0xFF) * 0x100 + (eps[1] & 0xFF);
					}
					//System.out.println("Payloadsize: " + payloadSize);

					//Read the mask, which is 4 bytes, and than the payload
					buf = readBytes(MASK_SIZE + payloadSize);
					//System.out.println("Payload:");
					//convertAndPrint(buf);
					//method continues below!    
					buf = unMask(Arrays.copyOfRange(buf, 0, 4), Arrays.copyOfRange(buf, 4, buf.length));
					String message = new String(buf);
					read = new Incoming(message);
					read.setComplete(msgend != 0);
					
					return read;
				}
			}
			
			
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
	
	private byte[] unMask(byte[] mask, byte[] data) {
        for (int i = 0; i < data.length; i++) {
              data[i] = (byte) (data[i] ^ mask[i % mask.length]);
        }
        return data;
	}
	
	private int getSizeOfPayload(byte b) {
		//Must subtract 0x80 from (unsigned) masked frames
		return ((b & 0xFF) - 0x80);
	}
	
	private byte[] readBytes(int numOfBytes) throws IOException {
		byte[] b = new byte[numOfBytes];
		socket.getInputStream().read(b);
		return b;
	}
	
	private void convertAndPrint(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			 sb.append(String.format("%02X ", b));
		}
		System.out.println(sb.toString());
    }

	private void shutdown() throws IOException {
		synchronized(this) {
			if (this.requestedClosing) {
				this.client.finish();
				socket.close();
				return;
			}
		}
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
}
