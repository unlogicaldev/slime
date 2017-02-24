package io.slime.chat.sockjs.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.slime.chat.sockjs.eventbridge.EventBridgeChain;
import io.slime.chat.sockjs.eventbridge.chain.ChatMessageChainHandler;
import io.slime.chat.sockjs.eventbridge.chain.LoginChainHandler;
import io.slime.chat.sockjs.eventbridge.chain.OfflineChainHandler;


@Configuration
public class SpringConfigurationSockjs {
	@Bean
	public EventBridgeChain getEventBridgeChain() {
		EventBridgeChain eventBridgeChain = new EventBridgeChain();
		
		eventBridgeChain.regisger(getLoginChainHandler());
		eventBridgeChain.regisger(getChatMessageChainHandler());
		eventBridgeChain.regisger(getOfflineChainHandler());
		
		return eventBridgeChain;
	}
	
	@Bean
	public ChatMessageChainHandler getChatMessageChainHandler() {
		return new ChatMessageChainHandler();
	}
	
	
	@Bean
	public LoginChainHandler getLoginChainHandler() {
		return new LoginChainHandler();
	}
	
	@Bean
	public OfflineChainHandler getOfflineChainHandler() {
		return new OfflineChainHandler();
	}

}
