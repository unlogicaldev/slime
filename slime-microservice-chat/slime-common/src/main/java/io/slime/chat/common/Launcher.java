package io.slime.chat.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.Scanner;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.slime.chat.common.redis.RedisDaemonClient;
import io.slime.chat.common.redis.RedisHttpClient;
import io.slime.chat.common.redis.RedisSockjsClient;
import io.slime.chat.common.spring.SpringConfiguration;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

public class Launcher extends io.vertx.core.Launcher {

  public static void main(String[] args) {
    new Launcher().dispatch(args);
  }

  @SuppressWarnings("resource")
  @Override
  public void beforeStartingVertx(VertxOptions options) {
	new AnnotationConfigApplicationContext(SpringConfiguration.class);
//    options.setClustered(true)
//        .setClusterHost("127.0.0.1");
  }

  @Override
  public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
    super.beforeDeployingVerticle(deploymentOptions);

    if (deploymentOptions.getConfig() == null) {
      deploymentOptions.setConfig(new JsonObject());
    }

    File conf = new File("src/conf/config.json");
    deploymentOptions.getConfig().mergeIn(getConfiguration(conf));
    
    File conf_properties = new File("src/conf/config_properties.json");
    JsonObject properties = getConfiguration(conf_properties);
    if(properties.containsKey("type")){
    	switch(properties.getString("type")){
	    	case "sockjs":
				try {
					InetAddress Address = InetAddress.getLocalHost();
					new RedisSockjsClient("localhost", 6379, Address.getHostAddress()+":"+properties.getInteger("port"));
				} catch (Exception e) {
					e.printStackTrace();
				}
	    		break;
	    	case "http":
				try {
					InetAddress Address = InetAddress.getLocalHost();
					new RedisHttpClient("localhost", 6379, Address.getHostAddress()+":"+properties.getInteger("port"));
				} catch (Exception e) {
					e.printStackTrace();
				}
	    		break;
	    	case "daemon":
				try {
					InetAddress Address = InetAddress.getLocalHost();
					new RedisDaemonClient("localhost", 6379, Address.getHostAddress()+":"+RedisDaemonClient.getPID());
				} catch (Exception e) {
					e.printStackTrace();
				}
		    	break;
    	}
    	
    }
    
  }

  private JsonObject getConfiguration(File config) {
    JsonObject conf = new JsonObject();
    if (config.isFile()) {
      System.out.println("Reading config file: " + config.getAbsolutePath());
      try (Scanner scanner = new Scanner(config).useDelimiter("\\A")) {
        String sconf = scanner.next();
        try {
          conf = new JsonObject(sconf);
        } catch (DecodeException e) {
          System.err.println("Configuration file " + sconf + " does not contain a valid JSON object");
        }
      } catch (FileNotFoundException e) {
        // Ignore it.
      }
    } else {
      System.out.println("Config file not found " + config.getAbsolutePath());
    }
    return conf;
  }
}
