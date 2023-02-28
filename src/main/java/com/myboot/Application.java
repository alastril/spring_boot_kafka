package com.myboot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    static Logger logger = LogManager.getLogger(Application.class);
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
        logger.info("com.myboot.Application has been started!");
    }
}