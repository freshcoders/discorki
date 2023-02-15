package com.alistats.discorki.model;

import lombok.Getter;

@Getter
public enum QueueType {
    RANKED_SOLO_5x5("Ranked Solo/Duo", 420),
    RANKED_FLEX_SR("Ranked Flex", 440);

    private final String name;
    private final int queueId;

    QueueType(String name, int queueId) {
        this.name = name;
        this.queueId = queueId;
    }

    public static QueueType getQueueType(int queueId) {
        for (QueueType queueType : QueueType.values()) {
            if (queueType.getQueueId() == queueId) {
                return queueType;
            }
        }

        return null;
    }
}
