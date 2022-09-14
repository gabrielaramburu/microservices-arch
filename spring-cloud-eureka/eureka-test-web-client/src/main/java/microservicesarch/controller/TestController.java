package microservicesarch.controller;



import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import microservicesarch.model.ServiceInfo;
import microservicesarch.service.Caller1Service;
import microservicesarch.service.Caller2Service;
import microservicesarch.service.Caller3Service;



@Controller
public class TestController {
	
	@Autowired 
	private Caller1Service caller1Service;
	
	@Autowired 
	private Caller2Service caller2Service;
	
	@Autowired 
	private Caller3Service caller3Service;
	
	
	
	
	@GetMapping("/main")
	public String mainForm(Model model) {
		System.out.println("Main form");
		
		if (model.getAttribute("service") == null) {
			microservicesarch.model.ServiceInfo service = new microservicesarch.model.ServiceInfo();
			service.setServiceName("microservice1");
			service.setServiceInstanceIndex(0);
			service.setMethodName("doSomeWork");
			model.addAttribute("service", service);
		}
		
		return "main";
	}
	
	
	@GetMapping("/servicesInstancesByName")
	public String obtainServiceInstances(@ModelAttribute("service") ServiceInfo service, RedirectAttributes model) {
		
		List<String> info = caller1Service.obtainServiceInstances(service);
		
		model.addFlashAttribute("service", service);
		model.addFlashAttribute("instances", info);
		return "redirect:/main";
	}
	
	@PostMapping("/invokeServiceInstance")
	public String invokeServiceInstance(@ModelAttribute("service") ServiceInfo service, RedirectAttributes model) {
		
		List<String> info = caller1Service.invokeServiceInstance(service);
		
		model.addFlashAttribute("service", service);
		model.addFlashAttribute("info", info);
		return "redirect:/main";
	}
	
	

	@PostMapping("/invokeServiceInstanceUsingClientLoadBalancer")
	public String invokeServiceInstanceUsingLoadBalancer(@ModelAttribute("service") ServiceInfo service, RedirectAttributes model) {
		
		List<String> info = caller2Service.callServiceWithManualRetry(service);
		
		model.addFlashAttribute("service", service);
		model.addFlashAttribute("info", info);
		return "redirect:/main";
		
	}

	
	@PostMapping("invokeInstanceUsingLoadBalancerAndRetry")
	public String invokeInstanceUsingLoadBalancerAndRetry(@ModelAttribute("service") ServiceInfo service, RedirectAttributes model) {
		
		List<String> info = caller3Service.callServiceWithResilience4jRetry(service);
		
		model.addFlashAttribute("info", info);
		return "redirect:/main";
	}
	

	
}
