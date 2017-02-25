package io.slime.chat.http;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.slime.chat.common.spring.SpringConfiguration;
import io.slime.chat.common.spring.service.RequestLogService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

public class HttpServer extends AbstractVerticle {
	private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
	private int serverPort = 8080;
	
	private RequestLogService requestLogService = (RequestLogService) SpringConfiguration.getBean("requestLogService");

	@Override
	public void start() throws Exception {
		
		logger.info("HTTP Server started on port: {}", serverPort);

		vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {

			public void handle(HttpServerRequest req) {
				
				requestLogService.print(String.format("Request on path: %s", req.path()));
				
				String file = req.path().equals("/") ? "/chat/index.html" : req.path();
				
				//send file and handle file not found
				req.response().sendFile("webroot" + file, new Handler<AsyncResult<Void>>() {
					@Override
					public void handle(AsyncResult<Void> event) {
						if(event.cause() instanceof FileNotFoundException) {
							
							logger.error("Resource not found: {}", "webroot" + file);
							
							req.response().sendFile("webroot/error/404.html");
						}
					}
				});
				
				
			}

		}).listen(serverPort);
		
	}
}
