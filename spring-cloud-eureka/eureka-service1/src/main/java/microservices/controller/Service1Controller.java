package microservices.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class Service1Controller {
	
	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt; 

	@GetMapping("/doSomeWork") 
	public ResponseEntity<String> doSomeWork() {
		String response = "Hi, I am " + webServerAppCtxt.getId()+ " running on port " + webServerAppCtxt.getWebServer().getPort();
		System.out.println(response);
		return ResponseEntity.ok(response);
	}
}
