package com.alinesno.infra.smart.brain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatEvent {

    private String message;
    private String businessId ;
    private String promptId ;

}
