# Discorki

Discorki is a Discord bot that tracks in game achievements for you and your friends. Did someone just promote? Did someone just hard int? Discorki will let you know.

In addition, the bot offers additional features such as generating teams and champions with the ARAM command, and the ability to subscribe to a friend's game to know when they're available.

The bot is currently in closed beta. Open beta is expected to start early april!

As soon as the bot is live, you can add it via [discorki.nl](discorki.nl).

## Prerequisites

+ [Java JDK 17](https://adoptium.net/temurin/releases/?version=17)

## Install

You can set up the application by setting up the application.properties at `src\main\resources\application.properties`

Example:

```Bash
# database settings
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mariadb://127.0.0.1:3306/discorki
spring.datasource.username=username
spring.datasource.password=password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# spring misc settings
spring.jpa.open-in-view=false
spring.output.ansi.enabled=always

# riot settings
riot.key=RGAPI-XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
riot.platformRouting=euw1
riot.regionalRouting=europe

# discord settings
discord.token=aBcDeFgHiJkLmNoPqRsTuVwXyZ

# log settings
logging.level.com.alistats.discorki.tasks.Task=INFO

# custom settings
app.summonerLookupUrl = https://euw.op.gg/summoner/userName=%s
app.matchLookupUrl = https://www.leagueofgraphs.com/match/euw/%d
app.host = discorki.nl
app.developerDiscordIds[0] = yourDiscordId
```

You can then run the application using the following command:

```Bash
`mvn spring-boot:run`
```

## Disclaimer

*Discorki isn't endorsed by Riot Games and doesn't reflect the views or opinions of Riot Games or anyone officially involved in producing or managing Riot Games properties. Riot Games, and all associated properties are trademarks or registered trademarks of Riot Games, Inc.*
