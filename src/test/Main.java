/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import socketfire.MessageEventListener;
import socketfire.Server;
import socketfire.message.ChannelMessage;
import socketfire.message.Message;
import socketfire.message.STDMessage;
import socketfire.message.UserMessage;

/**
 *
 * @author cesar
 */
public class Main {
	
	public static void main(String[] args) {
		Server s = new Server(1337);
		
		s.addMessageListener(Message.TYPE_CHAT, new MessageEventListener<STDMessage>() {

			@Override
			public void onMessage(STDMessage message) {
				message.setPayload(message.getMessage().replaceAll("hello", "hello world"));
			}
		});
		
		s.addMessageListener(Message.TYPE_CHANNEL, new MessageEventListener<ChannelMessage>() {

			@Override
			public void onMessage(ChannelMessage message) {
				if (message.getAction().equals("silence")) {
					message.getSrc().getChannel().setSilenced(true);
				}
				if (message.getAction().equals("unsilence")) {
					message.getSrc().getChannel().setSilenced(false);
				}
			}

		});
		
		s.addMessageListener(Message.TYPE_CHANNEL, new MessageEventListener<ChannelMessage>() {

			@Override
			public void onMessage(ChannelMessage message) {
				if (message.getAction().equals("play")) {
					message.getSrc().getChannel().dispatch(message);
				}
				if (message.getAction().equals("pause")) {
					message.getSrc().getChannel().dispatch(message);
				}
			}

		});
		
		s.addMessageListener(Message.TYPE_USER, new MessageEventListener<UserMessage>() {

			@Override
			public void onMessage(UserMessage message) {
				if (message.getAction().equals("setUsername")) {
					String name = message.getArgs()[0];
					if (name != null && !name.equals("")) {
						message.getSrc().setClientName(name);
					}
					
					UserMessage answer = new UserMessage(null, "confirm", new String[]{"Username set"});
					answer.setTarget(message.getSrc());
					message.getSrc().getServer().dispatch(answer);
					
					message.stopPropagation();
				}
			}
		});
		
		new Thread(s).start();
	}
	
}
