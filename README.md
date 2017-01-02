# simplegram
A super simple and basic Telegram Bot library for Java.

This is a simple wrapper around pengrad's [java-telegram-bot-api](https://github.com/pengrad/java-telegram-bot-api) to quickly hack telegram bot support to your programs. Only support private messages and simple text.


Usage:
```
import com.github.msx80.simplegram.*;

public class BotMain {

	private static void receive(Message m)
	{
		m.getBot().send(m.getUserId(), "your message: "+m);
	}
	
	public static void main(String[] args) 
	{
		Bot bot = new Bot("your token", BotMain::receive);
		bot.run();
	}
}
```