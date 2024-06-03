package tks.gv.rabbit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class RabbitControllerFirst {

    private final UserServicePublisher producer;

    @Autowired
    public RabbitControllerFirst(UserServicePublisher producer) {
        this.producer = producer;
    }

    @GetMapping(value = "/create")
    public String create() {
        producer.sendCreate("Hello RabbitFirst - Creation");
        return "Creation completed";
    }

    @GetMapping(value = "/delete")
    public String delete() {
        producer.sendDelete("Hello RabbitFirst - Deletion");
        return "Deletion completed";
    }

}
