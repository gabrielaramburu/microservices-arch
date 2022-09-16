package microservices.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;


@Configuration
@EnableScheduling
public class SleepConfiguration {
	private int lastIncomingRequestCount;
	private int sleepPeriod;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt; 

	public int getLastIncomingRequestCount() {
		return lastIncomingRequestCount;
	}

	public void setLastIncomingRequestCount(int lastIncomingRequestCount) {
		this.lastIncomingRequestCount = lastIncomingRequestCount;
	}
	
	public int getSleepPeriod() {
		return sleepPeriod;
	}

	public void setSleepPeriod(int sleepPeriod) {
		this.sleepPeriod = sleepPeriod;
	}

	private void calculateSleepPeriod() {
		int mostRecentRequestCount = obtainMostRecentRequestCount();
		int diff = mostRecentRequestCount - this.getLastIncomingRequestCount();
		this.setLastIncomingRequestCount(mostRecentRequestCount);
		this.setSleepPeriod(diff * 1000);
		System.out.println("Config:" + this.toString());
	}

	private int obtainMostRecentRequestCount() {
		try {
			String request = "http://localhost:" + webServerAppCtxt.getWebServer().getPort() + "/actuator/metrics/request.timed.doSomeWork";
			System.out.println(request);
			String response = restTemplate.getForObject(request, String.class);
			System.out.println(response);
			int value = ((Double)JsonPath.read(response, "$.measurements[0].value")).intValue();
			System.out.println("Value read from json actuator metric: " + value);
			return value;	
		} catch (Exception e) {
			System.out.println("Error trying to get the actuator metric.");
			return this.lastIncomingRequestCount;
		}
		
	}

	@Scheduled(fixedDelay = 10000)
	public void scheduleFixedDelayTask() {
	    System.out.println(
	      "Fixed delay task - " + System.currentTimeMillis() / 1000);
	    calculateSleepPeriod();
	}

	@Override
	public String toString() {
		return "SleepConfiguration [lastIncomingRequestCount=" + lastIncomingRequestCount + ", sleepPeriod="
				+ sleepPeriod + "]";
	}
	
	
}
