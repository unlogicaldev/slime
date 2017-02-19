package io.slime.chat.sockjs.eventbridge;

public class EventBridgeChainException extends Throwable {
	private static final long serialVersionUID = -4375961428621249366L;
	
	private boolean proccessed;
	
	
	public EventBridgeChainException(boolean isProccess, String message) {
		super(message);
		this.proccessed = isProccess;
	}
	
	public EventBridgeChainException(boolean isProccess) {
		super();
		this.proccessed = isProccess;
	}

	public boolean isProccessed() {
		return proccessed;
	}

	public void setProccessed(boolean proccessed) {
		this.proccessed = proccessed;
	}
	
	
}
