package com.alistats.discorki.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public abstract class Check {
    Logger logger = LoggerFactory.getLogger(Check.class);
}
