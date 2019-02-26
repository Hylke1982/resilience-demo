package nl.nn.resilience.backend.backend.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackendController {

    private static final Logger LOG = LoggerFactory.getLogger(BackendController.class);

    @GetMapping(path = "/backend")
    public String doBackendCall(){
        LOG.info("Did a backend call");
        return "hello";
    }

    @GetMapping(path = "/backend-slow")
    public String doSlowBackendCall() throws InterruptedException {
        Thread.sleep(2000L);
        LOG.info("Did a slow backend call");
        return "hello";
    }
}
