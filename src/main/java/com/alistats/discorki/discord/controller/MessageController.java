package com.alistats.discorki.discord.controller;

import java.util.Set;

import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Service
public class MessageController {
    public void sendMessage(TextChannel channel, Set<MessageEmbed> embeds) {
        channel.sendMessageEmbeds(embeds).queue();
    }
}
