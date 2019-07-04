/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import socketfire.handshake.SpecialHeaderException;
import socketfire.http.Header;

/**
 *
 * @author César de la Cal Bretschneider <cesar@magic3w.com>
 */
public class RequestFactory {
	
	public Request receive(Socket incoming) throws IOException, SpecialHeaderException {
		/*
		 * We start a buffered reader that will start off by receiving all the 
		 * information that the client wishes to send.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
		String msg;
		
		/*
		 * Any HTTP request will be formatted the following way:
		 * [method] [path] HTTP/[v]
		 * header:content
		 * header:content
		 * 
		 * body
		 *
		 * So we will need the following variables to make it work
		 */
		String method = null;
		String path = null;
		String version = null;
		ArrayList<Header> headers = new ArrayList<>();
		int length = 0;
		String body = null;
		
		/*
		 * This is a very basic mechanism for parsing the requests, but hopefully
		 * we'll get some mileage out of it.
		*/
		while (null != (msg = reader.readLine())) {
			
			if (method == null) {
				String[] pieces = msg.split(" ");
				method  = pieces[0];
				path    = pieces[1];
				version = pieces[2];
			}
			else {
				if (msg.trim().equals("")) {
					break;
				} 
				
				Header header = new Header(msg);
				headers.add(header);
				
				if (header.getKey().equals("Content-Length")) {
					length = Integer.parseInt(header.getValue());
				}
			}
		}
		
		if (length > 0) {
			char[] buffer = new char[length];
			reader.read(buffer, 0, length);
			body = new String(buffer);
		}
		
		
		return new Request(path, headers, body);
	}
}
