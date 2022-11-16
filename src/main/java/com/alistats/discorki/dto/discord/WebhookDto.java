package com.alistats.discorki.dto.discord;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class WebhookDto {
    private String username;
    private String avatar_url;
    private String content;
    private EmbedDto[] embeds;
    private boolean tts;
    private AllowedMentionsDto allowed_mentions;
}
