package com.alinesno.infra.smart.brain.inference.executor;

import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ExecutorGPTTest {

    private static final Logger log = LoggerFactory.getLogger(ExecutorGPTTest.class) ;

    @Test
    void getResult(){
        log.info("get result");
    }

}