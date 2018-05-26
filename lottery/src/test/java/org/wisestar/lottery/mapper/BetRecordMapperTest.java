package org.wisestar.lottery.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisestar.lottery.entity.BetRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BetRecordMapperTest {

    @Test
    public void findByOpenIdAndBetStates() throws Exception {
        List<Integer> list = new ArrayList<>();
        //list.add(1);
//        list.add(3);
        List<BetRecord> recordList = betRecordMapper.findByOpenIdAndStates(null, list);
        System.out.println(recordList);
    }

    @Autowired
    private BetRecordMapper betRecordMapper;

    @Test
    public void generateNo() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("prefix", "AA");
        betRecordMapper.generateNo(map);
        System.out.println("****************");
        System.out.println(map.get("betNo"));
        System.out.println("****************");
    }

}