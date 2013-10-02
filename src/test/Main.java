/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import socketfire.MessageEventListener;
import socketfire.Server;
import socketfire.message.STDMessage;

/**
 *
 * @author cesar
 */
public class Main {
	
	public static void main(String[] args) {
		Server s = new Server(1337);
		s.addMessageListener(new MessageEventListener<STDMessage>() {

			@Override
			public STDMessage onMessage(STDMessage message) {
				message.setPayload(message.getMessage().replaceAll("hello", "hello world"));
				return message;
			}
		});
		
		s.start();
	}
	
}
