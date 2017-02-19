package io.slime.chat.sockjs.eventbridge;

import java.util.ArrayList;
import java.util.List;

import io.vertx.ext.web.handler.sockjs.BridgeEvent;

public class EventBridgeChain {
	List<EventBridgeChainHandler> handlers = new ArrayList<>();
	
	public void regisger(EventBridgeChainHandler handler) {
		handlers.add(handler);
	}
	
	
	public EventBridgeChainResponse processInChain(BridgeEvent event) {
		EventBridgeChainResponse response = new EventBridgeChainResponse();
		
		for (EventBridgeChainHandler handler : handlers) {
			boolean handle = Boolean.FALSE;
			try {
				handle = handler.handle(event);
				if(handle) {
					response.setProcesssed(Boolean.TRUE);
					response.setHandler(handler);
					break;
				}
			} catch (EventBridgeChainException e) {
				handle = e.isProccessed();
				if(handle) {
					response.setHandler(handler);
					response.setException(e);
					response.setProcesssed(Boolean.TRUE);
					break;
				}
			}
		}
		
		
		return response;
	}
}
