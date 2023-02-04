package com.alistats.discorki.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alistats.discorki.config.CustomConfigProperties;
import com.alistats.discorki.model.Tier;
import com.alistats.discorki.riot.controller.GameConstantsController;
import com.alistats.discorki.util.StringUtil;

@Service
public class ImageService {
    @Autowired
    private CustomConfigProperties customConfig;
    @Autowired
    private GameConstantsController gameConstantsController;

    private static final String DATA_DRAGON_URL = "https://ddragon.leagueoflegends.com/cdn";
    private static final String DATA_DRAGON_VERSION = "12.22.1";

    // maybe the riot cdn can provide an image url or a slug for the champ
    public URL getChampionTileUrl(int championId) {
        String championName = gameConstantsController.getChampionIdByKey(championId);

        StringBuilder str = new StringBuilder();
        str.append(DATA_DRAGON_URL)
                .append("/")
                .append(DATA_DRAGON_VERSION)
                .append("/img/champion/")
                .append(championName)
                .append(".png");
        return resolveUrl(str.toString());
    }

    public URL getChampionSplashUrl(String championName) {
        championName = StringUtil.getCleanChampionName(championName);
        StringBuilder str = new StringBuilder();
        str.append(DATA_DRAGON_URL)
                .append("/img/champion/splash/")
                .append(championName)
                .append("_0.jpg");
        return resolveUrl(str.toString());
    }

    public URL getMapUrl(int mapId) {
        StringBuilder str = new StringBuilder();
        str.append(DATA_DRAGON_URL)
                .append("/")
                .append(DATA_DRAGON_VERSION)
                .append("/img/map/map")
                .append(mapId)
                .append(".png");

        return resolveUrl(str.toString());
    }

    public URL getRankEmblemUrl(Tier tier) {
        // tier should be a PascalCase string
        String tierStr = tier.toString().toLowerCase();

        StringBuilder str = new StringBuilder();
        str.append("http://")
                .append(customConfig.getHost())
                .append("/rank_emblems/")
                .append(tierStr)
                .append(".png");

        return resolveUrl(str.toString());
    }

    private URL resolveUrl(String url) {
        try {
            URL resolvedUrl = new URL(url);
            return resolvedUrl;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public URL mergeImagesDiagonally(URL url1, URL url2) throws IOException {
        final String TEXT = "VS";
        final int FONT_OUTLINE_SIZE = 64;
        final int FONT_SIZE = 56;
        final Color FONT_COLOR = Color.GRAY;
        final Color FONT_OUTLINE_COLOR = Color.BLACK;
        final String FONT_NAME = "Arial";

        // Load images
        BufferedImage image1 = ImageIO.read(url1.openStream());
        BufferedImage image2 = ImageIO.read(url2.openStream());

        // Create a new image that's the same size as the first
        int width = image1.getWidth();
        int height = image1.getHeight();
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Get the Graphics2D object for the combined image
        Graphics2D g = combined.createGraphics();

        // Draw the left half of the first image on the left half of the combined image
        g.drawImage(image1, 0, 0, width / 2, height, 0, 0, width / 2, height, null);

        // Draw the right half of the second image on the right half of the combined
        // image
        g.drawImage(image2, width / 2, 0, width, height, width / 2, 0, image2.getWidth(), image2.getHeight(), null);

        // Set the font and color for the text
        g.setFont(new Font(FONT_NAME, Font.BOLD, FONT_OUTLINE_SIZE));
        g.setColor(FONT_OUTLINE_COLOR);

        // Calculate the width and height of the text
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = g.getFont().getStringBounds(TEXT, frc);
        int textWidth = (int) bounds.getWidth();
        int textHeight = (int) bounds.getHeight();

        // Draw the text in the center of the image
        int x = (width - textWidth) / 2;
        int y = (height - textHeight) / 2 + textHeight - 12;
        g.drawString(TEXT, x, y);

        // Draw the text again with a transparent fill color
        g.setFont(new Font(FONT_NAME, Font.BOLD, FONT_SIZE));
        g.setColor(FONT_COLOR);
        g.drawString(TEXT, x+4, y);

        // Generate uuid
        UUID uuid = UUID.randomUUID();

        // Save to web exposed file
        String filePath = String.format("%s/static/generated/%s.png",
                getClass().getClassLoader().getResource("").getPath(),
                uuid.toString());
        File outputFile = new File(filePath);
        ImageIO.write(combined, "PNG", outputFile);

        // Return url
        StringBuilder str = new StringBuilder();
        str.append("http://")
                .append(customConfig.getHost())
                .append("/generated/")
                .append(uuid.toString())
                .append(".png");

        return new URL(str.toString());
    }
}
