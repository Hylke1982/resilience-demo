package nl.nn.resilience.monolith.rest.example1;

import nl.nn.resilience.monolith.service.backend.BackendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedbackController {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackController.class);


    private final BackendService backendService;

    public FeedbackController(@Autowired final BackendService backendService){
        this.backendService = backendService;
    }

    @PostMapping(path = "/feedback")
    public String postFeedback(@RequestBody String feedback) {
        final String response = backendService.doBackendCall();
        LOG.info(String.format("Feedback %s", feedback));
        return response;
    }

    @PostMapping(path = "/feedback-slow")
    public String postSlowFeedback(@RequestBody String feedback) throws InterruptedException {
        final String response = backendService.doSlowBackendCall();
        LOG.info(String.format("Feedback %s", feedback));
        return response;
    }

}
