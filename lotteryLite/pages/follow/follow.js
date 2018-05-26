var app = getApp();

Page({
    data: {
        activeIndex: 0,
        menus: [{
            'menuId': 1,
            'menu': '竞彩足球'
        }, {
            'menuId': 2,
            'menu': '十四场'
        }, {
            'menuId': 3,
            'menu': '任选九'
        }],
        /*竞彩的数据*/
        followBill: [],
        // 14的数据
        listDataFourteen: []
    },

    // 点击折叠、伸展
    tagtap: function(e) {
        var that = this;
        var index = e.currentTarget.dataset.idx;
        var type = e.currentTarget.dataset.type;
        var temArr;
        temArr = this.data.followBill;
        temArr[index].onoff = !temArr[index].onoff;
        console.log(index)
        this.setData({
            followBill: that.data.followBill
        })

    },
    onShow: function() {
        var that = this;
        var span = wx.getSystemInfoSync().windowWidth / this.data.menus.length + 'px';

        // 页面显示
        this.setData({
            itemWidth: this.data.menus.length <= 5 ? span : '160rpx'
        });
    },
    onLoad: function(options) {

        if (options.type == 5) {
            // 14
            this.setData({
                activeIndex: 1
            })
        }
        if (options.type == 6) {
            // 9
            this.setData({
                activeIndex: 2
            })
        }
        new app.WeToast(); //weToatst
        this.fetchData()
    },
    onHide: function() {
        // 页面隐藏
    },
    onUnload: function() {
        // 页面关闭
    },
    // 点击tab
    tabChange: function(e) {
        var index = e.currentTarget.dataset.index;
        console.log(index)
        this.setData({
            activeIndex: index
        });
        this.fetchData()
            // 更换tab,将页码归置为0
        this.data.recordData = [];
    },
    // /点击tab
    /*获取数据*/
    fetchData: function() {
        var that = this;
        var nowTime = new Date().getTime();
        var url;
        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        if (that.data.activeIndex == 0) {
            url = '/follow/football'
        }
        if (that.data.activeIndex == 1) {
            url = '/follow/fourteen'
        } else if (that.data.activeIndex == 2) {
            url = '/follow/nine'
        }

        wx.request({
            url: app.globalData.requestUrl + url,
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {
                wx.hideToast();
                var followBill = [];
                if (res.statusCode == 200) {
                    if (that.data.activeIndex == 0) {
                        console.log(res)
                        for (var i = 0; i < res.data.length; i++) {
                            var item = res.data[i];
                            var gameInfo = [];
                            item.bigTitle = '方案' + (i + 1);
                            item.deadlineTime = res.data[i].dutTime.substring(0, 16);
                            if (nowTime > Date.parse(item.deadlineTime)) {
                                item.timeFlag = false
                            } else {
                                item.timeFlag = true
                            }
                            item.onoff = true
                            for (var j = 0; j < res.data[i].detailList.length; j++) {
                                gameInfo.push(res.data[i].detailList[j]);
                                for (var k = 0; k < gameInfo[j].ratioList.length; k++) {
                                    if (gameInfo[j].ratioList[k].bet == 3) {
                                        gameInfo[j].ratioList[k].betView = '胜'
                                    }
                                    if (gameInfo[j].ratioList[k].bet == 1) {
                                        gameInfo[j].ratioList[k].betView = '平'
                                    }
                                    if (gameInfo[j].ratioList[k].bet == 0) {
                                        gameInfo[j].ratioList[k].betView = '负'
                                    }
                                    if (gameInfo[j].ratioList[k].rangqiu != 0) {
                                        gameInfo[j].rangqiuFlag = true
                                    }
                                }
                            }
                            item.gameInfo = gameInfo;
                            followBill.push(item)
                        }
                        that.setData({
                            followBill: followBill
                        })
                    } else {
                        console.log(res);
                        for (var i = 0; i < res.data.length; i++) {
                            var gameInfo = [];
                            res.data[i].onoff = true;
                            res.data[i].bigTitle = '方案' + (i + 1);
                            res.data[i].deadlineTime = res.data[i].dutTime.substring(0, 16);
                            if (nowTime > Date.parse(res.data[i].deadlineTime)) {
                                res.data[i].timeFlag = false
                            } else {
                                res.data[i].timeFlag = true
                            }
                        }
                        that.setData({
                            followBill: res.data
                        })
                    }
                } else {
                    that.wetoast.toast({
                        title: '获取数据失败'
                    });
                }
            },
            fail: function() {
                wx.hideToast();

                that.wetoast.toast({
                    title: '获取数据失败'
                });
            }
        })
    },
    // +-倍数
    minus: function(e) {
        var _index = e.currentTarget.dataset.index;
        var count;
        count = --this.data.followBill[_index].times;
        var _timeFlag = e.currentTarget.dataset.timeflag; /*看时间过期没有*/

        if (!_timeFlag) {
            return
        }
        if (count <= 0) {
            this.wetoast.toast({
                title: '倍数必须大于0'
            });
            this.data.followBill[_index].times = 1

            this.setData({
                followBill: this.data.followBill
            });
            return
        }
        this.setData({
            followBill: this.data.followBill
        });
        this.count(_index)
    },
    add: function(e) {

        var _index = e.currentTarget.dataset.index;
        var _timeFlag = e.currentTarget.dataset.timeflag; /*看时间过期没有*/
        var _bettingNum = e.currentTarget.dataset.amount; /*总金额*/

        if (!_timeFlag) {
            return
        }
        if (this.data.followBill[_index].times >= app.globalData.maxMultiple) {
            this.wetoast.toast({
                title: '理性投注,量力而行'
            });
            return
        } else {
            this.data.followBill[_index].times++
                this.setData({
                    followBill: this.data.followBill
                });
            // this.countPiece()

            this.count(_index)
        }
    },
    count: function(_index) {
        this.data.followBill[_index].betAmount = this.data.followBill[_index].piece * this.data.followBill[_index].times * 2;
        this.setData({
            followBill: this.data.followBill
        });
    },
    // 输入倍数
    bindMultiple: function(e) {
        var _idx = e.currentTarget.dataset.index;
        if (e.detail.value.length == 1) {
            e.detail.value = e.detail.value.replace(/[^1-9]/g, '')
        } else {
            e.detail.value = e.detail.value.replace(/\D/g, '')
        }
        if (e.detail.value >= app.globalData.maxMultiple) {
            this.wetoast.toast({
                title: '理性投注,量力而行'
            });
            return
        }
        this.data.followBill[_idx].times = e.detail.value;
        this.setData({
            followBill: this.data.followBill
        });

        this.count(_idx)
    },
    betEnter: function(e) {
        var _index = e.currentTarget.dataset.index;
        var that = this;
        if (this.data.followBill[_index].times == '') {
            that.wetoast.toast({
                title: '请输入倍数'
            });
            return
        };

        if (that.data.activeIndex == 0) {
            // 竞彩的
            var betDetail = '';
            for (var i = 0; i < that.data.followBill[_index].gameInfo.length; i++) {
                for (var j = 0; j < that.data.followBill[_index].gameInfo[i].ratioList.length; j++) {

                    betDetail += that.data.followBill[_index].gameInfo[i].gameId + '-' + that.data.followBill[_index].gameInfo[i].ratioList[j].rangqiu + '-' + that.data.followBill[_index].gameInfo[i].ratioList[j].bet + '-' + that.data.followBill[_index].gameInfo[i].ratioList[j].ratio + ','
                        // betDetailArr.push(str)
                }
            }
            betDetail = betDetail.substring(0, betDetail.length - 1);
        } else {
            // 14的
            var betDetail = that.data.followBill[_index].phaseId + '-';
            console.log(betDetail);
            for (var i = 0; i < that.data.followBill[_index].detailList.length; i++) {
                betDetail += that.data.followBill[_index].detailList[i].bet + ','
            }
            betDetail = betDetail.substring(0, betDetail.length - 1);
            console.log(betDetail)
        }
        // 传送给后台
        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        wx.request({
            url: app.globalData.requestUrl + '/bet/confirmBet',
            data: {
                lotteryType: that.data.followBill[_index].lotteryType,
                betAmount: that.data.followBill[_index].betAmount,
                /*下注金额*/
                betTimes: that.data.followBill[_index].times,
                /*下注倍数*/
                betPiece: that.data.followBill[_index].piece,
                /*多少注*/
                passType: that.data.followBill[_index].passType,
                /*过关方式*/
                betDetail: betDetail
            },
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {
                // res.data为后台返回给我 的id
                wx.hideToast();
                if (res.statusCode == 200) {
                    wx.navigateTo({
                        url: "../payment/payment?id=" + res.data
                    });
                } else {
                    that.wetoast.toast({
                        title: '网络错误'
                    });
                }
            },
            fail: function() {
                wx.hideToast();
                that.wetoast.toast({
                    title: '网络错误'
                });
            }
        });
    }
})