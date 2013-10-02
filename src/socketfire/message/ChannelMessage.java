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
public class ChannelMessage extends Message {

	public ChannelMessage(Client client, String action, String[] args) {
		super(2, null, client);
	}
	
}
