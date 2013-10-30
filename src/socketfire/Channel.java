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
import socketfire.message.Message;

/**
 *
 * @author cesar
 */
public class Channel extends Dispatcher {
	
	private Server server;
	private String name;

	public Channel(Server server, String name) {
		this.server = server;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
