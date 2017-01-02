package com.github.msx80.simplegram;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.function.Consumer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteWebhook;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

import okhttp3.OkHttpClient;

public class Bot {
	
	final TelegramBot bot;
	private Consumer<com.github.msx80.simplegram.Message> consumer;
	
	public Bot(String token, Consumer<com.github.msx80.simplegram.Message> consumer)
	{
		bot = TelegramBotAdapter.build(token);
		checkResult(bot.execute(new DeleteWebhook()));
		this.consumer = consumer;
		
	}
	
	private void checkResult(BaseResponse res) {
		if(!res.isOk())
		{
			throw new RuntimeException(res.errorCode()+ " "+res.description());
		}
		
	}

	public Bot(String token, String proxy, int proxyPort,  Consumer<com.github.msx80.simplegram.Message> consumer)
	{
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.proxy(new Proxy(Type.HTTP, InetSocketAddress.createUnresolved(proxy, proxyPort)));
		//builder.connectTimeout(600, TimeUnit.SECONDS);
		//builder.readTimeout(600, TimeUnit.SECONDS);
		bot = TelegramBotAdapter.buildCustom(token, builder.build());
		
		checkResult(bot.execute(new DeleteWebhook()));
		this.consumer = consumer;
		
	}
	
	
	
	
	public void run()
	{
		int lastUpdate = 0;
		while(true)
		{
			//System.out.println("START:"+Instant.now());
			GetUpdatesResponse res = bot.execute(new GetUpdates()
					.offset(lastUpdate)
					.limit(20)
					.timeout(600)
					.allowedUpdates("message"));
			//System.out.println("END:"+Instant.now());
			checkResult(res);
			for(Update u : res.updates())
			{
				lastUpdate = Math.max(lastUpdate, u.updateId()+1);
				// System.out.println(u); // consume
				
				if(u.message() != null)
				{
					if(u.message().chat().type() == com.pengrad.telegrambot.model.Chat.Type.Private)
					{
						if(u.message().text() != null)
						{
							try {
								com.github.msx80.simplegram.Message msg = new com.github.msx80.simplegram.Message(this, u.message().from().id(), u.message().from().username(), getDisplayName(u), u.message().text() );
								consume(msg);
							} catch (Exception e) {
								System.err.println("Error consuming message!");
								e.printStackTrace();
							}
						}
					}
				}
				
			}
		}
	}

	private String getDisplayName(Update u) {
		String f = u.message().from().firstName() == null ? "" : u.message().from().firstName();
		String l = u.message().from().lastName() == null ? "" : u.message().from().lastName();
		String res = (f+" "+l).trim();
		if(res.equals("")) res = "(no display name)";
		return res;
	}




	private void consume(com.github.msx80.simplegram.Message message) {
		
		consumer.accept(message);
		
	}

	public Integer send(Integer chatId, String text) {
		SendMessage request = new SendMessage(chatId, text);

		// sync
		SendResponse sendResponse = bot.execute(request);
		checkResult(sendResponse);
		Message message = sendResponse.message();
		return message.messageId();
	}
}
