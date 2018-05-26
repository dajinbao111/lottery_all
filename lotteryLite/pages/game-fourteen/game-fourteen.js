var app = getApp()

var shoppingCart = new Map() //购物车
var gameData = {} //存储一次后台请求的数据
var gameIdSet = new Set() //用来统计选择的场次数
    // 判断是否为空对象
function isEmptyObject(obj) {　　
    for (var key in obj) {　　　　
        return false; //返回false，不为空对象
        　　
    }　　　　
    return true; //返回true，为空对象
}
// 判断是否为空对象
// page
Page({
    data: {
        optDate: {
            optArr: [],
            timeArr: [],
            endTime: '',
            activeIndex: 0
                // itemWidth: 0
        },
        mathContent: {
            mathData: []
        },
        accountData: {
            footballFlag: false,
            num: 0,
            typeFlag: true

        },
        // 判断有无可买
        viewFlag: true,
        _height: null
    },
    onLoad: function(options) {
        // 页面初始化 options为页面跳转所带来的参数

        this.reset();
        this.fetchData();
        new app.WeToast(); //weToatst

    },
    onReady: function() {
        // 页面渲染完成
    },
    onShow: function() {
        // 页面显示

    },
    onHide: function() {
        // 页面隐藏
    },
    onUnload: function() {
        // 页面关闭

    },

    /*点击事件*/
    tabChange: function(e) {
        var index = e.currentTarget.dataset.index;
        this.data.optDate.activeIndex = index


        var phaseId = this.data.optDate.optArr[index]
        this.data.optDate.endTime = this.data.optDate.timeArr[index]
        this.data.mathContent.mathData = [];

        for (var no in gameData[phaseId]) {
            this.data.mathContent.mathData.push(gameData[phaseId][no])
        }
        this.setData({
                mathContent: this.data.mathContent,
                optDate: this.data.optDate
            })
            //切换期号之后清空购物车数据和场次数据
        shoppingCart.clear()
        gameIdSet.clear()
    },

    // 每次退出清空数据
    reset: function() {
        gameIdSet.clear();
        shoppingCart.clear()
    },
    //下注选择
    chooseAwards: function(e) {
        var that = this;
        var serialId = e.currentTarget.dataset.serialid
        var phaseId = e.currentTarget.dataset.phaseid
        var buy = e.currentTarget.dataset.buy //表示选择的胜平负
        for (var i = 0; i < this.data.mathContent.mathData.length; i++) {
            var item = this.data.mathContent.mathData[i];
            if (item.phaseId == phaseId && item.no == serialId) {
                //期号+序号+胜平负 作为key
                var key = phaseId + '-' + serialId + '-' + buy
                    // 3胜
                if (buy == 3) {
                    this.data.mathContent.mathData[i].flag3 = !item.flag3
                    if (item.flag3) {
                        //加入购物车
                        shoppingCart.set(key, item)
                        gameIdSet.add(serialId)
                    } else {
                        //移除购物车
                        shoppingCart.delete(key);
                    }
                }
                // 1平
                if (buy == 1) {
                    this.data.mathContent.mathData[i].flag1 = !item.flag1
                    if (item.flag1) {
                        //加入购物车
                        shoppingCart.set(key, item)
                        gameIdSet.add(serialId)
                    } else {
                        //移除购物车
                        shoppingCart.delete(key)
                    }
                }
                // 0负
                if (buy == 0) {
                    this.data.mathContent.mathData[i].flag0 = !item.flag0
                    if (item.flag0) {
                        //加入购物车
                        shoppingCart.set(key, item)
                        gameIdSet.add(serialId)
                    } else {
                        //移除购物车
                        shoppingCart.delete(key)
                    }
                }
                if (!item.flag3 && !item.flag1 && !item.flag0) {
                    gameIdSet.delete(serialId)
                }
                this.data.accountData.num = gameIdSet.size;

                this.setData({
                    mathContent: this.data.mathContent,
                    accountData: this.data.accountData
                })
                break
            }
        }
    },

    //期号选择事件
    bindPickerChange: function(e) {

    },

    //获取后台数据
    fetchData: function() {
        var that = this
        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        wx.request({
            url: app.globalData.requestUrl + '/game/getGameFouteen',
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {

                wx.hideToast()

                if (res.statusCode == 200) {
                    if (isEmptyObject(res.data)) {

                        that.setData({
                            viewFlag: false
                        })
                        return
                    }

                    var idx = 0
                    for (var phaseId in res.data) {

                        //期号放入Picker数组内
                        that.data.optDate.optArr.push(phaseId)
                            //存放每组的截止时间
                        var endTime = ''
                        for (var serialId in res.data[phaseId]) {
                            var item = {}
                                //后台数据属性和前台数据属性互转
                            item.no = res.data[phaseId][serialId].serialId
                            item.phaseId = res.data[phaseId][serialId].phaseId
                            item.type = res.data[phaseId][serialId].gameType
                            item.time = res.data[phaseId][serialId].startTime.substring(5, 16)
                            item.homeTeam = res.data[phaseId][serialId].hostTeam
                            item.visitingTeam = res.data[phaseId][serialId].guestTeam
                            item.homeTeamOdds = '胜' + res.data[phaseId][serialId].winRatio
                            item.flatOdds = '平' + res.data[phaseId][serialId].drawRatio
                            item.homeLoseOdds = '负' + res.data[phaseId][serialId].loseRatio
                            item.flag3 = false //胜标识
                            item.flag1 = false //平标识
                            item.flag0 = false //负标识

                            endTime = res.data[phaseId][serialId].endPostTime.substring(5, 16)

                            res.data[phaseId][serialId] = item
                            if (idx == 0) {
                                //把第一组数据用于显示
                                that.data.optDate.endTime = endTime
                                that.data.mathContent.mathData.push(item)
                            }
                        }
                        idx++
                        that.data.optDate.timeArr.push(endTime)

                    }

                    gameData = res.data
                    that.setData({
                        optDate: that.data.optDate,
                        mathContent: that.data.mathContent
                    });



                    var span = wx.getSystemInfoSync().windowWidth / that.data.optDate.optArr.length + 'px';


                    wx.getSystemInfo({
                        success: function(res) {
                            that.setData({
                                _height: res.windowHeight
                            })
                        }
                    });
                    // that.data.optDate.itemWidth = that.data.optDate.optArr.length <= 5 ? span : '160rpx';
                    // console.log(that.data.optDate.itemWidth)
                    // 页面显示
                    that.setData({
                        optDate: that.data.optDate
                    });
                } else {
                    that.wetoast.toast({
                        title: '获取数据失败'
                    });
                }
            },
            fail: function() {
                wx.hideToast()

                that.wetoast.toast({
                    title: '获取数据失败'
                });
            }
        })
    },
    // 清空购物车
    delChoose: function() {
        var that = this;
        wx.showModal({
            title: '清空已选',
            content: '是否确定已选',
            success: function(res) {
                if (res.confirm) {
                    shoppingCart.clear();
                    gameIdSet.clear();
                    for (var i = 0; i < that.data.mathContent.mathData.length; i++) {
                        that.data.mathContent.mathData[i].flag0 = false;
                        that.data.mathContent.mathData[i].flag1 = false;
                        that.data.mathContent.mathData[i].flag3 = false;
                    }
                    that.data.accountData.num = gameIdSet.size;

                    that.setData({
                        mathContent: that.data.mathContent,
                        accountData: that.data.accountData
                    })

                }
            }
        })
    },
    // 提交确定
    enter: function() {
        if (gameIdSet.size != 14) {
            this.wetoast.toast({
                title: '请选择14场'
            });
            return
        }
        app.globalData.fourteenShopCart = shoppingCart;
        app.globalData.fourteengameIdSet = gameIdSet;
        wx.navigateTo({
            url: '../bet-nine/bet-nine?type=5'
        })
    }
})