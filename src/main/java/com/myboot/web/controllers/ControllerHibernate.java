package com.myboot.web.controllers;

import com.myboot.Application;
import com.myboot.entity.User;
import com.myboot.web.services.HibernateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZonedDateTime;
import java.util.List;

@Controller
@RequestMapping("/hyber")
@Profile({"Hibernate"})
public class ControllerHibernate {

    private Logger logger = LogManager.getLogger(Application.class);
    @Autowired
    private HibernateService hibernateService;

    @GetMapping(path = "/users/{date}")
    @ResponseBody
    public ResponseEntity<List<User>> getMessageToKafka(@PathVariable ZonedDateTime date) {
        logger.debug("Message object from body {}", date);
        return new ResponseEntity<>(hibernateService.getUsersByDateCreated(Pageable.ofSize(5), date), HttpStatus.OK);
    }

    @PostMapping(path = "/users")
    public ResponseEntity<?> postMessageToKafka(@RequestBody User user) {
        logger.debug("Message object from body {}", user);
        hibernateService.addUser(user);
        return new ResponseEntity<>("Added", HttpStatus.OK);
    }
}
