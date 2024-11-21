package com.myboot.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myboot.entity.MessageSimple;
import com.myboot.services.KafkaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/kafka")
@Profile({"Publisher"})
public class ControllerKafka {
    private Logger logger = LogManager.getLogger(ControllerKafka.class);
    @Autowired
    private KafkaService kafkaService;

    @PostMapping(path = "/send")
    public ResponseEntity<?> postMessageToKafka(@RequestBody MessageSimple message) throws JsonProcessingException {
        logger.debug("Message object from body {}", message);
        kafkaService.sendObjectToKafka(message);
        return new ResponseEntity<>("Message was added success!", HttpStatus.OK);
    }


    @PostMapping(path = "/sendToBatch")
    public ResponseEntity<?> postMessageToKafkaBatch(@RequestBody List<MessageSimple> messages) throws JsonProcessingException {
        logger.debug("Message object from body {}", messages);
        kafkaService.sendObjectToKafkaBatch(messages);
        return new ResponseEntity<>("Message was added success!", HttpStatus.OK);
    }
}
