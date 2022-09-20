package microservicesarch.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import microservicesarch.model.ServiceInfo;

@Service
public class Caller3Service {
	private final static String RETRY_NAME = "loadBalancerRetry" ;
	
	private static Logger log = LoggerFactory.getLogger(Caller3Service.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	/* This is mandatory for managing @Retry events*/
	@Autowired
	private RetryRegistry registry;
	
	@PostConstruct
	public void postConstruct() {
		/* This is call only when the internal reply reaches the max attends.*/
	    registry
	        .retry(RETRY_NAME)
	        .getEventPublisher()
	        .onRetry(s -> log.warn("Request failed (" + RETRY_NAME +") "+ s));
	}
	
	
	
	@Retry(name = RETRY_NAME, fallbackMethod = "fallbackMethodRetry")
	public List<String> callServiceWithResilience4jRetry(ServiceInfo service) {
		List<String> info = new java.util.ArrayList<String>();
		ResponseEntity<String> response = restTemplate.getForEntity("http://microservice1/doSomeWork",String.class);
		info.add(response.getBody());
		
		return info;
	}


	public List<String> fallbackMethodRetry(ServiceInfo service, RuntimeException e) {
		log.error("Imposible to call service after several intents." + Thread.currentThread().getName());
		List<String> info = new java.util.ArrayList<String>();
		info.add("error");
		return info;
	}
	
}
