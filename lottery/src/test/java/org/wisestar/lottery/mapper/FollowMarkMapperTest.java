package org.wisestar.lottery.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisestar.lottery.entity.LotteryTypeEnum;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FollowMarkMapperTest {
    @Autowired
    private FollowMarkMapper followMarkMapper;

    @Test
    public void findCurrent() throws Exception {
        Integer[] types = {LotteryTypeEnum.FOOTBALL.getValue(), LotteryTypeEnum.SINGLE.getValue()};
        followMarkMapper.findCurrent(Arrays.asList(types));
    }

}