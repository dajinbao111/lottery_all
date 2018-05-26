const app = getApp();
Page({
    data: {
        navId: null,
        /*接受到的id*/
        viewData: {}
    },
    onLoad: function(options) {
        // 页面初始化 options为页面跳转所带来的参数;

        new app.WeToast(); //weToatst

        this.data.navId = options.id;
        this.setData({
            navId: this.data.navId
        })
        if (this.data.navId) {
            this.fetchData()
        }
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

        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        wx.request({
            url: app.globalData.requestUrl + '/bet/confirmPay',
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            data: that.data.navId,
            method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT    
            // header: {}, // 设置请求的 header    
            success: function(res) {
                wx.hideToast()


                if (res.statusCode == 200) {
                    that.data.viewData = res.data;
                    that.setData({
                        viewData: that.data.viewData
                    });

                } else {
                    that.wetoast.toast({
                        title: '获取用户信息失败',
                        titleClassName: 'check_info'
                    });
                }
            }
        });
    },
    payEnter: function() {
        console.log('1111')
        var that = this;
        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        wx.request({
            url: app.globalData.requestUrl + '/bet/pay',
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            data: that.data.navId,
            method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT    
            // header: {}, // 设置请求的 header    
            success: function(res) {
                console.log(res)
                wx.hideToast()
                console.log(res.statusCode)
                if (res.statusCode == 200) {
                    wx.redirectTo({
                        url: "../payment-successful/payment-successful?paysum=" + res.data
                    });
                } else {
                    that.wetoast.toast({
                        title: '操作失败',
                        titleClassName: 'check_info'
                    });
                }
            },
            fail: function() {
                that.wetoast.toast({
                    title: '操作失败',
                    titleClassName: 'check_info'
                });
            }
        });
    }
})