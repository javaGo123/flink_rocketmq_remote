package com.sensetime.fis.controller;


import com.google.gson.Gson;
import com.sensetime.fis.common.serialization.StandStackDeserializationSchema;
import com.sensetime.fis.common.serialization.TupleKeyValueSerializationSchema;
import com.sensetime.fis.model.StandStackBean;
import com.sensetime.fis.util.CommonUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import com.sensetime.fis.redis.CustomRedisWrapper;
import org.apache.rocketmq.flink.RocketMQConfig;
import org.apache.rocketmq.flink.RocketMQSink;
import org.apache.rocketmq.flink.RocketMQSource;
import org.apache.rocketmq.flink.common.selector.DefaultTopicSelector;
import org.springframework.web.bind.annotation.*;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author wangxinxin
 * @createTime 2019年7月10日上午10:29:14
 */

@RestController
public class FlinkController {

    private static BufferedImage image;

    @GetMapping(value = "/test")
    public void test() {
        System.out.println("This is msg-analyze-service!");
    }

}
