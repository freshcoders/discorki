package com.alistats.discorki.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alistats.discorki.model.constants.Queue;
import com.fasterxml.jackson.databind.ObjectMapper;

// TODO: maybe split this up in different files
// TODO: maybe use a database instead of json files (load on startup)
// https://github.com/freshcoders/discorki/issues/16
@Service
public class GameConstantService {

    private ObjectMapper mapper = new ObjectMapper();
    private File queueFile = new File("src/main/resources/constants/queues.json");
    private File gameModeFile = new File("src/main/resources/constants/gameModes.json");
    private File gameTypeFile = new File("src/main/resources/constants/gameTypes.json");
    private File seasonFile = new File("src/main/resources/constants/seasons.json");
    private File mapsFile = new File("src/main/resources/constants/maps.json");

    public Queue getQueue(Integer queueId) {
        List<Queue> queues = getQueues();
        for (Queue queue : queues) {
            if (queue.getQueueId().equals(queueId)) {
                return queue;
            }
        }
        return null;
    }

    private List<Queue> getQueues() {
        try {
            Queue[] queues = mapper.readValue(queueFile, Queue[].class);
            List<Queue> queueList = Arrays.asList(queues);

            return queueList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
