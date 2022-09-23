package microservices.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;
import microservices.aop.CustomerTimer;
import microservices.model.SleepConfiguration;


@Controller
@ResponseBody
public class Service1Controller {
	
	private static final Logger log = LoggerFactory.getLogger(Service1Controller.class);
	
	@Autowired
	private SleepConfiguration conf;
	
	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt; 
	

	@GetMapping("/doSomeWork") 
	@Timed("request.timed.doSomeWork")
	@CustomerTimer
	public ResponseEntity<String> doSomeWork() {
		
		try {
			
			log.info(Thread.currentThread().getName() + ", Sleeping for " + conf.getSleepPeriod());
			Thread.sleep(conf.getSleepPeriod());
			
			int port =  webServerAppCtxt.getWebServer().getPort();
			String response = System.currentTimeMillis() / 1000 + " Hi, I am " 
					+ webServerAppCtxt.getId()+ " running on port " + port;
			log.info(response);
			
			return ResponseEntity.ok(response);
			
		} catch (InterruptedException e) {
			log.error(e.toString());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}
