package io.slime.chat.sockjs.eventbridge;

public class EventBridgeChainResponse {
	private EventBridgeChainHandler handler;
	private boolean processsed;
	private EventBridgeChainException exception;
	
	public EventBridgeChainResponse() {}
	
	/*public EventBridgeChainResponse(EventBridgeChainHandler handler, boolean isProccessed) {
		this.processsed = isProccessed;
		this.handler = handler;
	}
	
	
	public EventBridgeChainResponse(EventBridgeChainHandler handler, EventBridgeChainException exception) {
		this.processsed = Boolean.FALSE;
		this.handler = handler;
		this.exception = exception;
	}*/
	
	public EventBridgeChainHandler getHandler() {
		return handler;
	}
	public void setHandler(EventBridgeChainHandler handler) {
		this.handler = handler;
	}
	
	
	
	public boolean isProcesssed() {
		return processsed;
	}

	public void setProcesssed(boolean processsed) {
		this.processsed = processsed;
	}

	public EventBridgeChainException getException() {
		return exception;
	}
	public void setException(EventBridgeChainException exception) {
		this.exception = exception;
	}
	
}
