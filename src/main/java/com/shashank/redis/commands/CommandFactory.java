package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

public class CommandFactory {
	
	private final Map<Command, Handler> commandHandlers = new EnumMap<>(Command.class);
	
	public CommandFactory(ObjectFactory objectFactory) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		initHandlers(objectFactory);
	}
	
	private void initHandlers(ObjectFactory objectFactory) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		for (Command command : Command.values()) {
			Constructor<? extends Handler> handlerConstructor = command.handler.getConstructor(ObjectFactory.class);
			Handler handler = handlerConstructor.newInstance(objectFactory);
			commandHandlers.put(command, handler);
		}
	}
	
	public Handler getCommandHandler(String command) {
		Handler handler = commandHandlers.get(Command.valueOf(command));
		if (handler == null) {
			throw new RuntimeException("Unknown command: " + command);
		}
		return handler;
	}
	
	private enum Command {
		
		ECHO(Echo.class), PING(Ping.class), SET(Set.class), GET(Get.class), INFO(Info.class), REPLCONF(ReplConf.class), PSYNC(PSync.class);
		
		private final Class<? extends Handler> handler;
		
		Command(Class<? extends Handler> handler) {
			this.handler = handler;
		}
		
	}
	
}
