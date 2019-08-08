/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire.message;

import socketfire.websocket.Client;

/**
 *
 * @author cesar
 */
public class ServerMessage extends Message {

	public ServerMessage(Client client, String action, String[] args) {
		super(Message.TYPE_SERVER, null, client);
	}
	
}
