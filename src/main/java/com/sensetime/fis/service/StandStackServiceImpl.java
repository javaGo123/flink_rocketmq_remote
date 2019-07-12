package com.sensetime.fis.service;

import com.google.gson.Gson;
import com.sensetime.fis.common.serialization.StandStackDeserializationSchema;
import com.sensetime.fis.common.serialization.TupleKeyValueSerializationSchema;
import com.sensetime.fis.model.StandStackBean;
import com.sensetime.fis.redis.CustomRedisWrapper;
import com.sensetime.fis.util.CommonUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.rocketmq.flink.RocketMQConfig;
import org.apache.rocketmq.flink.RocketMQSink;
import org.apache.rocketmq.flink.RocketMQSource;
import org.apache.rocketmq.flink.common.selector.DefaultTopicSelector;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * .任务-1实现
 * @author z.hj
 * @Date 2019/7/9 14:50
 */
@Service
public class StandStackServiceImpl implements StandStackService{



    @Override
    public void StandStackDeserializationSchema(StreamExecutionEnvironment env) {

        Properties consumerProps = new Properties();
        consumerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.97:9876");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_GROUP, "stand_c_2");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_TOPIC, "stand_stack_source");

        SingleOutputStreamOperator<Tuple2<String, String>> dataStream = env.addSource(new RocketMQSource<StandStackBean>(new StandStackDeserializationSchema(), consumerProps))
                .map(new RichMapFunction<StandStackBean, Tuple2<String, String>>() {
                    BufferedImage image;
                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
//                        image = ImageIO.read(new FileInputStream("/home/birdview.png"));
                    }

                    @Override
                    public Tuple2<String, String> map(StandStackBean value) throws Exception {
                        int centerX = (value.getStack().get(0).getLocation().getX1() + value.getStack().get(0).getLocation().getX2()) / 2;
                        int centerY = (value.getStack().get(0).getLocation().getY1() + value.getStack().get(0).getLocation().getY2()) / 2;
//                        value.getStack().get(0).setBetting_box(CommonUtils.positionToBoxNum(image, centerX, centerY));
                        System.out.println("centerX==>" + centerX + "  centerY===>" + centerY + "  bettingBox===> " + value.getStack().get(0));
                        return new Tuple2<>(value.getStandKey(), new Gson().toJson(value));
                    }
                });


        //往redis里面写入
        //实例化Flink和Redis关联类FlinkJedisPoolConfig，设置Redis端口
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("10.5.2.97").setPort(6379).build();
        dataStream.addSink(new RedisSink<Tuple2<String,String>>(conf,new CustomRedisWrapper()));


        //往rocketmq中写入
        Properties producerProps = new Properties();
        producerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.97:9876");
        int msgDelayLevel = RocketMQConfig.MSG_DELAY_LEVEL05;
        producerProps.setProperty(RocketMQConfig.MSG_DELAY_LEVEL, String.valueOf(msgDelayLevel));

        dataStream.addSink(new RocketMQSink<Tuple2<String, String>>(new TupleKeyValueSerializationSchema(),new DefaultTopicSelector<>("obj-sink"),producerProps));

        try {
            env.execute("msg-analyze-service:testStandStack");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
