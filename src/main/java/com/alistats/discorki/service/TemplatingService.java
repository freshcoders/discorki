package com.alistats.discorki.service;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

@Component
public class TemplatingService {
    PebbleEngine engine = new PebbleEngine.Builder().build();

    public String renderTemplate(String templatePath, HashMap<String, Object> context) {
        PebbleTemplate compiledTemplate = engine.getTemplate(templatePath);

        Writer writer = new StringWriter();

        try {
            compiledTemplate.evaluate(writer, context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return writer.toString();
    }
}
