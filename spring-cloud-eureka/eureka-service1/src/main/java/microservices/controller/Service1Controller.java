package microservices.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class Service1Controller {

	@GetMapping("/doSomeWork")
	public ResponseEntity<String> doSomeWork() {
		String response = "i have to get the port number";
		System.out.println(response);
		return ResponseEntity.ok(response);
	}
}
