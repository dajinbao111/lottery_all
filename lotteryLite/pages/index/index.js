import {
    WeToastClass
} from "../../wetoast/wetoast.js";
const app = getApp();
Page({
    data: {
        sliderData: {
            showLoading: true,
            indicatorDots: false,
            autoplay: true,
            interval: 3000,
            duration: 1000,
            swiperCurrent: 0,
            sendStartFlag: true,
            receiveFlag: true,
            slider: [{
                picUrl: '../images/index-swiper01.png'
            }, {
                picUrl: '../images/index-swiper01.png'
            }, {
                picUrl: '../images/index-swiper01.png'
            }]
        },
        // view层
        viewData: [{
            leftUrl: '../game-passBarrie/game-passBarrie',
            leftImg: '../images/smg-footerball.png',
            leftTit: '竞彩足球',
            leftDetails: '阿尔巴尼vs意大利',
            rightUrl: '../index/index',
            rightImg: '../images/brings.png',
            rightTit: '大乐透',
            rightDetails: '奖池超42亿',
            flag: false
        }, {
            leftUrl: '../game-single/game-single',
            leftImg: '../images/alone-footerball.png',
            leftTit: '竞足单关',
            leftDetails: '威尔士vs爱尔兰',
            rightUrl: '../index/index',
            rightImg: '../images/seven-star.png',
            rightTit: '七星彩',
            rightDetails: '玩篮球，猜胜负',
            flag: false
        }, {
            leftUrl: '../game-fourteen/game-fourteen',
            leftImg: '../images/game-fourteen.png',
            leftTit: '十四场',
            leftDetails: '2元可博500万',
            rightUrl: '../game-arr-3/game-arr-3',
            rightImg: '../images/arr-three.png',
            rightTit: '排列三',
            rightDetails: '猜3位数赢千元',
            flag: false
        }, {
            leftUrl: '../game-nine/game-nine',
            leftImg: '../images/choose-nine.png',
            leftTit: '任选九',
            leftDetails: '稳健14挑9场',
            rightUrl: '../game-arr-5/game-arr-5',
            rightImg: '../images/arrange-five.png',
            rightTit: '排列五',
            rightDetails: '猜5位数中10万',
            flag: false
        }, {
            leftUrl: '../follow/follow',
            leftImg: '../images/copy_bills.png',
            leftTit: '我要跟单',
            // leftDetails: '',          
        }]
    },
    // 轮播
    swiperChange: function(e) {
        this.data.sliderData.swiperCurrent = e.detail.current;
        this.setData({
            sliderData: this.data.sliderData
        })
    },
    // /轮播
    onLoad: function() {
        new app.WeToast(); //weToatst
        // this.setData({
        //     msgList: [{
        //         title: "new**喜中",
        //         type: '大乐透',
        //         money: '5000元'
        //     }, {
        //         title: "new**喜中",
        //         type: '大乐透',
        //         money: '5000元'
        //     }, {
        //         title: "new**喜中",
        //         type: '大乐透',
        //         money: '5000元'
        //     }]
        // });
        this.getUserInfo();
        this.announcement()
    },
    // 跳转导航
    navUrl: function(e) {
        var onoff = e.currentTarget.dataset.onoff;
        if (onoff === false) {
            wx.showModal({
                content: '很抱歉，该彩种暂未开放销售，敬请期待！',
                showCancel: false,
                confirmText: '知道了',
                confirmColor: "#f6380c"
            });
        } else {
            var url = e.currentTarget.dataset.url;
            wx.navigateTo({
                url: url
            })
        }
    },

    // 获取用户信息
    getUserInfo: function() {
        var that = this;
        wx.login({
            success: function(res) {
                var rec = res.code;

                if (res.code) {
                    //发起网络请求

                    wx.showToast({
                        title: '加载中',
                        icon: 'loading',
                        duration: 5000
                    });
                    wx.request({
                        url: app.globalData.requestUrl + '/auth/login',
                        header: {
                            "Content-Type": "application/json"
                        },
                        data: rec,
                        method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT    
                        // header: {}, // 设置请求的 header    
                        success: function(res) {
                            wx.hideToast();

                            wx.setStorage({
                                key: "userIdToken",
                                data: res.data
                            });
                            app.globalData.openId = res.data.openId;
                            app.globalData.jwtToken = res.data.jwtToken;
                            app.globalData.authorities = res.data.authorities
                        }
                    });
                } else {
                    that.wetoast.toast({
                        title: '获取用户信息失败',
                        titleClassName: 'check_info'
                    });
                }
            },
            fail: function() {
                that.wetoast.toast({
                    title: '获取用户信息失败',
                    titleClassName: 'check_info'
                });
            }
        });
    },
    announcement: function() {
        var that = this;
        wx.request({
            url: app.globalData.requestUrl + '/auth/notice',
            header: {
                "Content-Type": "application/json"
            },
            method: 'GET',
            success: function(res) {
                // res.data为后台返回给我 的id


                wx.hideToast();
                if (res.statusCode == 200) {
                    that.setData({
                        msgList: res.data
                    })
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