package microservicesarch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import microservicesarch.model.ServiceInfo;

@Service
public class Caller2Service {
	@Autowired
	private RestTemplate restTemplate;
	
	@LoadBalanced
	public List<String> callServiceWithManualRetry(ServiceInfo service) {
		List<String> info = new java.util.ArrayList<String>();
		
		boolean retry = true;
		int errorCount = 0;
		while (retry) {
			try {
				ResponseEntity<String> response = restTemplate.getForEntity("http://microservice1/doSomeWork",String.class);
				info.add(response.getBody());
				retry = false;
				System.out.println("Execution ok");
			} catch (Exception e) {
				System.out.println("Error to invoke Serviece");
				errorCount++;
				if (errorCount >= 3) {
					String msg = "Three attemps to execute service have failed.";
					System.out.println(msg);
					info.add(msg);
					break;
				}
			}
		}
		return info;
	}
}
