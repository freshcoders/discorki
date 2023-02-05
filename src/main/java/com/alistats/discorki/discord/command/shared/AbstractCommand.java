package com.alistats.discorki.discord.command.shared;

import org.springframework.beans.factory.annotation.Autowired;

import com.alistats.discorki.config.CustomConfigProperties;
import com.alistats.discorki.model.Guild;
import com.alistats.discorki.repository.GuildRepo;
import com.alistats.discorki.repository.MatchRepo;
import com.alistats.discorki.repository.RankRepo;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.repository.UserRepo;
import com.alistats.discorki.riot.controller.ApiController;
import com.alistats.discorki.riot.controller.GameConstantsController;
import com.alistats.discorki.service.ImageService;
import com.alistats.discorki.service.TemplatingService;

public abstract class AbstractCommand {
    @Autowired
    protected ApiController leagueApiController;
    @Autowired
    protected UserRepo userRepo;
    @Autowired
    protected SummonerRepo summonerRepo;
    @Autowired
    protected GuildRepo guildRepo;
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

    protected Guild getGuild(net.dv8tion.jda.api.entities.Guild guild) {
        return guildRepo.findById(guild.getId()).orElseGet(() -> {
            Guild newGuild = new Guild();
            newGuild.setId(guild.getId());
            newGuild.setName(guild.getName());
            newGuild.setDefaultChannelId(guild.getDefaultChannel().getIdLong());

            return guildRepo.save(newGuild);
        });
    }
}
