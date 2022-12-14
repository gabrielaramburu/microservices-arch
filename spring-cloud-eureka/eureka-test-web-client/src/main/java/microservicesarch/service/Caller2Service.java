package microservicesarch.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private Logger log = LoggerFactory.getLogger(Caller2Service.class);
	
	
	public List<String> callServiceWithManualRetry(ServiceInfo service) {
		List<String> info = new java.util.ArrayList<String>();
		
		boolean retry = true;
		int errorCount = 0;
		while (retry) {
			try {
				ResponseEntity<String> response = restTemplate.getForEntity("http://microservice1/doSomeWork",String.class);
				info.add(response.getBody());
				retry = false;
				log.info("Execution ok");
			} catch (Exception e) {
				log.error("Error to invoke Serviece");
				errorCount++;
				if (errorCount >= 3) {
					String msg = "Three attemps to execute service have failed.";
					log.error(msg);
					info.add(msg);
					break;
				}
			}
		}
		return info;
	}
}
