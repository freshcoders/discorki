package com.alistats.discorki.dto.riot.match;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class InfoDto {
    private long gameCreation;
    private long gameDuration;
    private long gameEndTimestamp;
    private long gameId;
    private String gameMode;
    private String gameName;
    private long gameStartTimestamp;
    private String gameType;
    private String gameVersion;
    private Integer mapId;
    private ParticipantDto[] participants;
    private String platformId;
    private Integer queueId;
    private TeamDto[] teams;
    private String tournamentCode;

    public boolean isRanked() {
        return queueId == 420 || queueId == 440;
    }

    public String getRankedQueueType(Integer queueId) {
        HashMap <Integer, String> queueTypeMap = new HashMap<Integer, String>();
        queueTypeMap.put(420, "RANKED_SOLO_5x5");
        queueTypeMap.put(440, "RANKED_FLEX_SR");
        
        return queueTypeMap.get(queueId);
    }
}
