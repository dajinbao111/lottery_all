package org.wisestar.lottery.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wisestar.lottery.dto.AccountRecordPage;
import org.wisestar.lottery.dto.AddBankCardDto;
import org.wisestar.lottery.dto.UserAccountDto;
import org.wisestar.lottery.dto.UserDto;
import org.wisestar.lottery.entity.AccountChangeEnum;
import org.wisestar.lottery.entity.User;
import org.wisestar.lottery.entity.UserAccount;
import org.wisestar.lottery.entity.UserAccountRecord;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.mapper.UserAccountMapper;
import org.wisestar.lottery.mapper.UserAccountRecordMapper;
import org.wisestar.lottery.mapper.UserMapper;
import org.wisestar.lottery.util.BeanUtils;
import org.wisestar.lottery.util.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangxu
 * @date 2017/10/23
 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserAccountMapper userAccountMapper;
    private final UserAccountRecordMapper userAccountRecordMapper;

    @Autowired
    public UserService(UserMapper userMapper,
                       UserAccountMapper userAccountMapper,
                       UserAccountRecordMapper userAccountRecordMapper) {
        this.userMapper = userMapper;
        this.userAccountMapper = userAccountMapper;
        this.userAccountRecordMapper = userAccountRecordMapper;
    }

    public void updateUserInfo(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void withdraw(String openId, Double amount) {
        UserAccount userAccount = userAccountMapper.getByOpenId(openId);
        if (amount > userAccount.getBalance()) {
            throw new ServiceException("提现的金额大于账户余额");
        }
        Double newBalance = userAccount.getBalance() - amount;

        //资金变动记录
        UserAccountRecord record = new UserAccountRecord();
        record.setOpenId(openId);
        record.setRecordTime(new Date());
        record.setAccountChange(AccountChangeEnum.WITHDRAW.getValue());
        record.setChangeAmount(-amount);
        record.setBalance(newBalance);
        //提现审核
        record.setApproval(0);
        //记录账户变动
        userAccountRecordMapper.insertSelective(record);
        //更新账户
        int count = userAccountMapper.updateBalance(openId, newBalance);
        if (count != 1) {
            throw new ServiceException("更新账户余额失败");
        }
    }

    public void saveUserSessionKey(String openId, String sessionKey) {
        User user = userMapper.getByOpenId(openId);
        if (user == null) {
            user = new User();
            user.setOpenId(openId);
            user.setSessionKey(sessionKey);
            user.setCreateDate(new Date());
            userMapper.insertSelective(user);
        } else {
            userMapper.updateSessionKey(openId, sessionKey);
        }
    }

    /**
     * 获取用户信息
     *
     * @param openId
     * @return 如果不存在，返回null
     */
    public UserDto getUserInfo(String openId) {
        User user = userMapper.getByOpenId(openId);

        if (user != null) {
            UserDto target = new UserDto();
            BeanUtils.copyProperties(user, target);

            UserAccount userAccount = userAccountMapper.getByOpenId(openId);
            if (userAccount == null) {
                //表里面初始化一条记录
                userAccount = new UserAccount();
                userAccount.setOpenId(openId);
                userAccount.setBalance(0.0);
                userAccountMapper.insertSelective(userAccount);
            }

            //填充用户余额
            target.setBalance(userAccount.getBalance());

            return target;
        }
        return null;
    }

    /**
     * 返回用户账户
     *
     * @param openId
     * @return
     */
    public UserAccountDto getUserAccount(String openId) {
        UserAccount userAccount = userAccountMapper.getByOpenId(openId);
        if (userAccount != null) {
            return userAccount.copyTo();
        }
        return null;
    }

    /**
     * 添加银行卡
     *
     * @param addBankCardDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBankInfo(AddBankCardDto addBankCardDto) {
        int count = userAccountMapper.updateBankInfo(addBankCardDto.getOpenId(),
                addBankCardDto.getBankName(),
                addBankCardDto.getBankAccount(),
                addBankCardDto.getBankCard());
        if (count != 1) {
            throw new ServiceException("绑定银行卡失败");
        }
    }

    /**
     * 账户变动记录
     *
     * @param openId
     * @param accountChange
     */
    public AccountRecordPage listAccountRecord(Integer page, Integer limit, String openId, Integer accountChange) {
        PageHelper.startPage(page, limit);
        List<UserAccountRecord> accountRecordList = userAccountRecordMapper.findByOpenIdAndAccountChange(openId, accountChange);
        PageInfo<UserAccountRecord> pageInfo = new PageInfo<>(accountRecordList);

        AccountRecordPage accountRecordPage = new AccountRecordPage();
        accountRecordPage.setPageNum(pageInfo.getPageNum());
        accountRecordPage.setPages(pageInfo.getPages());

        Map<String, Object> item;
        for (UserAccountRecord record : accountRecordList) {
            item = new HashMap<>(4);

            String accountChangeText = AccountChangeEnum.getText(record.getAccountChange());
            if (record.getAccountChange().equals(AccountChangeEnum.WITHDRAW.getValue()) &&
                    record.getApproval().equals(0)) {
                item.put("accountChange", String.format("%s(处理中)", accountChangeText));
            } else {
                item.put("accountChange", accountChangeText);
            }
            item.put("balance", record.getBalance());
            item.put("changeAmount", record.getChangeAmount());
            item.put("recordTime", DateUtils.date2String(record.getRecordTime(), "yyyy-MM-dd HH:mm"));
            accountRecordPage.getList().add(item);
        }

        return accountRecordPage;
    }

    /**
     * 提现列表
     *
     * @param page
     * @param limit
     * @return
     */
    public AccountRecordPage listWithdraw(Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<UserAccountRecord> accountRecordList = userAccountRecordMapper.findWithdraw();
        PageInfo<UserAccountRecord> pageInfo = new PageInfo<>(accountRecordList);

        AccountRecordPage accountRecordPage = new AccountRecordPage();
        accountRecordPage.setPageNum(pageInfo.getPageNum());
        accountRecordPage.setPages(pageInfo.getPages());

        Map<String, Object> item;
        for (UserAccountRecord record : accountRecordList) {
            item = new HashMap<>(7);
            item.put("recordId", record.getId());
            item.put("changeAmount", record.getChangeAmount());
            item.put("recordTime", DateUtils.date2String(record.getRecordTime(), "yyyy-MM-dd HH:mm"));

            UserAccount userAccount = userAccountMapper.getByOpenId(record.getOpenId());
            item.put("bankAccount", userAccount.getBankAccount());
            item.put("bankCard", userAccount.getBankCard());
            item.put("bankName", userAccount.getBankName());

            User user = userMapper.getByOpenId(record.getOpenId());
            item.put("nickname", user.getNickName());
            accountRecordPage.getList().add(item);
        }

        return accountRecordPage;
    }

    /**
     * 处理提现操作
     *
     * @param recordId
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleWithdraw(Long recordId) {
        UserAccountRecord userAccountRecord = userAccountRecordMapper.selectByPrimaryKey(recordId);
        if (userAccountRecord == null) {
            throw new ServiceException("user account record not exist");
        }
        int count = userAccountRecordMapper.updateApproval(recordId);
        if (count != 1) {
            throw new ServiceException("提现处理失败");
        }
    }
}
