package com.sensetime.fis.service;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * . 任务服务
 * @author z.hj
 * @Date 2019/7/9 14:49
 */
public interface StandStackService {

    /**
     * 任务-1
     */
    void StandStackDeserializationSchema(StreamExecutionEnvironment env);
}
