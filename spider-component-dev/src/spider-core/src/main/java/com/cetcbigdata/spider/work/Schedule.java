package com.cetcbigdata.spider.work;

import com.alibaba.fastjson.JSON;
import com.cetcbigdata.spider.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created with IDEA
 * author:Matthew
 * Date:2018-11-6
 * Time:17:04
 */
@Component
public class Schedule {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${crawler.task.lock}")
    private String LOCK;

    @Value("${crawler.task.count}")
    private String countTask;

    @Value("${crawler.task.queue}")
    private String TASK_QUEUE;

    @Value("${crawler.task.area}")
    private String area;
    @Value("${crawler.task.dataBase}")
    private String dataBase;
    @Value("${crawler.task.collection}")
    private String collectionName;


    private static final Logger LOG = LoggerFactory.getLogger(Schedule.class);



    @Scheduled(cron = "0 0/5 * * * ? ")
    public void checkTask() {
        if (redisTemplate.hasKey(countTask)) {
            LOG.info("countTask {}",redisTemplate.boundValueOps(countTask).get().toString());
            Set<String> keySet = redisTemplate.keys(TASK_QUEUE);
            if (keySet.isEmpty()) {

                if (redisTemplate.opsForValue().setIfAbsent(LOCK, true)) {
                    Message message = new Message();
                    message.setDataBase(dataBase);
                    message.setArea(area);
                    message.setCollection(collectionName);
                    message.setTimestamp(System.currentTimeMillis());
                    sendMessage("crawler", JSON.toJSONString(message));
                    redisTemplate.delete(countTask);
                    redisTemplate.expire(LOCK,5,TimeUnit.MINUTES);
                }
            }
        }
    }

    public void sendMessage(String topic, String data) {
        LOG.info("kafka sendMessage start");
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, data);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                LOG.error("kafka sendMessage error, ex = {}, topic = {}, data = {}", ex, topic, data);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOG.info("kafka sendMessage success topic = {}, data = {}",topic, data);
            }
        });
        LOG.info("kafka sendMessage end");
    }
}
