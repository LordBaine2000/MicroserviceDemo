package service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import annotation.apiversion.ApiVersion;
import config.WebConfig;

@SpringBootApplication
@Import(WebConfig.class)
@EnableDiscoveryClient
@RestController
public class ServiceApplication {
	private static Logger log = LoggerFactory.getLogger(ServiceApplication.class);

	private final DiscoveryClient discoveryClient;

	@Autowired
	public ServiceApplication(DiscoveryClient discoveryClient) {
		this.discoveryClient = discoveryClient;
	}

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

	@RequestMapping("/service-instances")
	public List<String> serviceInstances(HttpServletRequest request) {
		this.log(request);

		return this.discoveryClient.getServices();
	}

	@RequestMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName, HttpServletRequest request) {
		this.log(request);

		return this.discoveryClient.getInstances(applicationName);
	}

	@RequestMapping(value = "/version")
	@ApiVersion(to = "2.0.0")
	public String v1() {
		return "v1";
	}

	@RequestMapping(value = "/version")
	@ApiVersion(from = "2.0.0", to = "3.0.0")
	public String v2() {
		return "v2";
	}

	private void log(HttpServletRequest request) {
		log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
	}
}
