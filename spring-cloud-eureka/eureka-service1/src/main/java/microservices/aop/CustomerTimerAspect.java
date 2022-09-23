package microservices.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;
import microservices.controller.Service1Controller;


@Component
@Aspect
public class CustomerTimerAspect {

	private static final Logger log = LoggerFactory.getLogger(Service1Controller.class);
	
	@Autowired 
	MeterRegistry meterRegistry;
	
	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt; 
	
	@Around("@annotation(microservices.aop.CustomerTimer)")
	public Object measureExecutionTimeInController(ProceedingJoinPoint proceedingJointPoint) {
		
		
		Object value = null;
		try {
			final Sample sample = Timer.start(meterRegistry);
			
			value = proceedingJointPoint.proceed();
			int port =  webServerAppCtxt.getWebServer().getPort();
			if (port > 0) {
			    sample.stop(Timer.builder("request.controller")
	                     .tag("port", String.valueOf(port))
	                     .register(meterRegistry));
			}
			
		} catch (Throwable e) {
			log.error(e.getMessage());
		}
		
		return value;
	}

}
