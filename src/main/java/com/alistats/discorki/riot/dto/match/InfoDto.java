package com.alistats.discorki.riot.dto.match;
import java.util.ArrayList;
import java.util.List;

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
    private int mapId;
    private ParticipantDto[] participants;
    private String platformId;
    private int queueId;
    private TeamDto[] teams;
    private String tournamentCode;

    private final int REMAKE_THRESHOLD_SECONDS_SR = 180;
    private final int REMAKE_THRESHOLD_SECONDS_ARAM = 90;
    private final int SUMMONERS_RIFT_MAP_ID = 1;

    public boolean isAborted() {
        if (this.getMapId() == SUMMONERS_RIFT_MAP_ID)
            return this.getGameDuration() < REMAKE_THRESHOLD_SECONDS_SR;
        else 
            return this.getGameDuration() < REMAKE_THRESHOLD_SECONDS_ARAM;
    }

    public boolean isRanked() {
        return queueId == 420 || queueId == 440;
    }

    public List<List<ParticipantDto>> getTeamCategorizedParticipants() {
        List<List<ParticipantDto>> teams = new ArrayList<List<ParticipantDto>>();
        teams.add(new ArrayList<ParticipantDto>());
        teams.add(new ArrayList<ParticipantDto>());

        for (ParticipantDto participant : participants) {
            if (participant.getTeamId() == 100) {
                teams.get(0).add(participant);
            } else {
                teams.get(1).add(participant);
            }
        }

        return teams;
    }
}
