package gateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gateway.filters.pre.SimpleFilter;
import gateway.provider.FallbackProvider;

@EnableZuulProxy
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableFeignClients
@SpringBootApplication
@RestController
public class GatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@FeignClient("service-demo")
	public interface ServiceClient {
		@RequestMapping("/service-instances")
		List<String> serviceInstances();

		@RequestMapping("/service-instances/{applicationName}")
		HashMap<String, Object> serviceInstancesByApplicationName(@PathVariable("applicationName") String applicationName);

		@RequestMapping("/version")
		String getVersion(@RequestHeader(value="Api-Version") String apiVersion);
	}

	@Autowired
	private ServiceClient serviceClient;

	@RequestMapping("/subrequest")
	public HashMap<String, Object> subrequest(@RequestHeader Map<String, String> headers) {
		HashMap<String, Object> response = new HashMap<>();
		response.put("api-version", this.serviceClient.getVersion(headers.get("api-version")));

		for(String serviceName : this.serviceClient.serviceInstances()) {
			response.put(serviceName, this.serviceClient.serviceInstancesByApplicationName(serviceName));
		}

		return response;
	}

	@Bean
	public SimpleFilter simpleFilter() {
		return new SimpleFilter();
	}

	@Bean
	public ZuulFallbackProvider zuulFallbackProvider() {
		return new FallbackProvider();
	}
}
