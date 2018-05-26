var app = getApp();
Page({
    /**
     * 页面的初始数据
     */
    data: {
        mineInfo: {
            avatarUrl: '../images/mine-icon/no-avatar.png',
            nickName: '注册/登录'
        },
        mineAccount: {
            accountIcon: '../images/mine-icon/howMoney.png',
            howMoney: '--',
            rechargeIcon: '../images/mine-icon/recharge.png',
            cashIcon: '../images/mine-icon/cash.png'
        },
        adminData: [{
            toUrl: '../admin-noTicket/admin-noTicket',
            btnText: '待出票',
            adminFlag: false,
            num: 0
        }, {
            toUrl: '../admin-notGetBonus/admin-notGetBonus',
            btnText: '待兑奖',
            adminFlag: false,
            num: 0
        }, {
            toUrl: '../admin-withdrawCash/admin-withdrawCash',
            btnText: '待提现',
            adminFlag: false,
            num: 0
        }],
        privilegesFlag: false
    },
    navToLogin: function() {
        wx.navigateTo({
            url: '../user-login/user-login',
        })
    },
    // 跳转页面
    goDetail: function() {
        wx.navigateTo({
            url: '../user-account-details/user-account-details',
        })
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function(options) {},

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function() {

    },
    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function() {
        var that = this;
        this.fetchData()
        this.jurisdiction()
    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function() {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function() {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function() {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function() {

    },
    fetchData: function() {
        var that = this;
        if (app.globalData.openId) {
            wx.request({
                url: app.globalData.requestUrl + '/user/getInfo',
                header: {
                    "Content-Type": "application/json",
                    "X-Authentication-Token": app.globalData.jwtToken
                },
                method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT       
                success: function(res) {

                    wx.hideNavigationBarLoading() //完成停止加载
                    wx.stopPullDownRefresh() //停止下拉刷新


                    // /tjwtToken过期

                    // 根据是否有手机号判断是否跳转登录页面
                    if (res.data.phoneNum == null) {
                        wx.navigateTo({
                            url: '../user-login/user-login'
                        })
                    } else {

                        that.data.mineInfo.avatarUrl = res.data.avatarUrl;
                        that.data.mineInfo.nickName = res.data.nickName;
                        that.data.mineAccount.howMoney = res.data.balance;
                        that.setData({
                            mineInfo: that.data.mineInfo,
                            mineAccount: that.data.mineAccount
                        })

                    }

                }
            });
        };
    },

    // 权限
    jurisdiction: function() {
        var that = this;

        var jurisdictionArr = app.globalData.authorities;
        var jurisdictionFlag = false;
        // 出票权限
        if (jurisdictionArr.indexOf('tic') != -1) {
            this.data.adminData[0].adminFlag = true
        }
        // 兑奖权限
        if (jurisdictionArr.indexOf('cash') != -1) {
            this.data.adminData[1].adminFlag = true
        }
        // 提现权限
        if (jurisdictionArr.indexOf('cash') != -1) {
            this.data.adminData[2].adminFlag = true
        }
        if (jurisdictionArr.indexOf('cash') != -1) {
            this.data.privilegesFlag = true
        }
        this.setData({
            adminData: this.data.adminData,
            privilegesFlag: this.data.privilegesFlag
        });
        for (var i = 0; i < that.data.adminData.length; i++) {

            if (that.data.adminData[i].adminFlag) {
                jurisdictionFlag = true
            }
        }

        if (jurisdictionFlag) {
            wx.request({
                url: app.globalData.requestUrl + '/admin/listQueueSize',
                header: {
                    "Content-Type": "application/json",
                    "X-Authentication-Token": app.globalData.jwtToken
                },
                method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT       
                success: function(res) {

                    // adminData
                    if (res.statusCode == 200) {
                        wx.hideNavigationBarLoading() //完成停止加载
                        wx.stopPullDownRefresh() //停止下拉刷新
                        that.data.adminData[0].num = res.data["pending-ticket-queue"]
                        that.data.adminData[1].num = res.data["win-prize-queue"];
                        that.setData({
                            adminData: that.data.adminData
                        })
                    }
                }
            });
        }
    },
    onPullDownRefresh: function() {
        this.fetchData()
        this.jurisdiction()
    }
})