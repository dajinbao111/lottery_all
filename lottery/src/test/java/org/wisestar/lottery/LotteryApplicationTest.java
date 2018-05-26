package org.wisestar.lottery;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class LotteryApplicationTest {
//    @Autowired
//    private AmqpTemplate rabbitTemplate;

    @Test
    public void testSender() {
//        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_PENDING_TICKET, "test1");
    }

    @Test
    public void testRecevier() {
//       Object object = rabbitTemplate.receiveAndConvert("pending-ticket-queue");
//       System.out.println(object);
    }

    @Test
    public void test() throws Exception {
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8.toString());
        String credentials = Base64.encodeBase64String("lottery:lottery".getBytes());
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
        HttpEntity<String> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://120.76.157.149:15672/api/queues", HttpMethod.GET, request, String.class);
        System.out.println(responseEntity.getBody());
    }
}