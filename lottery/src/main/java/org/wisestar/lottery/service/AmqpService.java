package org.wisestar.lottery.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.wisestar.lottery.config.RabbitMQConfig;
import org.wisestar.lottery.entity.BetRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangxu
 * @date 2017/11/10
 */
@Service
public class AmqpService {

    @Value("${spring.rabbitmq.api}")
    private String api;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;

    private final AmqpTemplate rabbitTemplate;
    private final RestTemplate restTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AmqpService(AmqpTemplate rabbitTemplate,
                       RestTemplate restTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = restTemplate;
    }

    /**
     * 放入队列
     *
     * @param queue     队列名称
     * @param betRecord
     * @throws JsonProcessingException
     */
    public void send(String queue, BetRecord betRecord) {
        try {
            String message = objectMapper.writeValueAsString(betRecord);
            rabbitTemplate.convertAndSend(queue, message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从队列收取数据
     *
     * @param queue 队列名称
     * @param count 数量
     * @return
     * @throws IOException
     */
    public List<BetRecord> receive(String queue, Integer count) {
        List<BetRecord> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String message = (String) rabbitTemplate.receiveAndConvert(queue);
            try {
                if (message == null) {
                    break;
                }
                BetRecord betRecord = objectMapper.readValue(message, BetRecord.class);
                list.add(betRecord);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 获取队列当前大小
     *
     * @return
     */
    public Map<String, Long> size() {
        //初始化map
        Map<String, Long> queueSize = new HashMap<>();
        queueSize.put(RabbitMQConfig.QUEUE_PENDING_TICKET, 0L);
        queueSize.put(RabbitMQConfig.QUEUE_WIN_PRIZE, 0L);

        HttpHeaders headers = new HttpHeaders();
        //设置凭证
        String up = String.format("%s:%s", username, password);
        String credentials = Base64.encodeBase64String(up.getBytes());
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(api, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String json = response.getBody();
            JSONArray array = JSON.parseArray(json);
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                Long messages = object.getLong("messages");
                if (RabbitMQConfig.QUEUE_PENDING_TICKET.equals(name)) {
                    queueSize.put(RabbitMQConfig.QUEUE_PENDING_TICKET, messages);
                } else if (RabbitMQConfig.QUEUE_WIN_PRIZE.equals(name)) {
                    queueSize.put(RabbitMQConfig.QUEUE_WIN_PRIZE, messages);
                }
            }
        }

        return queueSize;
    }
}

