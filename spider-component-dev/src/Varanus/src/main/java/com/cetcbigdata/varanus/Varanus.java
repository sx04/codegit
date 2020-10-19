package com.cetcbigdata.varanus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;


/**
 * @author matthew
 */


@SpringBootApplication()
@EnableScheduling
public class Varanus {
    static Logger logger = LoggerFactory.getLogger(Varanus.class);

    public static void main(String[] args) {
        logger.info("starting.....");
        SpringApplication.run(Varanus.class, args);
        logger.info("application started!");
    }
}
