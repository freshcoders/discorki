package com.alistats.discorki.discord.command.shared;

import org.springframework.beans.factory.annotation.Autowired;

import com.alistats.discorki.config.CustomConfigProperties;
import com.alistats.discorki.model.Server;
import com.alistats.discorki.repository.MatchRepo;
import com.alistats.discorki.repository.PlayerRepo;
import com.alistats.discorki.repository.RankRepo;
import com.alistats.discorki.repository.ServerRepo;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.riot.controller.ApiController;
import com.alistats.discorki.riot.controller.GameConstantsController;
import com.alistats.discorki.service.ImageService;
import com.alistats.discorki.service.TemplatingService;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class AbstractCommand {
    @Autowired
    protected ApiController leagueApiController;
    @Autowired
    protected PlayerRepo playerRepo;
    @Autowired
    protected SummonerRepo summonerRepo;
    @Autowired
    protected ServerRepo serverRepo;
    @Autowired
    protected RankRepo rankRepo;
    @Autowired
    protected MatchRepo matchRepo;
    @Autowired
    protected GameConstantsController gameConstantsController;
    @Autowired
    protected CustomConfigProperties config;
    @Autowired
    protected TemplatingService templatingService;
    @Autowired
    protected ImageService imageService;

    @SuppressWarnings("null")
    protected Server obtainServer(net.dv8tion.jda.api.entities.Guild guild) {
        return serverRepo.findById(guild.getId()).orElseGet(() -> {
            Server newServer = new Server();
            newServer.setId(guild.getId());
            newServer.setName(guild.getName());
            newServer.setDefaultChannelId(guild.getDefaultChannel().getIdLong());

            return serverRepo.save(newServer);
        });
    }

    /**
     * Reply to the user with a message. This function exists to prevent the null
     * type safety warnings
     *
     */
    protected void reply(SlashCommandInteractionEvent event, String message) {
        if (message == null) {
            return;
        }
        event.getHook().sendMessage(message).queue();
    }

    protected void privateReply(User recipient, String message) {
        if (recipient == null || message == null) {
            return;
        }

        recipient.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }
}