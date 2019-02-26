package nl.nn.resilience.feedback.feedback.rest;

import nl.nn.resilience.feedback.feedback.service.BackendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

@CrossOrigin("*")
@RestController
public class FeedbackController {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackController.class);


    private final BackendService backendService;

    public FeedbackController(@Autowired final BackendService backendService) {
        this.backendService = backendService;
    }

    @PostMapping(path = "/feedback")
    public String postFeedback(@RequestBody String feedback) {
        final String response = backendService.doBackendCall();
        LOG.info(String.format("Feedback %s", feedback));
        return response;
    }

    @PostMapping(path = "/feedback-retry")
    public String postRetryFeedback(@RequestBody String feedback) throws InterruptedException {
        final String response = backendService.doRetryBackendCall();
        LOG.info(String.format("Feedback %s", feedback));
        return response;
    }

    @PostMapping(path = "/feedback-slow")
    public String postSlowFeedback(@RequestBody String feedback) throws InterruptedException {
        final String response = backendService.doSlowBackendCall();
        LOG.info(String.format("Feedback %s", feedback));
        return response;
    }

    @PostMapping(path = "/feedback-reactive")
    public Mono<String> postReactiveFeedback(@RequestBody String feedback) throws InterruptedException {
        final Mono<String> response = backendService.doReactiveBackendCall();
        LOG.info(String.format("Feedback %s", feedback));
        return response;
    }

    @PostMapping(path = "/feedback-circuit-breaker")
    public String postCircuitBreakerFeedback(@RequestBody String feedback) throws InterruptedException {
        final String response = backendService.doCircuitBreakerBackendCall();
        LOG.info(String.format("Feedback %s", feedback));
        return response;
    }

    @PostMapping(path = "/feedback-async")
    public DeferredResult<ResponseEntity<?>> postAsyncBreakerFeedback(@RequestBody String feedback) throws InterruptedException {
        LOG.info(String.format("Feedback %s", feedback));
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(500L);
        output.onTimeout(() -> output
                .setResult(ResponseEntity.status(HttpStatus.ACCEPTED).header("Link", "/path/to/more-info")
                        .build()));

        ForkJoinPool.commonPool().submit(() -> {
            LOG.info("Processing in separate thread");
            output.setResult(ResponseEntity.ok(backendService.doRetryBackendCall()));
        });
        return output;
    }

    @PostMapping(path = "/feedback-server-timeout")
    public Callable<String> postTimeoutFeedback(@RequestBody String feedback) {
        return () -> {
            Thread.sleep(3000);
            return "timeout";
        };
    }

}
