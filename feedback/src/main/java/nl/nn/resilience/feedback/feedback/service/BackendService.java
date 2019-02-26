package nl.nn.resilience.feedback.feedback.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 5000))
    public String doRetryBackendCall() {
        final RestTemplate restTemplate = new RestTemplate();
        LOG.info(String.format("Doing retry request to URI [%s]", url));
        final String response = restTemplate.getForObject(url, String.class);
        LOG.info(String.format("Backend retry response was [%s]", response));
        return response;
    }





    @HystrixCommand(commandKey = "timeout-call", fallbackMethod = "fallback_hello", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String doSlowBackendCall() throws InterruptedException {
        final RestTemplate restTemplate = new RestTemplate();
        LOG.info(String.format("Doing slow request to URI [%s]", slowUrl));
        final String response = restTemplate.getForObject(slowUrl, String.class);
        LOG.info(String.format("Backend slow response was [%s]", response));
        return response;
    }

    @HystrixCommand(commandKey = "circuit-breaker-call", fallbackMethod = "fallback_hello_circuit", commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "600000"),
    })
    public String doCircuitBreakerBackendCall() throws InterruptedException {
        final RestTemplate restTemplate = new RestTemplate();
        LOG.info(String.format("Doing circuit breaker request to URI [%s]", url));
        final String response = restTemplate.getForObject(url, String.class);
        LOG.info(String.format("Backend circuit breaker response was [%s]", response));
        return response;
    }


    public Mono<String> doReactiveBackendCall(){
        final WebClient client = WebClient.create(url);
        return client
                .get()
                .retrieve()
                .bodyToMono(String.class);
    }




    private String fallback_hello() {
        return "Request fails. It takes long time to response";
    }

    private String fallback_hello_circuit() {
        return "My circuit is broken";
    }
}
