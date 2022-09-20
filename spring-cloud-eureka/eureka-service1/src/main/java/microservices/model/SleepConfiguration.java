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
	
	private int previousIncomingRequestCount;
	private int lastIncommingRequestCount;
	private int sleepPeriod;
	
	private static final Logger log = LoggerFactory.getLogger(SleepConfiguration.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt; 

	
	
	public int getPreviousIncomingRequestCount() {
		return previousIncomingRequestCount;
	}

	public void setPreviousIncomingRequestCount(int previousIncomingRequestCount) {
		this.previousIncomingRequestCount = previousIncomingRequestCount;
	}

	public int getLastIncommingRequestCount() {
		return lastIncommingRequestCount;
	}

	public void setLastIncommingRequestCount(int lastIncommingRequestCount) {
		this.lastIncommingRequestCount = lastIncommingRequestCount;
	}

	public int getSleepPeriod() {
		return sleepPeriod;
	}

	public void setSleepPeriod(int sleepPeriod) {
		this.sleepPeriod = sleepPeriod;
	}

	private void calculateSleepPeriod() {
		this.lastIncommingRequestCount = obtainMostRecentRequestCount();
		int diff = this.lastIncommingRequestCount - this.previousIncomingRequestCount;
		
		this.sleepPeriod = calculateSleepPeriod(diff);
		log.info("Config:" + this.toString());
		this.previousIncomingRequestCount = this.lastIncommingRequestCount;
	}

	private int calculateSleepPeriod(int diff) {
		diff = diff / (TASK_EXECUTION_PERIOD / 1000);
		if (diff <= 1) return 10;
		if (diff <= 2) return 200;
		if (diff <= 3) return 250;
		if (diff <= 4) return 300;
		if (diff <= 5) return 350;
		if (diff <= 6) return 400;
		if (diff <= 7) return 450;
		if (diff <= 8) return 500;
		return 1000;
	}

	private int obtainMostRecentRequestCount() {
		try {
			String request = "http://localhost:" + webServerAppCtxt.getWebServer().getPort() + "/actuator/metrics/request.timed.doSomeWork";
			log.debug(request);
			String response = restTemplate.getForObject(request, String.class);
			log.debug(response);
			int value = ((Double)JsonPath.read(response, "$.measurements[0].value")).intValue();
			log.info("Value read from json actuator metric: " + value);
			return value;	
		} catch (Exception e) {
			log.error("Error trying to get the actuator metric.");
			return this.previousIncomingRequestCount;
		}
		
	}

	@Scheduled(fixedDelay = TASK_EXECUTION_PERIOD)
	public void scheduleFixedDelayTask() {
	    log.info("Fixed delay task - " + System.currentTimeMillis() / 1000);
	    calculateSleepPeriod();
	}

	@Override
	public String toString() {
		return "SleepConfiguration [previousIncomingRequestCount=" + previousIncomingRequestCount
				+ ", lastIncommingRequestCount=" + lastIncommingRequestCount + ", sleepPeriod=" + sleepPeriod + "]";
	}

	
	
}
