package fr.utc.chat;

import fr.utc.chat.configuration.WebSocketConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//@SpringBootApplication
public class ChatApplication {

	public static void main(String[] args) {

//		SpringApplication.run(ChatApplication.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(ChatApplication.class, args);

		// 注册WebSocket配置类
		WebSocketConfig webSocketConfig = context.getBean(WebSocketConfig.class);
		webSocketConfig.serverEndpointExporter();
	}

}
