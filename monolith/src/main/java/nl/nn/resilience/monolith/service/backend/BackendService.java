package nl.nn.resilience.monolith.service.backend;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BackendService {


    private static final Logger LOG = LoggerFactory.getLogger(BackendService.class);

    @Value("${backend.url}")
    private String url;

    @Value("${backend.url.slow}")
    private String slowUrl;

    public String doBackendCall() {
        final RestTemplate restTemplate = new RestTemplate();
        LOG.info(String.format("Doing request to URI [%s]", url));
        final String response = restTemplate.getForObject(url, String.class);
        LOG.info(String.format("Backend response was [%s]", response));
        return response;
    }


    @HystrixCommand(fallbackMethod = "fallback_hello", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String doSlowBackendCall() throws InterruptedException {
        final RestTemplate restTemplate = new RestTemplate();
        LOG.info(String.format("Doing slow request to URI [%s]", slowUrl));
        final String response = restTemplate.getForObject(slowUrl, String.class);
        LOG.info(String.format("Backend slow response was [%s]", response));
        return response;
    }


    private String fallback_hello() {
        return "Request fails. It takes long time to response";
    }
}
