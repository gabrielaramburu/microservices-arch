package microservicesarch.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import microservicesarch.service.Caller3Service;

@RestController
public class TestRestController {
	
	private Logger log = LoggerFactory.getLogger(TestRestController.class);
	
	@Autowired
	private Caller3Service caller3service;
	
	@PostMapping("/doSomeWork")
	public ResponseEntity<String> doSomeThing() {
		ResponseEntity<String> response;
		List<String> info ;
		try {
			info = caller3service.callServiceWithResilience4jRetry(null);
			
		} catch (Exception e) {
			info = new ArrayList<String>();
			info.add("error");
			log.error("An error has ocurred trying to invoke service:" + e.getMessage());
		}
		
		if (info.stream().anyMatch(i -> i.equals("error"))) {
			response = new ResponseEntity<String>(info.get(0), HttpStatus.FAILED_DEPENDENCY);
		} else {
			response = new ResponseEntity<String>(info.get(0), HttpStatus.ACCEPTED);	
		}
		
		return response;
	}
}
