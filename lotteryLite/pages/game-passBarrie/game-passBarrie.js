var app = getApp();
var shoppingCart = new Map() //购物车
var gameIdSet = new Set() //用来统计选择的场次数

Page({
    data: {
        accordionWrap: {
            accordionData: []
        },
        // 手风琴data
        // 结算
        accountData: {
            footballFlag: true,
            matchNum: 0,
            typeFlag: false
        },
        // /结算
        chooseData: [],
        deadline: '' /*截止时间*/

    },
    // 手风琴
    shoufenqin(e) {
        for (var i = 0; i < this.data.accordionWrap.accordionData.length; i++) {
            if (e.currentTarget.dataset.id == this.data.accordionWrap.accordionData[i].idx) {
                this.data.accordionWrap.accordionData[i].checkFlag = !this.data.accordionWrap.accordionData[i].checkFlag
            }
        };
        this.setData({
            accordionWrap: this.data.accordionWrap
        })
    },
    onLoad: function(options) {
        this.reset()
            // 页面初始化 options为页面跳转所带来的参数
        this.fetchData();
        new app.WeToast(); //weToatst

    },
    onReady: function() {
        // 页面渲染完成
    },
    onShow: function() {

        // 页面显示
    },
    // 获取数据
    fetchData: function() {
        var that = this;

        wx.stopPullDownRefresh() //停止下拉刷新
        wx.hideNavigationBarLoading() //完成停止加载
        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        wx.request({
            url: app.globalData.requestUrl + '/game/getGameInfo',
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT       
            success: function(res) {
                wx.hideToast()

                if (res.statusCode == 200) {

                    var groupIdx = 0
                    var gameGroup = []
                    var deadLine = '';
                    gameGroup[groupIdx] = {
                        idx: null,
                        checkFlag: true,
                        data: []
                    }
                    var tempGroup = []
                        //临时存放分组的名称
                    var weekTitle = ""
                    for (var weekday in res.data) {
                        var item = {}
                            //后台数据属性和前台数据属性互转
                        item.no = res.data[weekday].weekday
                        item.gameId = res.data[weekday].gameId
                        item.mathchTime = res.data[weekday].dueTime.substring(11, 16)
                        item.gameEventType = res.data[weekday].gameEventType
                        item.hostTeam = res.data[weekday].hostTeam
                        item.guestTeam = res.data[weekday].guestTeam
                        item.lastUpdated = res.data[weekday].lastUpdated
                        if (res.data[weekday].hadRangqiu) {
                            //存在未开售的情况
                            item.hadRangqiu = res.data[weekday].hadRangqiu
                            item.hadWinRatio = res.data[weekday].hadWinRatio
                            item.hadDrawRatio = res.data[weekday].hadDrawRatio
                            item.hadLoseRatio = res.data[weekday].hadLoseRatio
                            item.hadWinFlag = false
                            item.hadDrawFlag = false
                            item.hadLoseFlag = false
                        }
                        if (res.data[weekday].hhadRangqiu) {
                            item.hhadRangqiu = res.data[weekday].hhadRangqiu
                            item.hhadWinRatio = res.data[weekday].hhadWinRatio
                            item.hhadDrawRatio = res.data[weekday].hhadDrawRatio
                            item.hhadLoseRatio = res.data[weekday].hhadLoseRatio
                            item.hhadWinFlag = false
                            item.hhadDrawFlag = false
                            item.hhadLoseFlag = false
                        }
                        var title = res.data[weekday].weekday.substring(0, 2) + " " + res.data[weekday].gameDate
                        if (weekTitle == "") {
                            deadLine = item.lastUpdated
                            weekTitle = title
                            tempGroup.push(item)

                            that.setData({
                                deadline: deadLine
                            })
                        } else if (weekTitle == title) {
                            tempGroup.push(item)
                        } else {
                            gameGroup[groupIdx].data = tempGroup
                            gameGroup[groupIdx].idx = groupIdx
                            gameGroup[groupIdx].title = weekTitle
                            gameGroup[groupIdx].matchNum = tempGroup.length

                            weekTitle = title
                            tempGroup = []
                            tempGroup.push(item)
                            groupIdx++
                            gameGroup[groupIdx] = {
                                idx: null,
                                checkFlag: true,
                                data: []
                            }
                        }
                    }
                    gameGroup[groupIdx].data = tempGroup
                    gameGroup[groupIdx].idx = groupIdx
                    gameGroup[groupIdx].title = weekTitle
                    gameGroup[groupIdx].matchNum = tempGroup.length
                    that.data.accordionWrap.accordionData = gameGroup
                    that.setData({
                        accordionWrap: that.data.accordionWrap
                    })
                } else {
                    that.wetoast.toast({
                        title: '获取数据失败',
                        titleClassName: 'check_info'
                    });
                }
            },
            fail: function() {
                wx.hideToast()

                that.wetoast.toast({
                    title: '获取数据失败',
                    titleClassName: 'check_info'
                });
            }
        });
    },

    // /获取数据

    /*
    **
    选择比赛
    **
    */
    chooseAwards: function(e) {
        var that = this;
        var gameId = e.currentTarget.dataset.gameid;
        var ratio = e.currentTarget.dataset.ratio; /* 赔率(可能没有)*/
        var rangqiu = e.currentTarget.dataset.rangqiu; /*让球个数(可能没有)*/
        var buy = e.currentTarget.dataset.buy; //表示选择的胜平负 
        var accordionData = that.data.accordionWrap.accordionData /*为了不写这么长定义的变量*/
        if (typeof(ratio) == 'undefined') {
            return
        }
        for (var i = 0; i < accordionData.length; i++) {
            for (var j = 0; j < accordionData[i].data.length; j++) {
                var item = accordionData[i].data[j];
                if (item.gameId == gameId) {
                    // var key = gameId +  '-' + rangqiu + '-' + buy;
                    var key;
                    if (rangqiu == 0) {
                        key = gameId + '-' + '0' + '-' + buy + '-' + ratio;
                    } else {
                        key = gameId + '-' + '1' + '-' + buy + '-' + ratio;
                    }

                    // 不让球的3种情况
                    if (rangqiu == 0) {
                        // 不让球主胜
                        if (buy == 3) {
                            item.hadWinFlag = !item.hadWinFlag;
                            if (item.hadWinFlag) {
                                //加入购物车
                                shoppingCart.set(key, item)
                                gameIdSet.add(gameId);
                            } else {
                                //移除购物车
                                shoppingCart.delete(key);
                            }
                        };
                        // 不让球主胜平
                        if (buy == 1) {
                            item.hadDrawFlag = !item.hadDrawFlag;
                            if (item.hadDrawFlag) {
                                //加入购物车
                                shoppingCart.set(key, item)
                                gameIdSet.add(gameId)
                            } else {
                                //移除购物车
                                shoppingCart.delete(key)
                            }
                        };
                        // 不让球主负
                        if (buy == 0) {
                            item.hadLoseFlag = !item.hadLoseFlag;
                            if (item.hadLoseFlag) {
                                //加入购物车
                                shoppingCart.set(key, item)
                                gameIdSet.add(gameId)
                            } else {
                                //移除购物车
                                shoppingCart.delete(key)
                            }
                        };
                    } else {
                        // 让球的情况
                        // 让球主胜
                        if (buy == 3) {
                            item.hhadWinFlag = !item.hhadWinFlag;
                            if (item.hhadWinFlag) {
                                //加入购物车
                                shoppingCart.set(key, item)
                                gameIdSet.add(gameId);
                            } else {
                                //移除购物车
                                shoppingCart.delete(key);
                            }
                        };
                        // 不让球主胜平
                        if (buy == 1) {
                            item.hhadDrawFlag = !item.hhadDrawFlag;
                            if (item.hhadDrawFlag) {
                                //加入购物车
                                shoppingCart.set(key, item)
                                gameIdSet.add(gameId)
                            } else {
                                //移除购物车
                                shoppingCart.delete(key)
                            }
                        };
                        // 不让球主负
                        if (buy == 0) {
                            item.hhadLoseFlag = !item.hhadLoseFlag;
                            if (item.hhadLoseFlag) {
                                //加入购物车
                                shoppingCart.set(key, item)
                                gameIdSet.add(gameId)
                            } else {
                                //移除购物车
                                shoppingCart.delete(key)
                            }
                        };
                    }
                    if (!item.hadWinFlag && !item.hadDrawFlag && !item.hadLoseFlag && !item.hhadWinFlag && !item.hhadDrawFlag && !item.hhadLoseFlag) {
                        gameIdSet.delete(gameId)
                    }
                    this.data.accountData.matchNum = gameIdSet.size;
                    this.setData({
                        accordionWrap: that.data.accordionWrap,
                        accountData: that.data.accountData
                    })
                }
            };
        };

    },


    // 每次退出清空数据
    reset: function() {
        gameIdSet.clear();
        shoppingCart.clear()
    },

    // 清空购物车
    delChoose: function() {
        var that = this;
        wx.showModal({
            title: '清空已选',
            content: '是否确定已选',
            success: function(res) {
                if (res.confirm) {
                    var accordionData = that.data.accordionWrap.accordionData /*为了不写这么长定义的变量*/
                    shoppingCart.clear();
                    gameIdSet.clear();
                    for (var i = 0; i < accordionData.length; i++) {
                        for (var j = 0; j < accordionData[i].data.length; j++) {
                            if (accordionData[i].data[j].hadRangqiu) {
                                //存在未开售的情况

                                accordionData[i].data[j].hadWinFlag = false
                                accordionData[i].data[j].hadDrawFlag = false
                                accordionData[i].data[j].hadLoseFlag = false
                            }
                            if (accordionData[i].data[j].hhadRangqiu) {
                                accordionData[i].data[j].hhadWinFlag = false
                                accordionData[i].data[j].hhadDrawFlag = false
                                accordionData[i].data[j].hhadLoseFlag = false
                            }
                        }
                    }
                    that.data.accountData.matchNum = gameIdSet.size;

                    that.setData({
                        accordionWrap: that.data.accordionWrap,
                        accountData: that.data.accountData
                    })
                }
            }
        })
    },
    // // 下拉刷新
    // 下拉刷新
    refresh: function() {
        wx.showNavigationBarLoading() //在标题栏中显示加载
        this.fetchData()
    },

    // 提交确定
    enter: function() {
        if (gameIdSet.size < 2) {
            this.wetoast.toast({
                title: '请至少选择2场比赛'
            });
            return
        }
        app.globalData.raceShopCart = shoppingCart;
        app.globalData.raceIdSet = gameIdSet;
        wx.navigateTo({
            url: '../bet-foot/bet-foot?type=7'
        })
    }
})