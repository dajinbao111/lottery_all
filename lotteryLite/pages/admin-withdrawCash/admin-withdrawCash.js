// pages/admin-noPay/admin-noPay.js
var app = getApp();
var pageNum = 0;
var pageSize = 6;
var pageSum, dataList
Page({
    data: {
        dataFlag: false,
        listItem: [],
        isHideLoadMore: false,
    },
    onLoad: function() {},
    onShow: function() {

        this.setData({
            listItem: []
        })
        new app.WeToast(); //weToatst
        pageNum = 0;
        this.getData()
    },
    // 上拉加载
    onReachBottom: function() {
        this.getData();
    },
    // 获取数据
    getData: function() {
        var that = this;
        // 判断还有没有数据 
        if (pageSum) {
            if (pageSum <= pageNum) {
                that.wetoast.toast({
                    title: '没有更多了',
                    titleClassName: 'check_info'
                });
                that.setData({
                    isHideLoadMore: true
                })
                return
            }
        }
        pageNum++;

        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        wx.request({
            url: app.globalData.requestUrl + '/admin/listWithdraw/' + pageSize + '/' + pageNum,
            method: 'POST',
            header: {
                'content-type': 'application/json',
                "X-Authentication-Token": app.globalData.jwtToken
            },
            success: function(res) {
                wx.hideToast();
                if (res.statusCode == 200) {
                    wx.hideNavigationBarLoading() //完成停止加载
                    wx.stopPullDownRefresh() //停止下拉刷新
                    pageSum = res.data.pages;
                    pageNum = res.data.pageNum;
                    dataList = res.data.list;
                    if (dataList.length == 0) {
                        that.data.dataFlag = true
                    } else {
                        that.data.dataFlag = false
                    };
                    that.setData({
                        dataFlag: that.data.dataFlag
                    })

                    if (dataList.length > 0) {
                        for (var i = 0; i < dataList.length; i++) {
                            dataList[i].changeAmount = Math.abs(dataList[i].changeAmount);
                            dataList[i].bankCardDetail = dataList[i].bankCard.replace(/(\d{4})(?=\d)/g, "$1 ");
                            that.data.listItem.push(dataList[i]);
                        }
                    }


                    that.setData({
                        listItem: that.data.listItem
                    })
                } else {
                    that.wetoast.toast({
                        title: '网络请求未知错误',
                        titleClassName: 'check_info'
                    });
                }
            },
            fail: function() {
                wx.hideToast();
                that.wetoast.toast({
                    title: '网络请求未知错误',
                    titleClassName: 'check_info'
                });
            }
        })
    },
    detail: function(e) {
        var recordId = e.target.dataset.recordid;
        var that = this;
        wx.showModal({
            title: '确认处理',
            content: '是否确认处理',
            success: function(res) {
                if (res.confirm) {
                    wx.showToast({
                        title: '加载中',
                        icon: 'loading',
                        duration: 5000
                    });
                    wx.request({
                        url: app.globalData.requestUrl + '/admin/handleWithdraw/' + recordId,
                        method: 'POST',
                        header: {
                            'content-type': 'application/json',
                            "X-Authentication-Token": app.globalData.jwtToken
                        },
                        success: function(res) {
                            wx.hideToast();

                            if (res.statusCode == 200) {
                                wx.navigateTo({
                                    url: "../admin-withdrawCash/admin-withdrawCash"
                                });
                            } else {
                                that.wetoast.toast({
                                    title: '网络请求未知错误',
                                    titleClassName: 'check_info'
                                });
                            }
                        },
                        fail: function() {
                            wx.hideToast();
                            that.wetoast.toast({
                                title: '网络请求未知错误',
                                titleClassName: 'check_info'
                            });
                        }
                    })
                }
            }
        })
    },
    onPullDownRefresh: function() {
        this.setData({
            listItem: []
        })
        pageNum = 0;
        this.getData()
    },

    copyName: function(e) {
        var that = this

        wx.setClipboardData({
            data: e.target.dataset.bankaccount,
            success: function(res) {
                wx.getClipboardData({
                    success: function(res) {
                        that.wetoast.toast({
                            title: '复制成功',
                            titleClassName: 'check_info'
                        });
                    }
                })
            }
        })
    },
    copyNum: function(e) {
        var that = this
        wx.setClipboardData({
            data: e.target.dataset.bankcard,
            success: function(res) {
                wx.getClipboardData({
                    success: function(res) {
                        that.wetoast.toast({
                            title: '复制成功',
                            titleClassName: 'check_info'
                        });
                    }
                })
            }
        })
    },
    navMine: function() {

        wx.switchTab({
            url: "../user-mine/user-mine"
        });
    }
})