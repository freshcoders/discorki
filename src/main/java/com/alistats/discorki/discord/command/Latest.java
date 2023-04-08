package com.alistats.discorki.discord.command;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alistats.discorki.discord.EmbedFactory;
import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Server;
import com.alistats.discorki.riot.controller.LeagueApiHelper;
import com.alistats.discorki.riot.dto.MatchDto;
import com.alistats.discorki.riot.dto.MatchDto.InfoDto.ParticipantDto;
import com.alistats.discorki.riot.dto.SummonerDto;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class Latest extends AbstractCommand implements Command {
    @Autowired
    private EmbedFactory embedFactory;

    @Autowired
    private LeagueApiHelper apiHelper;

    @Override
    public String getCommandName() {
        return "latest";
    }

    @Transactional(readOnly = true)
    public void run(SlashCommandInteractionEvent event) throws Exception {
        String summonerName = Optional.ofNullable(event.getOption("summoner"))
                .orElseThrow(() -> new RuntimeException("Summoner name cannot be empty.")).getAsString();

        try {
            LOG.debug("Looking for summoner named {}", summonerName);
            SummonerDto summonerDto = leagueApiController.getSummoner(summonerName);
            LOG.debug("Getting most recent match for summoner {} ({}).", summonerName, summonerDto.getPuuid());
            MatchDto match = apiHelper.getMostRecentMatch(summonerDto.getPuuid());
            Server server = obtainServer(event.getGuild());
            reply(event, "Fetching player ranks... 💤");
            LOG.debug("Fetching player ranks for match {}", match.getInfo().getGameId());
            HashMap<ParticipantDto, Rank> participantRanks = apiHelper.getParticipantRanks(match.getInfo().getParticipants());

            LOG.debug("Creating embed for match {}", match.getInfo().getGameId());
            MessageEmbed embed = embedFactory.getMatchEmbed(server, match, participantRanks);
            if (embed == null) {
                throw new Exception("Could not create embed for match.");
            }

            LOG.debug("Sending embed for match {}", match.getInfo().getGameId());
            replyWithEmbeds(event, embed);
        } catch (Exception e) {
            if (e.getMessage().contains("404")) {
                reply(event, String.format("Summoner ***%s*** not found.", summonerName));
            } else {
                throw new Exception(e);
            }
        }
    }

}
