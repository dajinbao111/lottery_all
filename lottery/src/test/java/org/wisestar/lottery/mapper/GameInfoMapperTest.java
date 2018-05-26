package org.wisestar.lottery.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisestar.lottery.entity.GameInfo;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameInfoMapperTest {
    @Autowired
    private GameInfoMapper gameInfoMapper;

    @Test
    public void getByGameId() throws Exception {
        for (int i = 0; i < 10; i++) {
            GameInfo gameInfo = gameInfoMapper.getByGameId("99187");
            System.out.println(gameInfo.getWeekday());
        }
    }


    @Test
    public void getLastUpdated() throws Exception {
        Date date = gameInfoMapper.getLastUpdated();
        System.out.println(date);
    }

    @Test
    public void findByLastUpdated() throws Exception {
        List<GameInfo> list = gameInfoMapper.findByLastUpdated(new Date());
        Assert.assertNull(list);
    }
}