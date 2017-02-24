package io.slime.chat.common.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("io.slime.chat")
public class SpringConfiguration implements ApplicationContextAware{
	private static ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		context = ac;
	}
	
	public static Object getBean(String beanName){
		return context.getBean(beanName);
	}
	
	public static <T> Object getBean(Class<T> arg){
		return context.getBean(arg);
	}
}
