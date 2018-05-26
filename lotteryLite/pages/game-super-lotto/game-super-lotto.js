Page({
    data: {
        redBall: [],
        accountData: {
            footballFlag: false
        }
    },
    onLoad: function(options) {
        // 页面初始化 options为页面跳转所带来的参数
    },
    onReady: function() {
        // 页面渲染完成
    },
    onShow: function() {
        // 页面显示
        // 制造红球
        this.makeBall(35)
        this.makeBall(12)
    },
    onHide: function() {
        // 页面隐藏
    },
    onUnload: function() {
        // 页面关闭
    },
    // 制造球球
    makeBall: function(num) {
        var that = this;
        var ball = [];

        for (var i = 1; i <= num; i++) {
            if (String(i).length == 1) {
                i = '0' + i;
            }
            ball.push(i)
        }
        if (num == 35) {
            that.setData({
                redBall: ball
            })
        } else {
            that.setData({
                blueBall: ball
            })
        }


    }
})