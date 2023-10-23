package com.alinesno.infra.smart.brain.interp.core;

import com.alinesno.infra.smart.brain.interp.exception.InterpreterException;
import lombok.Data;

import java.util.List;

@Data
public class Interpreter {

    private List<Object> messages;
        private boolean downloadOpenProcedures;
        private boolean local;
        private Object procedures;
        private Object proceduresDb;
        private int numProcedures;
        private boolean debugMode;


    public String getSystemMessage() {
        return null ;
    }

    public String getRelevantProceduresString() throws InterpreterException {
        return null ;
    }

    public boolean isDebugMode() {
        return true ;
    }
}
