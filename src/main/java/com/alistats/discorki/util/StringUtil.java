package com.alistats.discorki.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class StringUtil {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
  
    public static String getCleanChampionName(String name) {
      String nowhitespace = WHITESPACE.matcher(name).replaceAll("-");
      String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
      String slug = NONLATIN.matcher(normalized).replaceAll("");
      return slug.substring(0,1).toUpperCase() + slug.substring(1).toLowerCase();
    }
}
