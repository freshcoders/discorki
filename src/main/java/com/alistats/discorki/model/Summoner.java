package com.alistats.discorki.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
    private List<Rank> ranks;
    @ManyToMany(mappedBy = "trackedSummoners", fetch=FetchType.EAGER)
    private List<Match> matches;

    public Match getCurrentMatch() {
        return matches.stream().filter(m -> m.getStatus() == Status.IN_PROGRESS).findFirst().orElse(null);
    }
}