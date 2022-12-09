package com.alistats.discorki.notification.common;

import java.util.ArrayList;

import com.alistats.discorki.discord.dto.EmbedDto;
import com.alistats.discorki.riot.dto.spectator.CurrentGameInfoDto;

public interface GameStartNotification {
    public ArrayList<EmbedDto> check(CurrentGameInfoDto currentGame);
}
