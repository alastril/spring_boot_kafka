package com.myboot.web.services;

import com.myboot.Application;
import com.myboot.entity.User;
import com.myboot.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class HibernateService {

    private Logger logger = LogManager.getLogger(HibernateService.class);

    @Autowired
    UserRepository userRepository;

    public List<User> getUsersByDateCreated(Pageable pageable, ZonedDateTime dateCreated) {
        return userRepository.findAllByDateCreation(pageable,dateCreated).getContent();
    }

    public void addUser(User user) {
        logger.info("adding user" + user);
        userRepository.save(user);
    }
}
