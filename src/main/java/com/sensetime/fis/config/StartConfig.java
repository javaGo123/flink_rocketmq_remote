package com.sensetime.fis.config;

import org.springframework.context.annotation.Configuration;

/**
 * .用来加载一些系统配置
 * @author z.hj
 * @Date 2019/7/8 21:54
 */
@Configuration
public class StartConfig {
    StartConfig(){
        System.setProperty("spring.application.name","msg-analyze-service");
    }
}
