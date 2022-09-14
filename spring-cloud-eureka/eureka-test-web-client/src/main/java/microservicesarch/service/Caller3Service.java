package microservicesarch.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import microservicesarch.model.ServiceInfo;

@Service
public class Caller3Service {
	private final static String RETRY_NAME = "loadBalancerRetry" ;
	
	@Autowired
	private RestTemplate restTemplate;
	
	/* This is mandatory for managin @Retry events*/
	@Autowired
	private RetryRegistry registry;
	
	@PostConstruct
	public void postConstruct() {
		/* registering event for @Replay */
	    registry
	        .retry(RETRY_NAME)
	        .getEventPublisher()
	        .onRetry(System.out::println);
	}
	
	
	@LoadBalanced
	@Retry(name = RETRY_NAME, fallbackMethod = "fallbackMethodRetry")
	public List<String> callServiceWithResilience4jRetry(ServiceInfo service) {
		List<String> info = new java.util.ArrayList<String>();
		ResponseEntity<String> response = restTemplate.getForEntity("http://microservice1/doSomeWork",String.class);
		info.add(response.getBody());
		
		return info;
	}
	
	public String fallbackMethodRetry(@ModelAttribute("service") ServiceInfo service, RedirectAttributes model, RuntimeException e) {
		System.out.println("It was ipossible to call service. Max retry attempts reached."); 
		return "error";
	}

	
}
