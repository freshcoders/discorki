package com.alistats.discorki.util;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class StringUtil {
  private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
  private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

  private final Font DISCORD_FONT = new Font("Whitney", Font.PLAIN, 16);
  private final AffineTransform AFFINE_TRANSFORM = new AffineTransform();
  private final FontRenderContext FONT_READER_CONTEXT = new FontRenderContext(AFFINE_TRANSFORM, true, true);

  public static String getCleanChampionName(String name) {
    String nowhitespace = WHITESPACE.matcher(name).replaceAll("-");
    String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
    String slug = NONLATIN.matcher(normalized).replaceAll("");
    return StringUtils.capitalize(slug.toLowerCase());
  }

  public int getDiscordTextWidthInPixels(String text) {
    return (int)(DISCORD_FONT.getStringBounds(text, FONT_READER_CONTEXT).getWidth());
  }

  public String shortenDiscordTextToPixel(String text, int maxPixel) {
    int textWidth = getDiscordTextWidthInPixels(text);
    if (textWidth <= maxPixel) {
        return text;
    }
    String ellipsis = "…";
    int ellipsisWidth = getDiscordTextWidthInPixels(ellipsis);
    int endIndex = text.length() - 1;
    while (textWidth + ellipsisWidth > maxPixel && endIndex > 0) {
        endIndex--;
        text = text.substring(0, endIndex);
        textWidth = getDiscordTextWidthInPixels(text);
    }
    return text + ellipsis;
  }
}
