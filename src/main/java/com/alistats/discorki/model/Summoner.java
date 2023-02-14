package com.alistats.discorki.model;

import java.util.Set;

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
    private int profileIconId;
    private long revisionDate;
    private String name;
    private String id;
    private String puuid;
    private long summonerLevel;
    @OneToMany(mappedBy = "summoner", fetch = FetchType.EAGER)
    private Set<Rank> ranks;
    @ManyToMany(mappedBy = "trackedSummoners", fetch=FetchType.EAGER)
    private Set<Match> matches;
    @ManyToMany(mappedBy = "summoners", fetch=FetchType.EAGER)
    private Set<Player> players;

    public Match getCurrentMatch() {
        return matches.stream().filter(m -> m.getStatus() == Status.IN_PROGRESS).findFirst().orElse(null);
    }

    public Rank getCurrentSoloQueueRank() {
        return ranks.stream().filter(r -> r.getQueueType().equals("RANKED_SOLO_5x5")).findFirst().orElse(null);
    }

    public Rank getCurrentFlexQueueRank() {
        return ranks.stream().filter(r -> r.getQueueType().equals("RANKED_FLEX_SR")).findFirst().orElse(null);
    }

    public void removeUser(Player player) {
        players.remove(player);
    }

    public void removeUserById(String id) {
        players.removeIf(user -> user.getId().equals(id));
    }
}