package com.myboot.web.controllers;

import com.myboot.entity.User;
import com.myboot.request.RequestDate;
import com.myboot.services.HibernateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hyber")
@Profile({"Hibernate"})
public class ControllerHibernate {

    private static final Logger LOGGER = LogManager.getLogger(ControllerHibernate.class);
    @Autowired
    private HibernateService hibernateService;

    @GetMapping(path = "/users")
    @ResponseBody
    public ResponseEntity<List<User>> getUsersByDateCreated(@RequestBody RequestDate requestDate) {
        LOGGER.debug("Message date_created object from body {}", requestDate);
        return new ResponseEntity<>(hibernateService.getUsersByDateCreated(
                PageRequest.of(requestDate.getPage(), requestDate.getCountItemsPerPage(), Sort.by(Sort.Direction.fromString(requestDate.getDirection().name()),requestDate.getFieldsSorted().toArray(new String[0]))),
                requestDate.getDate()), HttpStatus.OK);
    }

    @GetMapping(path = "/users/before")
    @ResponseBody
    public ResponseEntity<List<User>> getUserByDateFilterBefore(@RequestBody RequestDate requestDate) {
        LOGGER.debug("Message before object from body {}", requestDate);
        return new ResponseEntity<>(hibernateService.getUsersSliceByDateCreatedBefore(
                PageRequest.of(requestDate.getPage(), requestDate.getCountItemsPerPage(), Sort.by(Sort.Direction.fromString(requestDate.getDirection().name()),requestDate.getFieldsSorted().toArray(new String[0]))),
                requestDate.getDate()), HttpStatus.OK);
    }

    @GetMapping(path = "/users/between")
    @ResponseBody
    public ResponseEntity<List<User>> getUserByDateFilterBetween(@RequestBody RequestDate requestDate) {
        LOGGER.debug("Message object from body Between {}", requestDate);
        return new ResponseEntity<>(hibernateService.getUsersSliceByDateCreatedBetween(
                PageRequest.of(requestDate.getPage(), requestDate.getCountItemsPerPage(), Sort.by(Sort.Direction.fromString(requestDate.getDirection().name()),requestDate.getFieldsSorted().toArray(new String[0]))),
                requestDate.getDateFrom(), requestDate.getDateTo()), HttpStatus.OK);
    }

    @PostMapping(path = "/users")
    public ResponseEntity<User> postMessageToDB(@RequestBody User user) {
        LOGGER.debug("Message to DB object from body {}", user);
        User userRes = hibernateService.addUser(user);
        return new ResponseEntity<>(userRes, HttpStatus.OK);
    }
}
