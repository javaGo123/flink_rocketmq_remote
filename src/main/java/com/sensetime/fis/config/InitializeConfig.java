package com.sensetime.fis.config;

import com.sensetime.fis.service.StandStackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * . 初始化加载
 * @author z.hj
 * @Date 2019/7/9 14:41
 */
@Slf4j
@Component
public class InitializeConfig implements CommandLineRunner {

    @Autowired
    private StandStackService standStackService;

    @Override
    public void run(String... args) throws Exception {
        log.info("start job 1 ....");
        String[] jars = {
                "D:\\code\\rws3\\msg-analyze-service\\target\\msg-analyze-service.jar.original"
        };
        StreamExecutionEnvironment env = StreamExecutionEnvironment
                .createRemoteEnvironment("10.5.2.97", 8061, 2, jars);

        standStackService.StandStackDeserializationSchema(env);
    }

}
