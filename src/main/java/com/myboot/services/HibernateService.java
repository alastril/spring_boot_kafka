package com.myboot.services;

import com.myboot.entity.User;
import com.myboot.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class HibernateService {

    private Logger logger = LogManager.getLogger(HibernateService.class);

    @Autowired
    UserRepository userRepository;

    public User findById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getUsersByDateCreated(Pageable pageable, ZonedDateTime dateCreated) {
        return userRepository.findAllByDateCreation(pageable, dateCreated).getContent();
    }

    public List<User> getUsersSliceByDateCreatedBefore(Pageable pageable, ZonedDateTime dateCreated) {
        return userRepository.findAllSliceByDateCreationBefore(pageable, dateCreated).getContent();
    }

    public List<User> getUsersSliceByDateCreatedBetween(Pageable pageable, ZonedDateTime dateCreationStart, ZonedDateTime dateCreationEnd) {
        try {
            return userRepository.findByDateCreationBetween(pageable, dateCreationStart, dateCreationEnd);
        } catch (Exception e) {
            logger.error("error get user {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void addUser(User user) {
        try {
            logger.info("adding user {}", user);
            userRepository.updateUser(user);
            Thread.sleep(100);
        } catch (Exception e) {
            logger.error("error save user {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
