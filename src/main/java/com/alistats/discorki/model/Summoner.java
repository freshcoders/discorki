package com.alistats.discorki.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.alistats.discorki.model.Match.Status;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "summoners")
public class Summoner {
    @Id
    private String accountId;
    private Integer profileIconId;
    private Long revisionDate;
    private String name;
    private String id;
    private String puuid;
    private Long summonerLevel;
    @Column(columnDefinition = "boolean default false")
    private Boolean tracked;
    @OneToMany(mappedBy = "summoner")
    private Set<Rank> ranks;
    @ManyToMany(mappedBy = "trackedSummoners", fetch=FetchType.EAGER)
    private Set<Match> matches;
    @ManyToMany(mappedBy = "summoners")
    private Set<DiscordUser> users;

    public Match getCurrentMatch() {
        return matches.stream().filter(m -> m.getStatus() == Status.IN_PROGRESS).findFirst().orElse(null);
    }
}