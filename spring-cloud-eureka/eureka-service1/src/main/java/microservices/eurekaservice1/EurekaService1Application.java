package microservices.eurekaservice1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EurekaService1Application {

	public static void main(String[] args) {
		SpringApplication.run(EurekaService1Application.class, args);
	}

}
