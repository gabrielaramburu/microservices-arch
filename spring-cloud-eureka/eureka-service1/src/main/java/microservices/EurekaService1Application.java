package microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableAspectJAutoProxy
public class EurekaService1Application {

	public static void main(String[] args) {
		SpringApplication.run(EurekaService1Application.class, args);
	}

	@Bean
	public ServletWebServerApplicationContext getServletWebServerApplicationContext() {
		return new ServletWebServerApplicationContext();
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
