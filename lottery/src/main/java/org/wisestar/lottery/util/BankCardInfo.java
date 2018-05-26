package org.wisestar.lottery.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.wisestar.lottery.exception.ErrorText;
import org.wisestar.lottery.exception.ServiceException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangxu
 * @date 2017/11/17
 */
public class BankCardInfo {


    private static Map<String, String> bankMap = new HashMap<>();

    /**
     * {"bank":"CMB","validated":true,"cardType":"DC","key":"6214850270509158","messages":[],"stat":"ok"}
     * DC储蓄卡CC信用卡
     *
     * @param cardNo 卡号
     * @return 银行名称
     */
    //
    public static String getBankInfo(String cardNo) {
        String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=%s&cardBinCheck=true";
        RestTemplate restTemplate = new RestTemplate();
        String content = restTemplate.getForObject(String.format(url, cardNo), String.class);

        if (StringUtils.isEmpty(content)) {
            throw new ServiceException(ErrorText.ERROR_BANK_NETWORK);
        }
        JSONObject json = JSON.parseObject(content);
        if (!json.getBooleanValue("validated")) {
            throw new ServiceException(ErrorText.ERROR_BANK_CARDNO);
        }
        if (!"DC".equals(json.getString("cardType"))) {
            throw new ServiceException(ErrorText.ERROR_BANK_CARDTYPE);
        }
        String bank = json.getString("bank");
        return bankMap.get(bank);
    }

