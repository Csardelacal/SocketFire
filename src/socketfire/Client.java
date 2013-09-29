package socketfire;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import socketfire.handshake.MalformedHeaderException;

/**
 *
 * @author cesar
 */
public class Client extends Thread {
	
	private final Socket socket;
	private final BufferedReader in;
	private final PrintWriter out;
	private final Server server;
	private final Channel channel;
	
	public static final int MASK_SIZE = 4;
	public static final int SINGLE_FRAME_UNMASKED = 0x81;
	
	public Client(Server server, Socket s) throws IOException, MalformedHeaderException {
		this.server = server;
		this.socket = s;
		this.in  = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.out = new PrintWriter(this.socket.getOutputStream(), true);
		
		String headers = "";
		String read;
		
		do {
			read = this.in.readLine();
			headers += read + "\n";
		}
		while (!read.trim().equals(""));
		
		Handshake handshake = new Handshake(headers);
		headers = handshake.getHeaders();
		this.channel = this.server.getChannel(handshake.getRequestHeader("_location").getValue());
		
		this.out.write(headers);
		this.out.flush();
		this.channel.registerClient(this);
		this.send("Welcome to the EQTV server");
		//this.out = null;
	}
	
	public void send(String message) throws IOException {
		byte[] msg = message.getBytes();
		System.out.println("Sending to client");
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
		convertAndPrint(baos.toByteArray());

		//Send the frame to the client
		os.write(baos.toByteArray(), 0, baos.size());
		os.flush();
	}
	
	public void parseMessage(String s) {
		System.out.println(s);
		this.channel.broadcast(s);
	}
	
	/**
	 * @see http://stackoverflow.com/questions/12702305/using-html5-client-with-a-server-in-java
	 */
	@Override
	public void run() {
		String msg = "";
		try {
			int buffLength  = 4096;

			int len = 0;            
			byte[] b = new byte[buffLength];
			//rawIn is a Socket.getInputStream();
			while(!this.socket.isClosed()){    //Read the first two bytes of the message, the frame type byte - and the payload length byte
				byte[] buf = readBytes(2);
				System.out.println("Headers:");
				//Print them in nice hex to console
				convertAndPrint(buf);
				//And it with 00001111 to get four lower bits only, which is the opcode
				int opcode = buf[0] & 0x0F;

				//Opcode 8 is close connection
				if (opcode == 8) {
					//Client want to close connection!
					System.out.println("Client closed!");
					socket.close();
					this.channel.dropClient(this);
					//TODO: Server should unregister the client
				} 
				//Else I just assume it's a single framed text message (opcode 1)
				else {
					final int payloadSize = getSizeOfPayload(buf[1]);
					System.out.println("Payloadsize: " + payloadSize);

					//Read the mask, which is 4 bytes, and than the payload
					buf = readBytes(MASK_SIZE + payloadSize);
					System.out.println("Payload:");
					convertAndPrint(buf);
					//method continues below!    
					buf = unMask(Arrays.copyOfRange(buf, 0, 4), Arrays.copyOfRange(buf, 4, buf.length));
					String message = new String(buf);
					this.parseMessage(message);
				}
			}
			
			
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
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
	
}
