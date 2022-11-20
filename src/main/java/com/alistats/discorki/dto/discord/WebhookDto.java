package com.alistats.discorki.dto.discord;
import java.util.Arrays;

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
    
    @Override
    public String toString() {
        return "WebhookDto [username=" + username + ", avatar_url=" + avatar_url + ", content=" + content + ", embeds="
                + Arrays.toString(embeds) + ", tts=" + tts + ", allowed_mentions=" + allowed_mentions + "]";
    }
    
}
