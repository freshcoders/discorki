# Discorki 🚀

Discorki is a Discord bot that tracks in game achievements for you and your friends. Did someone just promote? Did someone just hard int? Discorki will let you know.

In addition, the bot offers additional features such as generating teams and champions with the ARAM command, and the ability to subscribe to a friend's game to know when they're available.

The bot is currently in closed beta. Open beta is expected to start early april!

As soon as the bot is live, you can add it via [discorki.nl](https://discorki.nl).

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
app.host = discorki.nl
app.developerDiscordIds[0] = yourDiscordId
```

You can then run the application using the following command:

```Bash
`mvn spring-boot:run`
```

## Docker compose

You can also run this containerized with docker-compose. It will auto set-up SSL via Traefik. The database is not exposed so it won't be necessary to change the username and passwords. The Traefik dashboard is also not exposed.

```yml
version: "3.9"
services:
  traefik:
    image: "traefik:v2.9"
    container_name: "traefik"
    command:
      - "--api.insecure=false"
      - "--providers.docker=true"
      - "--providers.docker.exposedbydefault=false"
      - "--entrypoints.web.address=:80"
      - "--entrypoints.websecure.address=:443"
      - "--certificatesresolvers.myresolver.acme.tlschallenge=true"
      - "--certificatesresolvers.myresolver.acme.email=${HTTPS_EMAIL}"
      - "--certificatesresolvers.myresolver.acme.storage=/letsencrypt/acme.json"
      - "--entrypoints.web.http.redirections.entryPoint.to=websecure"
      - "--entrypoints.web.http.redirections.entryPoint.scheme=https"
      - "--entrypoints.web.http.redirections.entrypoint.permanent=true"
    ports:
      - "443:443"
    volumes:
      - "./letsencrypt:/letsencrypt"
      - "/var/run/docker.sock:/var/run/docker.sock:ro"
  discorki:
    build: https://github.com/freshcoders/discorki.git
    ports:
      - "8080:8080"
    links:
      - db:db
    environment:
      - spring_jpa_hibernate_ddl-auto=update
      - spring_datasource_url=jdbc:mariadb://db:3306/discorki
      - spring_datasource_username=discorki
      - spring_datasource_password=discorki
      - spring_datasource_driver-class-name=org.mariadb.jdbc.Driver
      - spring_jpa_open-in-view=false
      - riot_key=${RIOT_API_KEY}
      - riot_platformRouting=${RIOT_PLATFORM_ROUTING}
      - riot_regionalRouting=${RIOT_REGIONAL_ROUTING}
      - discord_token=${DISCORD_BOT_TOKEN}
      - app_host=${HOST}
    depends_on:
      - db
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.discorki.rule=Host(`${HOST}`)"
      - "traefik.http.routers.discorki.entrypoints=websecure"
      - "traefik.http.routers.discorki.tls.certresolver=myresolver"
  db:
    image: mariadb
    restart: always
    environment:
      MARIADB_ROOT_PASSWORD: discorki
      MARIADB_DATABASE: discorki
      MARIADB_USER: discorki
      MARIADB_PASSWORD: discorki
    volumes:
     - ./mariadb:/var/lib/mysql
```

This is the `.env`-file:

```Bash
HOST=discorki.nl
HTTPS_EMAIL=
RIOT_API_KEY=
DISCORD_BOT_TOKEN=
RIOT_PLATFORM_ROUTING=euw1
RIOT_REGIONAL_ROUTING=europe
```

## Disclaimer

*Discorki isn't endorsed by Riot Games and doesn't reflect the views or opinions of Riot Games or anyone officially involved in producing or managing Riot Games properties. Riot Games, and all associated properties are trademarks or registered trademarks of Riot Games, Inc.*
