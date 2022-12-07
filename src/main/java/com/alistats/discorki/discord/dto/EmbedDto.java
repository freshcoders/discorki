package com.alistats.discorki.discord.dto;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class EmbedDto {
    private Integer color;
    private AuthorDto author;
    private String title;
    private String url;
    private String description;
    private FieldDto[] fields;
    private ThumbnailDto thumbnail;
    private ImageDto image;
    private FooterDto footer;
    private String timestamp;
    @Override
    public String toString() {
        return "EmbedDto [color=" + color + ", author=" + author + ", title=" + title + ", url=" + url
                + ", description=" + description + ", fields=" + Arrays.toString(fields) + ", thumbnail=" + thumbnail
                + ", image=" + image + ", footer=" + footer + ", timestamp=" + timestamp + "]";
    }

    
}
