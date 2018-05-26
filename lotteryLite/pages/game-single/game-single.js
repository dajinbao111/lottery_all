var app = getApp();
var shoppingCart = new Map() //购物车
var gameIdSet = new Set() //用来统计选择的场次数

Page({
    data: {
        // 比赛数据
        accordionWrap: {
            accordionData: []
        },
        accountData: {
            footballFlag: true,
            typeFlag: false
        },
        viewFlag: true,
        deadLine: '' /*截止时间*/
    },
    onLoad: function(options) {
        new app.WeToast(); //weToatst
        this.reset()
        this.getDatas()
    },
    onReady: function() {
        // 页面渲染完成
    },
    onShow: function() {
        // this.getDatas();
    },

    onHide: function() {
        // 页面隐藏
    },
    onUnload: function() {
        // 页面关闭
    },
    // 标题折叠/展开函数
    tagtap: function(e) {
        var that = this;
        var index = e.currentTarget.dataset.idx;
        var temArr = this.data.accordionWrap.accordionData;
        temArr[index].checkFlag = !temArr[index].checkFlag;
        this.setData({
            accordionWrap: that.data.accordionWrap
        })
    },
    // 获取数据 
    getDatas: function(e) {
        var that = this;
        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        wx.request({
            url: app.globalData.requestUrl + '/game/getGameSingleInfo',
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {
                wx.hideToast();


                if (res.statusCode == 200) {
                    if (app.isEmptyObject(res.data)) {
                        that.setData({
                            viewFlag: false
                        })
                        return
                    }
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
                        item.guestTeam = res.data[weekday].guestTeam;
                        item.flag3 = false //胜标识
                        item.flag1 = false //平标识
                        item.flag0 = false //负标识
                        item.rangqiu = res.data[weekday].rangqiu;
                        item.winRatio = res.data[weekday].winRatio;
                        item.drawRatio = res.data[weekday].drawRatio;
                        item.loseRatio = res.data[weekday].loseRatio;
                        item.lastUpdated = res.data[weekday].lastUpdated

                        var title = res.data[weekday].weekday.substring(0, 2) + " " + res.data[weekday].gameDate
                        if (weekTitle == "") {
                            deadLine = item.lastUpdated


                            weekTitle = title
                            tempGroup.push(item);
                            that.setData({
                                deadLine: deadLine
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
                    });

                }
            }
        })
    },
    /*选择点击事件*/
    chooseAwards: function(e) {
        var that = this;
        var gameId = e.currentTarget.dataset.gameid;
        var ratio = e.currentTarget.dataset.ratio; /* 赔率(可能没有)*/
        var buy = e.currentTarget.dataset.buy; //表示选择的胜平负 
        var rangqiu = e.currentTarget.dataset.rangqiu;

        var accordionData = that.data.accordionWrap.accordionData; /*为了不写这么长定义的变量*/
        for (var i = 0; i < accordionData.length; i++) {
            for (var j = 0; j < accordionData[i].data.length; j++) {
                var item = accordionData[i].data[j];
                if (item.gameId == gameId) {
                    var key;
                    if (rangqiu == 0) {
                        key = gameId + '-' + '0' + '-' + buy + '-' + ratio;
                    } else {
                        key = gameId + '-' + '1' + '-' + buy + '-' + ratio;
                    }

                    // 不让球的3种情况
                    if (buy == 3) {
                        if (!item.flag3) { //
                            //加入购物车
                            if (!gameIdSet.has(gameId) && gameIdSet.size == 1) {
                                this.wetoast.toast({
                                    title: '只能选择1场比赛'
                                });
                                return
                            }
                            gameIdSet.add(gameId)
                            shoppingCart.set(key, item)
                        } else {
                            //移除购物车
                            shoppingCart.delete(key)
                        }
                        item.flag3 = !item.flag3
                    }
                    if (buy == 1) {

                        if (!item.flag1) {
                            if (!gameIdSet.has(gameId) && gameIdSet.size == 1) {
                                this.wetoast.toast({
                                    title: '只能选择1场比赛'
                                });
                                return
                            }
                            //加入购物车
                            shoppingCart.set(key, item)
                            gameIdSet.add(gameId)
                        } else {
                            //移除购物车
                            shoppingCart.delete(key)
                        }
                        item.flag1 = !item.flag1
                    }
                    if (buy == 0) {
                        if (!item.flag0) {
                            if (!gameIdSet.has(gameId) && gameIdSet.size == 1) {
                                this.wetoast.toast({
                                    title: '只能选择1场比赛'
                                });
                                return
                            }
                            //加入购物车
                            shoppingCart.set(key, item)
                            gameIdSet.add(gameId)
                        } else {
                            //移除购物车
                            shoppingCart.delete(key)
                        }
                        item.flag0 = !item.flag0
                    }
                    if (!item.flag3 && !item.flag1 && !item.flag0) {
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
                            accordionData[i].data[j].flag3 = false
                            accordionData[i].data[j].flag1 = false
                            accordionData[i].data[j].flag0 = false
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
    // 选项
    bindPickerChange: function(e) {
        this.setData({
            index: e.detail.value
        })
    },

    // 提交确定
    enter: function() {
        if (gameIdSet.size != 1) {
            this.wetoast.toast({
                title: '请选择一场比赛'
            });
            return
        }

        app.globalData.singleShopCart = shoppingCart;
        app.globalData.singleGameIdSet = gameIdSet;
        wx.navigateTo({
            url: '../bet-nine/bet-nine?type=8'
        })
    },
    // 下拉刷新
    refresh: function() {
        this.getDatas()
    },
})