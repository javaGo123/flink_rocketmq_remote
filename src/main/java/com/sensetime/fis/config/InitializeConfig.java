package com.sensetime.fis.config;

import com.sensetime.fis.service.StandStackService;
import lombok.extern.slf4j.Slf4j;
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
        standStackService.StandStackDeserializationSchema();
    }

}
