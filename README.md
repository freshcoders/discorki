### Discorki 

A Discord messaging bot for funky League of Legends player stats.

![](assets/discorki.png)
Image generated using [Dalle2](https://labs.openai.com/)

## Usage

Currently, Discorki is in a very early stage of development. You can set up the application by adding settings in: 
`src\main\resources\application.properties`

You can then run the application using the following command:
`mvn spring-boot:run`

## Features

This bot works by scanning for recent games for tracked players. You can track players by adding them to the `app.usernames` list in `application.properties` file. In the future, this will be done through a Discord command (maybe) and the appication properties will be removed.

As of now, there exist several notifications, which are checked periodically for each tracked player. These are:

- **Top DPS**: Notifies when a player has the most damage dealt.
- **Clash game**: Notifies when a player has played a Clash game.
- **Penta kill**: Notifies when a player has gotten a penta kill.
- **Rank change**: Notifies when a player has changed rank.
- **Lost to bots**: Notifies when a player has lost to bots.

