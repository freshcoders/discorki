package com.alistats.discorki.dto.discord;
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
}
