package microservicesarch.controller;



import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import microservicesarch.model.Service;



@Controller
public class TestController {
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/main")
	public String mainForm(Model model) {
		System.out.println("Executing controller. ");
		
		if (model.getAttribute("service") == null) {
			microservicesarch.model.Service service = new microservicesarch.model.Service();
			service.setServiceName("microservice1");
			service.setServiceInstanceIndex(0);
			service.setMethodName("doSomeWork");
			model.addAttribute("service", service);
		}
		
		return "main";
	}
	
	
	@GetMapping("/servicesInstancesByName")
	public String obtainServiceInstances(@ModelAttribute("service") Service service, RedirectAttributes model) {
		System.out.println("Executing controller. ServiceName : " + service.getServiceName());
		
		List<String> info = new java.util.ArrayList<String>();
		List<ServiceInstance> instances = discoveryClient.getInstances(service.getServiceName());
		if (instances.size() == 0) {
			info.add("There are not avialable instance's service.");
		} else {
			for (ServiceInstance instance: instances) {
				
				//instance.getHost() + ":" + instance.getPort() + "/" +		instance.getInstanceId()
				String serviceInfo = instance.getUri().toString();
						 
				
				System.out.println("Instance: " + serviceInfo);
				info.add(serviceInfo);
			}
		}
		
		model.addFlashAttribute("service", service);
		model.addFlashAttribute("instances", info);
		return "redirect:/main";
	}
	
	@PostMapping("/invokeServiceInstance")
	public String invokeServiceInstance(@ModelAttribute("service") Service service, RedirectAttributes model) {
		System.out.println("Executing controller. ServiceName : " + service.toString());
		
		List<String> info = new java.util.ArrayList<String>();
		URI instanceUri = getInstanceUri(service);
		if (instanceUri == null) {
			info.add("Instances does not exists");
		} else {
			//If we allow Spring to instantiate a RestTemplate, it will hava a LoadBalancer intercerptor injected in the instance
			//Therefore, for testing purpose,  we need to create our instance to avoid that behavior.
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.getForEntity(instanceUri.toString()+"/"+service.getMethodName(),String.class);
			info.add(response.getBody());
		}
		
		model.addFlashAttribute("service", service);
		model.addFlashAttribute("info", info);
		return "redirect:/main";
	}
	
	@PostMapping("/invokeServiceInstanceUsingClientLoadBalancer")
	public String invokeServiceInstanceUsingLoadBalancer(@ModelAttribute("service") Service service, RedirectAttributes model) {
		System.out.println("Executing controller. ServiceName : " + service.toString());
		List<String> info = new java.util.ArrayList<String>();
	
		
		ResponseEntity<String> response = restTemplate.getForEntity("http://micorservice1/doSomeWork",String.class);
		info.add(response.getBody());
	
		
		model.addFlashAttribute("service", service);
		model.addFlashAttribute("info", info);
		return "redirect:/main";
		
	}
	
	
	private URI getInstanceUri(Service service) {
		List<ServiceInstance> instances = discoveryClient.getInstances(service.getServiceName());
		if (service.getServiceInstanceIndex() >= instances.size()) return null;
		else return instances.get(service.getServiceInstanceIndex()).getUri();
	}
	
}
