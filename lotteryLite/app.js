let {
    WeToast
} = require('./wetoast/wetoast.js')

//app.js
App({
    WeToast, //注册小程序，接收一个Object参数

    onLaunch: function() {
        // 展示本地存储能力

    },


    // 判断是否是正整数
    isPositiveInteger: function(s) { //是否为正整数
        var re = /^[0-9]+$/;
        return re.test(s)
    },
    isEmptyObject(obj) {　　
        for (var key in obj) {　　　　
            return false; //返回false，不为空对象
            　　
        }　　　　
        return true; //返回true，为空对象
    },
    // 最大值最小值
    maxMin: function(arrs) {
        var maxN = arrs[0];
        var minN = arrs[0];
        var reArr = [];
        for (var i = 1; i < arrs.length; i++) {
            var cur = arrs[i];
            cur > maxN ? maxN = cur : null;
            cur < minN ? minN = cur : null;
        }
        reArr.push(minN);
        reArr.push(maxN);
        return reArr
            // console.log('最大' + maxN + ',最小' + minN)
    },
    globalData: {
        requestUrl: 'https://www.wisestar.org/lottery',
        appid: 'wxb3359252deb1547c',
        secret: '2cbbcc0106b44af9bdb2101e512dd19f',
        openId: null,
        jwtToken: null,
        /*竞彩购物车*/
        raceShopCart: [],
        /*竞彩场次*/
        raceIdSet: [],
        /*权限 */
        authorities: null,
        /*14场购物车*/
        fourteenShopCart: [],
        /*14场场次*/
        fourteengameIdSet: [],
        /*任9购物车*/
        nineShopCart: [],
        /*任9购场次*/
        ninegameIdSet: [],
        /*单关购物车*/
        singleShopCart: [],
        /*单关场次*/
        singleGameIdSet: [],
        maxMultiple: 10000,
        /*最大倍数*/
        /*支付成功的后退*/
        /*提现记录的后退*/
        accountDetailFlag: true
    },
    /*彩票类型 */
    lotteryType: {
        1: '大乐透',
        2: '排列三',
        3: '排列五',
        4: '七星彩',
        5: '胜负彩',
        6: '任选九',
        7: '竞彩足球',
        8: '竞足单关'
    },
    /*投注状态 */
    betState: {
        1: '待支付',
        2: '支付不成功',
        3: '待出票',
        4: '出票不成功',
        5: '待开奖',
        6: '未中奖',
        7: '中奖'
    }

})