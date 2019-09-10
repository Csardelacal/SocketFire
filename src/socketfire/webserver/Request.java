/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketfire.webserver;

import java.util.ArrayList;
import socketfire.http.Header;

/**
 *
 * @author César de la Cal Bretschneider <cesar@magic3w.com>
 */
public class Request {
	
	private final String path;
	private final ArrayList<Header> headers;
	private final String body;
	
	public Request(String path, ArrayList<Header> headers, String body) {
		this.path = path;
		this.headers = headers;
		this.body = body;
	}

	public String getBody() {
		return this.body;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public ArrayList<Header> getHeaders() {
		return this.headers;
	}
}
