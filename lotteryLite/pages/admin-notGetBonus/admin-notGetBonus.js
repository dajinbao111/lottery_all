var app = getApp();
var pageNum;
var pageSize = 12;
var pageSum, dataList;
var count = 2
Page({
    // 页面初始化数据
    data: {
        expandFlag: true,
        imgSrc: null,
        dataList: []
    },
    // 页面初始化生命周期函数
    onLoad: function() {
        new app.WeToast();
        // this.fetchData()
    },

    fetchData: function() {
        var that = this;

        if (that.data.dataList.length != 0) {
            var sum = 0;
            for (var i = 0; i < that.data.dataList.length; i++) {
                if (that.data.dataList[i].disabledFlag) {
                    sum++
                }
            };


            if (sum < that.data.dataList.length) {
                that.wetoast.toast({
                    title: '数据未处理完'
                })
                return
            }
        }
        wx.request({
            url: app.globalData.requestUrl + "/admin/listRecordWin/" + count,
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {
                if (res.statusCode === 200) {
                    if (res.data.length == 0) {
                        that.wetoast.toast({
                            title: '暂无数据'
                        });
                        return
                    }

                    for (var i = 0; i < res.data.length; i++) {
                        res.data[i].disabledFlag = false;
                        // 竞彩和单关
                        if (res.data[i].lotteryType == 7 || res.data[i].lotteryType == 8) {
                            for (var j = 0; j < res.data[i].detailList.length; j++) {
                                for (var k = 0; k < res.data[i].detailList[j].ratioList.length; k++) {
                                    if (res.data[i].detailList[j].ratioList[k].rangqiu != 0) {
                                        res.data[i].detailList[j].rangqiuFlag == true
                                    }
                                }
                            };
                        } else if (res.data[i].lotteryType == 5 || res.data[i].lotteryType == 6) {
                            for (var j = 0; j < res.data[i].detailList.length; j++) {
                                var betArr = [];
                                betArr.push(res.data[i].detailList[j].bet.split(""))
                                res.data[i].detailList[j].betArr = betArr;
                            }
                        }
                    };

                    that.setData({
                        dataList: res.data
                    })

                } else {
                    that.wetoast.toast({
                        title: '数据加载失败'
                    })
                };
            },
            fail: function(res) {},
            complete: function(res) {
                wx.hideToast();
            },
        })
    },
    winAmontFn: function(e) {
        var value = e.detail.value;
        var index = e.currentTarget.dataset.index;
        this.data.dataList[index].winAmount = value
        this.setData({
            dataList: this.data.dataList
        })

    },
    enter: function(e) {
        var that = this;
        var betNo = e.currentTarget.dataset.betno;
        var index = e.currentTarget.dataset.index;
        wx.request({
            url: app.globalData.requestUrl + "/admin/handleCash/" + betNo,
            data: that.data.dataList[index].winAmount,
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {

                if (res.statusCode === 200) {
                    that.wetoast.toast({
                        title: '操作成功'
                    });
                    that.data.dataList[index].disabledFlag = true
                    that.setData({
                        dataList: that.data.dataList
                    })
                } else {
                    that.wetoast.toast({
                        title: '操作失败'
                    })
                };
            },
            fail: function(res) {
                that.wetoast.toast({
                    title: '操作失败'
                })
            },
            complete: function(res) {
                wx.hideToast();
            },
        })
    }
})