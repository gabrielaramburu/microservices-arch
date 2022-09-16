package microservices.controller;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.micrometer.core.annotation.Timed;
import microservices.model.SleepConfiguration;

@Controller
@ResponseBody
public class Service1Controller {
	@Autowired
	private SleepConfiguration conf;
	
	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt; 

	@GetMapping("/doSomeWork") 
	@Timed("request.timed.doSomeWork")
	public ResponseEntity<String> doSomeWork() {
		String response = System.currentTimeMillis() / 1000 + " Hi, I am " + webServerAppCtxt.getId()+ " running on port " + webServerAppCtxt.getWebServer().getPort();
		System.out.println(response);
		try {
			Thread.currentThread().sleep(conf.getSleepPeriod());
			System.err.println("Working for " + conf.getSleepPeriod());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(response);
	}
}
