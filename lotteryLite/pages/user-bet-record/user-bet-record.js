var app = getApp();
var betCurrentPage;
var pagesTotal;
Page({
    data: {
        activeIndex: 0,
        menus: [{
            'menuId': 1,
            'menu': '全部'
        }, {
            'menuId': 1,
            'menu': '待开奖'
        }, {
            'menuId': 1,
            'menu': '中奖'
        }, {
            'menuId': 1,
            'menu': '未成功'
        }],
        recordData: [],
        _height: null,
        dataFlag: false
    },

    onShow: function() {
        var that = this;
        var span = wx.getSystemInfoSync().windowWidth / this.data.menus.length + 'px';

        wx.getSystemInfo({
            success: function(res) {
                that.setData({
                    _height: res.windowHeight
                })
            }
        });
        // 页面显示
        this.setData({
            itemWidth: this.data.menus.length <= 5 ? span : '160rpx'
        });
    },
    onLoad: function() {
        new app.WeToast(); //weToatst

        betCurrentPage = 0;

        this.fetchData();

    },
    tabChange: function(e) {
        var index = e.currentTarget.dataset.index;
        this.setData({
            activeIndex: index
        });

        // 更换tab,将页码归置为0
        betCurrentPage = 0;
        this.data.recordData = [];
        this.fetchData()
    },
    // 获取数据
    fetchData: function() {
        var that = this;
        var recordType;
        // 0全部 1待开奖 2已开奖 3未开奖

        switch (that.data.activeIndex) {
            case 0:
                recordType = 'listRecordAll/'
                break;
            case 1:
                recordType = 'listRecordPending/'
                break;
            case 2:
                recordType = 'listRecordWin/'
                break;
            case 3:
                recordType = 'listRecordFail/'
                break;
        };


        if (pagesTotal) {
            if (pagesTotal <= betCurrentPage) {
                that.wetoast.toast({
                    title: '没有更多了',
                    titleClassName: 'check_info'
                });
                return
            }
        };
        betCurrentPage++;

        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });

        wx.request({
            url: app.globalData.requestUrl + '/user/' + recordType + '8/' + betCurrentPage,
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
            // header: {}, // 设置请求的 header
            success: function(res) {

                // that.data.recordData = res.data;
                // var recordData = that.data.recordData.list;

                wx.hideToast()

                if (res.statusCode == 200) {
                    /*记录下总页数*/
                    pagesTotal = res.data.pages;
                    /*/记录下总页数*/


                    if (res.data.list == 0) {
                        that.data.dataFlag = true
                    } else {
                        that.data.dataFlag = false
                    }

                    for (var i = 0; i < res.data.list.length; i++) {
                        // 判断图片
                        if (res.data.list[i].lotteryType) {
                            // 根据lotteryType判断显示的图片和类型
                            switch (res.data.list[i].lotteryType) {
                                // 大乐透
                                case 1:
                                    res.data.list[i].typeImg = '../images/brings.png';
                                    break;
                                    // 排列3
                                case 2:
                                    res.data.list[i].typeImg = '../images/arr-three.png';
                                    break;
                                    // 排列5
                                case 3:
                                    res.data.list[i].typeImg = '../images/arrange-five.png';
                                    break;
                                    // 七星彩
                                case 4:
                                    res.data.list[i].typeImg = '../images/seven-star.png';
                                    break;
                                    // 14场
                                case 5:
                                    res.data.list[i].typeImg = '../images/game-fourteen.png';
                                    break;
                                    // 任9
                                case 6:
                                    res.data.list[i].typeImg = '../images/choose-nine.png';
                                    break;
                                    // 竞彩
                                case 7:
                                    res.data.list[i].typeImg = '../images/smg-footerball.png';
                                    break;
                                    // 单关
                                case 8:
                                    res.data.list[i].typeImg = '../images/alone-footerball.png';
                                    break;
                            }
                        }
                        that.data.recordData.push(res.data.list[i])
                    };
                    // 隐藏加载条
                    wx.hideToast();
                    that.setData({
                        recordData: that.data.recordData,
                        dataFlag: that.data.dataFlag
                    })

                } else {
                    that.wetoast.toast({
                        title: '获取数据失败',
                        titleClassName: 'check_info'
                    });
                }
            },
            fail: function() {
                this.wetoast.toast({
                    title: '获取数据失败',
                    titleClassName: 'check_info'
                });
            }
        });
    },
    // 无记录跳转到首页
    navIndex() {
        wx.switchTab({
            url: '../index/index'
        })
    },
    // 跳转详情
    navUserDetail: function(e) {

        var betNo = e.currentTarget.dataset.betno;
        console.log(betNo)
            // wx.navigateTo({
            //     url: '../user-bet-detail/user-bet-detail?betNo=' + betNo
            // })
        wx.redirectTo({
            url: '../user-bet-detail/user-bet-detail?betNo=' + betNo
        })
    }
})