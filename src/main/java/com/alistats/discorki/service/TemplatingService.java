package com.alistats.discorki.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

@Component
public class TemplatingService {
    PebbleEngine engine = new PebbleEngine.Builder().build();

    public String renderTemplate(String templatePath, HashMap<String, Object> context) throws IOException {
        PebbleTemplate compiledTemplate = engine.getTemplate(templatePath);

        Writer writer = new StringWriter();

        compiledTemplate.evaluate(writer, context);

        return writer.toString();
    }
}
