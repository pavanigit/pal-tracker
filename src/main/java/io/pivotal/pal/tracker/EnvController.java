package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {
    private final String port;
    private final String memoryLimit;
    private final String cfInstanceIndex;
    private final String cfInstanceAddress;

    public EnvController(@Value("${port:NOT SET}") String port,
                         @Value("${memory.limit:NOT SET}") String memoryLimit,
                         @Value("${cf.instance.index:NOT SET}") String cfInstanceIndex,
                         @Value("${cf.instance.addr:NOT SET}") String cfInstanceAddress) {

        this.port = port;
        this.memoryLimit = memoryLimit;
        this.cfInstanceIndex = cfInstanceIndex;
        this.cfInstanceAddress = cfInstanceAddress;
    }
@GetMapping("/env")
    public Map<String, String> getEnv() {
        Map<String, String> environmentValueMap = new HashMap<>();
        environmentValueMap.put("PORT",this.port);
        environmentValueMap.put("MEMORY_LIMIT",this.memoryLimit);
        environmentValueMap.put("CF_INSTANCE_INDEX",this.cfInstanceIndex);
        environmentValueMap.put("CF_INSTANCE_ADDR",this.cfInstanceAddress);
        return environmentValueMap;

    }
}
