// 引入AllBills类
import {
    AllBills
} from 'admin-allBillsModel.js';
var allBills = new AllBills();
var app = getApp();
var pageNum;
var pageSize = 12;
var pageSum, dataList;
Page({
    // 页面初始化数据
    data: {
        dataFlag: false,
        isHideLoadMore: false,
        // listItem: [{
        //     betAmount: '20.0',
        //     betNo: 'c0cbd8bd48544994bf86529bd22228fa',
        //     betState: '出票不成功',
        //     betTime: '2017-11-03 17:04',
        //     lotteryType: '竞足单关',
        //     nicknamel: '19C',
        //     onoff: 'false',
        //     winAmount: '4.0'
        // }],
        listItem: [],
        gameInfList: {},
        gameInfListFN: {

        },
        expandFlag: null
    },
    // 页面初始化生命周期函数
    onLoad: function() {
        pageNum = 0;
        this._loadData();
        new app.WeToast(); //weToatst
    },
    // 点击展
    bindExpand: function(e) {
        var that = this;
        var index = e.currentTarget.dataset.idx;
        var listArr = this.data.listItem;
        var betNo = e.currentTarget.dataset.betno;
        var lotteryType = e.currentTarget.dataset.lotterytype;
        var betId = e.currentTarget.dataset.betid;
        for (var i = 0; i < listArr.length; i++) {

            if (listArr[i].betId == betId) {

                listArr[i].onoff = !listArr[i].onoff;
                continue
            }
            listArr[i].onoff = false;
        }
        this.setData({
            listItem: that.data.listItem
        })

        if (listArr[index].onoff) {
            wx.showToast({
                title: '加载中',
                icon: 'loading',
                duration: 5000
            });
            wx.request({
                url: app.globalData.requestUrl + '/admin/viewRecord/' + betNo,
                method: 'POST',
                header: {
                    'content-type': 'application/json',
                    "X-Authentication-Token": app.globalData.jwtToken
                },
                success: function(res) {
                    wx.hideToast();

                    if (res.statusCode == 200) {
                        // 竞彩和单关
                        if (lotteryType == 7 || lotteryType == 8) {

                            that.setData({
                                gameInfList: res.data,
                                expandFlag: true
                            });

                            for (var i = 0; i < that.data.gameInfList.detailList.length; i++) {
                                for (var j = 0; j < that.data.gameInfList.detailList[i].ratioList.length; j++) {
                                    if (that.data.gameInfList.detailList[i].ratioList[j].bet == '3') {
                                        that.data.gameInfList.detailList[i].ratioList[j].betView = '胜'
                                    }
                                    if (that.data.gameInfList.detailList[i].ratioList[j].bet == '1') {
                                        that.data.gameInfList.detailList[i].ratioList[j].betView = '平'
                                    }
                                    if (that.data.gameInfList.detailList[i].ratioList[j].bet == '0') {
                                        that.data.gameInfList.detailList[i].ratioList[j].betView = '负'
                                    }
                                }
                            };

                            that.setData({
                                gameInfList: that.data.gameInfList
                            })
                        };
                        if (lotteryType == 5 || lotteryType == 6) {


                            // 截取队名前3个
                            for (var i = 0; i < res.data.detailList.length; i++) {
                                res.data.detailList[i].hostTeam = res.data.detailList[i].hostTeam.substring(0, 2)
                            }

                            that.setData({
                                gameInfListFN: res.data,
                                expandFlag: false
                            });
                        }
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
    },
    // 自定义函数---获取数据
    _loadData: function() {
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
        // 加载提示
        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });

        // 调用类里面的方法获取数据，回调函数处理异步请求
        allBills.getAllBillsData(pageNum, pageSize, (res) => {
            var that = this;

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
                for (var i = 0; i < dataList.length; i++) {
                    dataList[i].onoff = false;
                    that.data.listItem.push(dataList[i]);
                }
                // 隐藏加载条
                wx.hideToast();
                that.setData({
                    listItem: that.data.listItem
                })
            } else {
                wx.hideToast()
                that.wetoast.toast({
                    title: '获取数据失败',
                });
            }

        });
    },
    // 上拉加载
    onReachBottom: function() {
        this._loadData();

    },
    onPullDownRefresh: function() {
        wx.showNavigationBarLoading() //在标题栏中显示加载
        this.setData({
            listItem: []
        })
        pageNum = 0;
        this._loadData()
    }
})