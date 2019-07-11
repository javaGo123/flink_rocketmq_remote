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

        String[] jars = {
                "D:\\code\\rws3\\msg-analyze-service\\target\\msg-analyze-service.jar.original"
        };

        StreamExecutionEnvironment env = StreamExecutionEnvironment
                .createRemoteEnvironment("10.5.2.97", 8061, 2, jars);

        try {
            image = ImageIO.read(new FileInputStream("src/main/resources/birdview.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Properties consumerProps = new Properties();
        consumerProps.setProperty(RocketMQConfig.NAME_SERVER_ADDR, "10.5.2.97:9876");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_GROUP, "stand_c_2");
        consumerProps.setProperty(RocketMQConfig.CONSUMER_TOPIC, "stand_stack_source");


        SingleOutputStreamOperator<Tuple2<String, String>> dataStream = env.addSource(new RocketMQSource<StandStackBean>(new StandStackDeserializationSchema(), consumerProps))
                .map(new MapFunction<StandStackBean, Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> map(StandStackBean value) throws Exception {
                        int centerX = (value.getStack().get(0).getLocation().getX1() + value.getStack().get(0).getLocation().getX2()) / 2;
                        int centerY = (value.getStack().get(0).getLocation().getY1() + value.getStack().get(0).getLocation().getY2()) / 2;
                        value.getStack().get(0).setBetting_box(CommonUtils.positionToBoxNum(image, centerX, centerY));
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
            env.execute("msg-analyze-service");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
