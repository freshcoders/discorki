package com.alistats.discorki.model;

import java.util.Optional;
import java.util.Set;

import com.alistats.discorki.model.Match.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    private long level;
    @OneToMany(mappedBy = "summoner")
    private Set<Rank> ranks;
    @ManyToMany(mappedBy = "trackedSummoners")
    private Set<Match> matches;
    @ManyToMany(mappedBy = "summoners")
    private Set<Player> players;

    public Optional<Match> getMatchInProgress() {
        return matches.stream().filter(m -> m.getStatus() == Status.IN_PROGRESS).findFirst();
    }

    public Rank getCurrentRank(QueueType queueType) {
        return ranks.stream().filter(r -> r.getQueueType().equals(queueType)).findFirst().orElse(null);
    }

    public void removeLinkedPlayer(Player player) {
        players.remove(player);
    }

    public void removeLinkedPlayerById(Long playerId) {
        players.removeIf(player -> player.getId() == playerId);
    }
    
}