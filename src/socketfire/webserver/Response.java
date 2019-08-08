/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire.webserver;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import socketfire.http.Header;

/**
 *
 * @author César de la Cal Bretschneider <cesar@magic3w.com>
 */
public class Response {
	private String body;
	private ArrayList<Header> headers;

	public Response(ArrayList<Header> headers, String body) {
		this.body = body;
		this.headers = headers;
	}
	
	public void send(Socket socket) {
		try {
			BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
			
			OutputStreamWriter o = new OutputStreamWriter(socket.getOutputStream());
			o.write("HTTP/2.0 200 OK\n");
			
			this.headers.add(new Header("Content-length", Integer.toString(this.body.length())));
			
			for (int i = 0; i < this.headers.size(); i++) {
				o.write(this.headers.get(i).getKey() + ": " + this.headers.get(i).getValue());
				o.write("\n");
			}
			
			o.write("\n");
			o.write(this.body);
			o.flush();
		} catch (IOException ex) {
			Logger.getLogger(Response.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
