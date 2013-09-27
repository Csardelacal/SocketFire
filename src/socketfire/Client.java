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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cesar
 */
public class Client extends Thread {
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Server server;
	
	public static final int MASK_SIZE = 4;
	public static final int SINGLE_FRAME_UNMASKED = 0x81;
	
	public Client(Server server, Socket s) throws IOException {
		this.server = server;
		this.socket = s;
		this.in  = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.out = new PrintWriter(this.socket.getOutputStream(), true);
		
		String headers = "";
		String read    = "";
		
		do {
			read = this.in.readLine();
			headers += read + "\n";
		}
		while (!read.trim().equals(""));
		
		headers = new Handshake(headers).getHeaders();
		System.out.print(headers);
		this.out.write(headers);
		this.out.flush();
		//this.send("Welcome to the EQTV server");
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
		this.server.broadcast(s);
	}
	
	@Override
	public void run() {
		String msg = "";
		try {
			InputStream raw = this.socket.getInputStream();
			int buffLength  = 4096;

			int len = 0;            
			byte[] b = new byte[buffLength];
			//rawIn is a Socket.getInputStream();
			while(true){
				 len = raw.read(b);
				 if(len!=-1){

					byte rLength = 0;
					int rMaskIndex = 2;
					int rDataStart = 0;
					//b[0] is always text in my case so no need to check;
					byte data = b[1];
					byte op = (byte) 127;
					rLength = (byte) (data & op);

					if(rLength==(byte)126) rMaskIndex=4;
					if(rLength==(byte)127) rMaskIndex=10;

					byte[] masks = new byte[4];

					int j=0;
					int i=0;
					for(i=rMaskIndex;i<(rMaskIndex+4);i++){
						 masks[j] = b[i];
						 j++;
					}

					rDataStart = rMaskIndex + 4;

					int messLen = len - rDataStart;

					byte[] message = new byte[messLen];

					for(i=rDataStart, j=0; i<len; i++, j++){
						 message[j] = (byte) (b[i] ^ masks[j % 4]);
					}

					parseMessage(new String(message)); 
					//parseMessage(new String(b));

					b = new byte[buffLength];

				}
			}
			
			
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void convertAndPrint(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02X ", b));
    }
    System.out.println(sb.toString());
    }
	
}
