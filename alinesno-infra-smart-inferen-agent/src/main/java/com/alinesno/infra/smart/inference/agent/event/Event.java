package com.alinesno.infra.smart.inference.agent.event;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Event {

    private String code;
    private String subject;
    private String predicate;
    private String object;

    public Event(String code, String subject, String predicate, String object) {
        this.code = code;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

}