    static {
        bankMap.put("SRCB", "深圳农村商业银行");
        bankMap.put("BGB", "广西北部湾银行");
        bankMap.put("SHRCB", "上海农村商业银行");
        bankMap.put("BJBANK", "北京银行");
        bankMap.put("WHCCB", "威海市商业银行");
        bankMap.put("BOZK", "周口银行");
        bankMap.put("KORLABANK", "库尔勒市商业银行");
        bankMap.put("SPABANK", "平安银行");
        bankMap.put("SDEB", "顺德农商银行");
        bankMap.put("HURCB", "湖北省农村信用社");
        bankMap.put("WRCB", "无锡农村商业银行");
        bankMap.put("BOCY", "朝阳银行");
        bankMap.put("CZBANK", "浙商银行");
        bankMap.put("HDBANK", "邯郸银行");
        bankMap.put("BOC", "中国银行");
        bankMap.put("BOD", "东莞银行");
        bankMap.put("CCB", "中国建设银行");
        bankMap.put("ZYCBANK", "遵义市商业银行");
        bankMap.put("SXCB", "绍兴银行");
        bankMap.put("GZRCU", "贵州省农村信用社");
        bankMap.put("ZJKCCB", "张家口市商业银行");
        bankMap.put("BOJZ", "锦州银行");
        bankMap.put("BOP", "平顶山银行");
        bankMap.put("HKB", "汉口银行");
        bankMap.put("SPDB", "上海浦东发展银行");
        bankMap.put("NXRCU", "宁夏黄河农村商业银行");
        bankMap.put("NYBANK", "广东南粤银行");
        bankMap.put("GRCB", "广州农商银行");
        bankMap.put("BOSZ", "苏州银行");
        bankMap.put("HZCB", "杭州银行");
        bankMap.put("HSBK", "衡水银行");
        bankMap.put("HBC", "湖北银行");
        bankMap.put("JXBANK", "嘉兴银行");
        bankMap.put("HRXJB", "华融湘江银行");
        bankMap.put("BODD", "丹东银行");
        bankMap.put("AYCB", "安阳银行");
        bankMap.put("EGBANK", "恒丰银行");
        bankMap.put("CDB", "国家开发银行");
        bankMap.put("TCRCB", "江苏太仓农村商业银行");
        bankMap.put("NJCB", "南京银行");
        bankMap.put("ZZBANK", "郑州银行");
        bankMap.put("DYCB", "德阳商业银行");
        bankMap.put("YBCCB", "宜宾市商业银行");
        bankMap.put("SCRCU", "四川省农村信用");
        bankMap.put("KLB", "昆仑银行");
        bankMap.put("LSBANK", "莱商银行");
        bankMap.put("YDRCB", "尧都农商行");
        bankMap.put("CCQTGB", "重庆三峡银行");
        bankMap.put("FDB", "富滇银行");
        bankMap.put("JSRCU", "江苏省农村信用联合社");
        bankMap.put("JNBANK", "济宁银行");
        bankMap.put("CMB", "招商银行");
        bankMap.put("JINCHB", "晋城银行JCBANK");
        bankMap.put("FXCB", "阜新银行");
        bankMap.put("WHRCB", "武汉农村商业银行");
        bankMap.put("HBYCBANK", "湖北银行宜昌分行");
        bankMap.put("TZCB", "台州银行");
        bankMap.put("TACCB", "泰安市商业银行");
        bankMap.put("XCYH", "许昌银行");
        bankMap.put("CEB", "中国光大银行");
        bankMap.put("NXBANK", "宁夏银行");
        bankMap.put("HSBANK", "徽商银行");
        bankMap.put("JJBANK", "九江银行");
        bankMap.put("NHQS", "农信银清算中心");
        bankMap.put("MTBANK", "浙江民泰商业银行");
        bankMap.put("LANGFB", "廊坊银行");
        bankMap.put("ASCB", "鞍山银行");
        bankMap.put("KSRB", "昆山农村商业银行");
        bankMap.put("YXCCB", "玉溪市商业银行");
        bankMap.put("DLB", "大连银行");
        bankMap.put("DRCBCL", "东莞农村商业银行");
        bankMap.put("GCB", "广州银行");
        bankMap.put("NBBANK", "宁波银行");
        bankMap.put("BOYK", "营口银行");
        bankMap.put("SXRCCU", "陕西信合");
        bankMap.put("GLBANK", "桂林银行");
        bankMap.put("BOQH", "青海银行");
        bankMap.put("CDRCB", "成都农商银行");
        bankMap.put("QDCCB", "青岛银行");
        bankMap.put("HKBEA", "东亚银行");
        bankMap.put("HBHSBANK", "湖北银行黄石分行");
        bankMap.put("WZCB", "温州银行");
        bankMap.put("TRCB", "天津农商银行");
        bankMap.put("QLBANK", "齐鲁银行");
        bankMap.put("GDRCC", "广东省农村信用社联合社");
        bankMap.put("ZJTLCB", "浙江泰隆商业银行");
        bankMap.put("GZB", "赣州银行");
        bankMap.put("GYCB", "贵阳市商业银行");
        bankMap.put("CQBANK", "重庆银行");
        bankMap.put("DAQINGB", "龙江银行");
        bankMap.put("CGNB", "南充市商业银行");
        bankMap.put("SCCB", "三门峡银行");
        bankMap.put("CSRCB", "常熟农村商业银行");
        bankMap.put("SHBANK", "上海银行");
        bankMap.put("JLBANK", "吉林银行");
        bankMap.put("CZRCB", "常州农村信用联社");
        bankMap.put("BANKWF", "潍坊银行");
        bankMap.put("ZRCBANK", "张家港农村商业银行");
        bankMap.put("FJHXBC", "福建海峡银行");
        bankMap.put("FJNX", "福建省农村信用社联合社");
        bankMap.put("ZJNX", "浙江省农村信用社联合社");
        bankMap.put("LZYH", "兰州银行");
        bankMap.put("JSB", "晋商银行");
        bankMap.put("BOHAIB", "渤海银行");
        bankMap.put("CZCB", "浙江稠州商业银行");
        bankMap.put("YQCCB", "阳泉银行");
        bankMap.put("SJBANK", "盛京银行");
        bankMap.put("XABANK", "西安银行");
        bankMap.put("BSB", "包商银行");
        bankMap.put("JSBANK", "江苏银行");
        bankMap.put("FSCB", "抚顺银行");
        bankMap.put("HNRCU", "河南省农村信用");
        bankMap.put("COMM", "交通银行");
        bankMap.put("XTB", "邢台银行");
        bankMap.put("CITIC", "中信银行");
        bankMap.put("HXBANK", "华夏银行");
        bankMap.put("HNRCC", "湖南省农村信用社");
        bankMap.put("DYCCB", "东营市商业银行");
        bankMap.put("ORBANK", "鄂尔多斯银行");
        bankMap.put("BJRCB", "北京农村商业银行");
        bankMap.put("XYBANK", "信阳银行");
        bankMap.put("ZGCCB", "自贡市商业银行");
        bankMap.put("CDCB", "成都银行");
        bankMap.put("HANABANK", "韩亚银行");
        bankMap.put("CMBC", "中国民生银行");
        bankMap.put("LYBANK", "洛阳银行");
        bankMap.put("GDB", "广东发展银行");
        bankMap.put("ZBCB", "齐商银行");
        bankMap.put("CBKF", "开封市商业银行");
        bankMap.put("H3CB", "内蒙古银行");
        bankMap.put("CIB", "兴业银行");
        bankMap.put("CRCBANK", "重庆农村商业银行");
        bankMap.put("SZSBK", "石嘴山银行");
        bankMap.put("DZBANK", "德州银行");
        bankMap.put("SRBANK", "上饶银行");
        bankMap.put("LSCCB", "乐山市商业银行");
        bankMap.put("ICBC", "中国工商银行");
        bankMap.put("JZBANK", "晋中市商业银行");
        bankMap.put("HZCCB", "湖州市商业银行");
        bankMap.put("NHB", "南海农村信用联社");
        bankMap.put("XXBANK", "新乡银行");
        bankMap.put("JRCB", "江苏江阴农村商业银行");
        bankMap.put("YNRCC", "云南省农村信用社");
        bankMap.put("ABC", "中国农业银行");
        bankMap.put("GXRCU", "广西省农村信用");
        bankMap.put("PSBC", "中国邮政储蓄银行");
        bankMap.put("BZMD", "驻马店银行");
        bankMap.put("ARCU", "安徽省农村信用社");
        bankMap.put("GSRCU", "甘肃省农村信用");
        bankMap.put("LYCB", "辽阳市商业银行");
        bankMap.put("JLRCU", "吉林农信");
        bankMap.put("URMQCCB", "乌鲁木齐市商业银行");
        bankMap.put("XLBANK", "中山小榄村镇银行");
        bankMap.put("CSCB", "长沙银行");
        bankMap.put("JHBANK", "金华银行");
        bankMap.put("BHB", "河北银行");
        bankMap.put("NBYZ", "鄞州银行");
        bankMap.put("LSBC", "临商银行");
        bankMap.put("BOCD", "承德银行");
        bankMap.put("SDRCU", "山东农信");
        bankMap.put("NCB", "南昌银行");
        bankMap.put("TCCB", "天津银行");
        bankMap.put("WJRCB", "吴江农商银行");
        bankMap.put("CBBQS", "城市商业银行资金清算中心");
        bankMap.put("HBRCU", "河北省农村信用社");
    }

}
