package microservices.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import microservices.controller.Service1Controller;


@Configuration
@EnableScheduling

public class SleepConfiguration {
	private static final int TASK_EXECUTION_PERIOD = 5000;
	
	private int incomingRequestCount;
	private int sleepPeriod;
	
	private static final Logger log = LoggerFactory.getLogger(SleepConfiguration.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt; 


	public int getSleepPeriod() {
		return sleepPeriod;
	}

	public void setSleepPeriod(int sleepPeriod) {
		this.sleepPeriod = sleepPeriod;
	}

	private void calculateSleepPeriod() {
		
		obtainMostRecentRequestCount();
		this.sleepPeriod = calculaterPeriod();
		log.info("Config:" + this.toString());
	}

	private int calculaterPeriod() {
		int requestPerSecond = this.incomingRequestCount;
		if (requestPerSecond <= 1) return 10;
		if (requestPerSecond <= 2) return 200;
		if (requestPerSecond <= 3) return 300;
		if (requestPerSecond <= 4) return 400;
		if (requestPerSecond <= 5) return 500;
		if (requestPerSecond <= 6) return 600;
		if (requestPerSecond <= 7) return 700;
		if (requestPerSecond <= 8) return 800;
		return 1000;
	}

	private void obtainMostRecentRequestCount() {
		try {
			String request = "http://localhost:" + webServerAppCtxt.getWebServer().getPort() + "/actuator/metrics/request.timed.doSomeWork";
			log.debug(request);
			String response = restTemplate.getForObject(request, String.class);
			log.debug(response);
			int value = ((Double)JsonPath.read(response, "$.measurements[0].value")).intValue();
			log.info("Value read from json actuator metric: " + value);
			
			this.incomingRequestCount = value;	
		} catch (Exception e) {
			log.warn("Error trying to get the actuator metric.");
			this.incomingRequestCount = 0;
		}
		
	}

	@Scheduled(fixedDelay = TASK_EXECUTION_PERIOD)
	public void scheduleFixedDelayTask() {
	    log.info("Fixed delay task - " + System.currentTimeMillis() / 1000);
	    calculateSleepPeriod();
	}

	@Override
	public String toString() {
		return "SleepConfiguration [incomingRequestCount=" + incomingRequestCount + ", sleepPeriod=" + sleepPeriod
				+ "]";
	}
	
	
}
