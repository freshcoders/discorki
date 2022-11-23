package com.alistats.discorki.notification.common;

import java.util.ArrayList;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.riot.spectator.CurrentGameInfoDto;

public interface IGameStartNotification {
    public ArrayList<EmbedDto> check(CurrentGameInfoDto currentGame);
}
