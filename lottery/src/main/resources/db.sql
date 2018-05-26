DELIMITER $$
DROP PROCEDURE IF EXISTS `generate_detNo` $$
-- 订单编号由12位日期+5位流水号生成
CREATE PROCEDURE `generate_detNo`(IN prefix VARCHAR(2), OUT newDetNo VARCHAR(25))
  BEGIN
    -- 当前日期
    DECLARE currentDate VARCHAR(15);
    -- 当前时间
    DECLARE currentTime VARCHAR(15);
    -- 离现在最近的订单编号的流水号
    DECLARE maxNo INT DEFAULT 0;
    -- 离现在最近的订单编号
    DECLARE oldBetNo VARCHAR(25) DEFAULT '';

    -- 根据年月日时分生成订单编号的日期部分
    SELECT DATE_FORMAT(NOW(), '%Y%m%d%H%i')
    INTO currentTime;
    -- SELECT currentTime;
    SELECT DATE_FORMAT(NOW(), '%Y%m%d')
    INTO currentDate;
    -- SELECT currentDate;

    SELECT IFNULL(betNo, '')
    INTO oldBetNo
    FROM betRecord
    -- 截取年月日,起始位(1开始),长度
    WHERE SUBSTRING(betNo, 1, 10) = CONCAT(prefix, currentDate) AND LENGTH(betNo) = 20
    ORDER BY id DESC
    LIMIT 1;
    -- SELECT oldBetNo;

    IF oldBetNo != ''
    THEN
      -- 截取订单后6位
      SET maxNo = CONVERT(SUBSTRING(oldBetNo, -6), DECIMAL);
      -- SELECT maxNo;
    END IF;
    -- 不足6位用0填充左边
    SELECT CONCAT(prefix, currentTime, LPAD((maxNo + 1), 6, '0'))
    INTO newDetNo;
    -- SELECT newDetNo;
  END $$
DELIMITER ;