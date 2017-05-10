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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
@RestController
public class ServiceApplication {
	private static Logger log = LoggerFactory.getLogger(ServiceApplication.class);

	@Autowired
	private DiscoveryClient discoveryClient;

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

	private void log(HttpServletRequest request) {
		log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
	}
}
