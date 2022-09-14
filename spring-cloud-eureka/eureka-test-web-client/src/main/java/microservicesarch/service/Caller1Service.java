package microservicesarch.service;

import java.net.URI;
import java.util.List;

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
	
	
	public List<String> obtainServiceInstances(ServiceInfo service) {
		List<String> info = new java.util.ArrayList<String>();
		
		List<ServiceInstance> instances = discoveryClient.getInstances(service.getServiceName());
		if (instances.size() == 0) {
			info.add("There are not avialable instance's service.");
		} else {
			for (ServiceInstance instance: instances) {
				
				String serviceInfo = instance.getUri().toString();
						 
				System.out.println("Instance: " + serviceInfo);
				info.add(serviceInfo);
			}
		}
		
		return info;
	}
	
	
	public List<String> invokeServiceInstance(ServiceInfo service) {
		List<String> info = new java.util.ArrayList<String>();
		URI instanceUri = getInstanceUri(service);
		if (instanceUri == null) {
			info.add("There are not instances with the name: " + service.getServiceName());
		} else {
			//If we allow Spring to instantiate a RestTemplate, it will hava a LoadBalancer intercerptor injected in the instance
			//Therefore, for testing purpose,  we need to create our instance to avoid that behavior.
			//It is important to clarify that the injected load balancer does not works as expected.(round robbin)
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.getForEntity(instanceUri.toString(),String.class);
			info.add(response.getBody());
		}
		
		return info;
	}
	
	private URI getInstanceUri(ServiceInfo service) {
		List<ServiceInstance> instances = discoveryClient.getInstances(service.getServiceName());
		if (service.getServiceInstanceIndex() >= instances.size()) return null;
		else return instances.get(service.getServiceInstanceIndex()).getUri();
	}
	
}
