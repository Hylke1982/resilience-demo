package nl.nn.resilience.monolith.rest.example1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingController {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingController.class);

    @PostMapping(path = "/shoppingCart")
    public void postShoppingCart(@RequestBody final String shoppingCart){
        LOG.info(String.format("Shopping cart %s", shoppingCart));
    }


}
