/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socketfire.message;

import socketfire.Client;

/**
 *
 * @author cesar
 */
public class STDMessage extends Message {

	public STDMessage(Client client, String payload) {
		super(Message.TYPE_CHAT, payload, client);
	}
	
	public String getMessage() {
		return (String)this.getPayload();
	}
	
}
