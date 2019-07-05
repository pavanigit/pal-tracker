package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

@RestController
public class ProjectClientController {
    @Autowired
    private ProjectClient client;


    @GetMapping(path="/proxy/{userId}")
    ResponseEntity<UserInfo> getUserInfoFromProxy(@PathVariable Long userId){
       return client.getRestOp().getForEntity(client.getUserServiceUrl()+"/users/"+userId,UserInfo.class);

    }
}
