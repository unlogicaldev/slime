package io.slime.chat.common.redis;

public enum RedisKeyStore {

	CHANNEL("CHANNEL:MASTER"), 
	CLIENT("CLIENT:MASTER"), 
	INSTANCE("CLIENT:INSTANCE:MASTER"), 
	DAEMON("DAEMON:MASTER"), 
	PUBLISH("REQUEST:PUBLISH:MASTER"), 
	REGIST("REQUEST:REGIST"), 
	VERTX("VERTX:MASTER"), 
	LOG("LOG:MASTER"), 
	HTTP("HTTP:MASTER");

	private String master;

	RedisKeyStore(String master) {
		this.master = master;
	}

	public String getMaster() {
		return this.master;
	}
}
