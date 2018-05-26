const app = getApp();
Page({
    data: {
        detail: {},
        betSrc: 'https://www.wisestar.org/jenkins/static/7038df32/images/headshot.png',
        imgFlag: false
    },
    onLoad: function(options) {
        // 页面初始化 options为页面跳转所带来的参数
        new app.WeToast(); //weToatst

        this.setData({
            betNo: options.betNo
        });
        this.fetchData()
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
    fetchData: function() {
        var that = this;
        var betNo = this.data.betNo
        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        wx.request({
            url: app.globalData.requestUrl + '/user/viewRecord/' + betNo,
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {
                console.log(res)
                wx.hideToast();

                if (res.statusCode == 200) {
                    console.log(res)
                    switch (res.data.lotteryType) {
                        // 大乐透
                        case 1:
                            res.data.typeImg = '../images/brings.png';
                            res.data.viewType = '大乐透';
                            break;
                            // 排列3
                        case 2:
                            res.data.typeImg = '../images/arr-three.png';
                            res.data.viewType = '排列3';
                            break;
                            // 排列5
                        case 3:
                            res.data.typeImg = '../images/arrange-five.png';
                            res.data.viewType = '排列5';
                            break;
                            // 七星彩
                        case 4:
                            res.data.typeImg = '../images/seven-star.png';
                            res.data.viewType = '七星彩';
                            break;
                            // 14场
                        case 5:
                            res.data.typeImg = '../images/game-fourteen.png';
                            res.data.viewType = '14场';
                            break;
                            // 任9
                        case 6:
                            res.data.typeImg = '../images/choose-nine.png';
                            res.data.viewType = '任选9';
                            break;
                            // 竞彩
                        case 7:
                            res.data.typeImg = '../images/smg-footerball.png';
                            res.data.viewType = '竞彩足球';
                            break;
                            // 单关
                        case 8:
                            res.data.typeImg = '../images/alone-footerball.png';
                            res.data.viewType = '竞彩单关';
                            break;
                    }
                    if (res.data.lotteryType == 5 || res.data.lotteryType == 6) {
                        for (var i = 0; i < res.data.detailList.length; i++) {
                            var betArr = [];
                            betArr.push(res.data.detailList[i].bet.split(""))
                            res.data.detailList[i].betArr = betArr;
                        };
                    }
                    if (res.data.lotteryType == 7 || res.data.lotteryType == 8) {
                        for (var i = 0; i < res.data.detailList.length; i++) {
                            for (var j = 0; j < res.data.detailList[i].ratioList.length; j++) {
                                var result = ''
                                if (res.data.detailList[i].ratioList[j].rangqiu == 0 && res.data.detailList[i].hadResult != null) {

                                    if (res.data.detailList[i].ratioList[j].bet == res.data.detailList[i].hadResult) {
                                        result = '胜'
                                    } else {
                                        result = '负'
                                    }
                                }
                                if (res.data.detailList[i].ratioList[j].rangqiu == 0 && res.data.detailList[i].hadResult == null) {
                                    result = null
                                }
                                if (res.data.detailList[i].ratioList[j].rangqiu != 0 && res.data.detailList[i].hhadResult != null) {
                                    if (res.data.detailList[i].ratioList[j].bet == res.data.detailList[i].hhadResult) {
                                        result = '胜'
                                    } else {
                                        result = '负'
                                    }
                                }
                                if (res.data.detailList[i].ratioList[j].rangqiu != 0 && res.data.detailList[i].hhadResult == null) {
                                    result = null
                                }

                                res.data.detailList[i].ratioList[j].result = result
                            }
                        };
                    }
                    that.setData({
                        detail: res.data
                    });

                    console.log(that.data.detail)
                }
            }
        })
    },
    navIndex: function() {
        console.log('111')
        wx.switchTab({
            url: "../index/index"
        });
    },
    imageSrc: function(e) {
        var that = this;
        var betNo = e.currentTarget.dataset.src;

        this.setData({
            imgFlag: true,
            betSrc: app.globalData.requestUrl + '/auth/ticket/' + betNo
        })
    },
    imgHide: function() {
        this.setData({
            imgFlag: false
        })
    }
})