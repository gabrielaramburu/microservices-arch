package microservicesarch.model;

public class Service {
	private String serviceName;
	private String methodName;
	private Integer serviceInstanceIndex;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Integer getServiceInstanceIndex() {
		return serviceInstanceIndex;
	}

	public void setServiceInstanceIndex(Integer serviceInstanceIndex) {
		this.serviceInstanceIndex = serviceInstanceIndex;
	}

	
	
}
