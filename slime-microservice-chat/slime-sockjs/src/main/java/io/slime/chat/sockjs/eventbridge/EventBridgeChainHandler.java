package io.slime.chat.sockjs.eventbridge;

import io.vertx.ext.web.handler.sockjs.BridgeEvent;

public interface EventBridgeChainHandler {
	boolean handle(BridgeEvent event) throws EventBridgeChainException;
}
