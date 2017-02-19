package io.slime.chat.common.util;

import io.vertx.core.Vertx;

public class VertxHolder {
	private static Vertx vertx;

	public static Vertx getVertx() {
		return vertx;
	}

	public static void setVertx(Vertx vertx) {
		VertxHolder.vertx = vertx;
	}
	
	
}