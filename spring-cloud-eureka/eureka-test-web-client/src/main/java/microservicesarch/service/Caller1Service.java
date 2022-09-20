package microservicesarch.service;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import microservicesarch.model.ServiceInfo;

@Service
public class Caller1Service {
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	private Logger log = LoggerFactory.getLogger(Caller1Service.class);
	
	public List<String> obtainServiceInstances(ServiceInfo service) {
		List<String> info = new java.util.ArrayList<String>();
		
		List<ServiceInstance> instances = discoveryClient.getInstances(service.getServiceName());
		if (instances.size() == 0) {
			info.add("There are not avialable instance's service.");
		} else {
			for (ServiceInstance instance: instances) {
				
				String serviceInfo = instance.getUri().toString();
						 
				log.info("Instance: " + serviceInfo);
				info.add(serviceInfo);
			}
		}
		
		return info;
	}
	
	
	public List<String> invokeServiceInstance(ServiceInfo service) {
		List<String> info = new java.util.ArrayList<String>();
		URI instanceUri = getInstanceUri(service);
		if (instanceUri == null) {
			info.add("There is not an instance [" + service.getServiceInstanceIndex() + "] form service: " + service.getServiceName());
		} else {
			//If we allow Spring to instantiate a RestTemplate, it will hava a LoadBalancer intercerptor injected in the instance
			//Therefore, for testing purpose,  we need to create our instance to avoid that behavior.
			//It is important to clarify that the injected load balancer does not works as expected.(round robbin)
			log.info(instanceUri.toString());
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.getForEntity(instanceUri.toString()+"/doSomeWork",String.class);
			info.add(response.getBody());
		}
		
		return info;
	}
	
	private URI getInstanceUri(ServiceInfo service) {
		List<ServiceInstance> instances = discoveryClient.getInstances(service.getServiceName());
		log.info(instances.toString());
		if (service.getServiceInstanceIndex() >= instances.size()) return null;
		else return instances.get(service.getServiceInstanceIndex()).getUri();
	}
	
}
