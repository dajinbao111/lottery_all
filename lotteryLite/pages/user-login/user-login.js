const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
        // 验证码计时
        btnFlag: false,
        btnValue: '获取验证码',
        // 验证码计时
        phone: null,
        code: null,
        _nickName: null,
        _avatarUrl: null
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function(options) {
        new app.WeToast(); //weToatst

    },

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
        wx.getUserInfo({
            success: function(res) {

                that.data._nickName = res.userInfo.nickName
                that.data._avatarUrl = res.userInfo.avatarUrl
            }
        });
        this.setData({
            _nickName: this.data._nickName,
            _avatarUrl: this.data._avatarUrl
        })
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
     * 发送验证码
     */
    // 发送验证码
    sendcode: function() {
        var countdown = 60;
        var that = this;

        if (that.data.phone == null || that.data.phone.length != 11) {
            that.wetoast.toast({
                title: '请先填写手机号',
                titleClassName: 'check_info'
            });
            return
        };
        // 请求验证码
        wx.request({
                url: app.globalData.requestUrl + '/auth/sendCode',
                method: 'POST',
                data: {
                    openId: app.globalData.openId,
                    phoneNum: that.data.phone
                },
                header: {
                    'content-type': 'application/json'
                },
                success: function(res) {


                    if (res.data.error_response) {
                        that.wetoast.toast({
                            title: '网络请求未知错误',
                            titleClassName: 'check_info'
                        });
                    }
                },
                fail: function() {
                    that.wetoast.toast({
                        title: '网络请求未知错误',
                        titleClassName: 'check_info'
                    });
                }
            })
            // 请求验证码End
        function settime() {
            if (countdown == 0) {
                that.data.btnFlag = false
                that.data.btnValue = "获取验证码";
                countdown = 10;
                that.setData({
                    btnFlag: that.data.btnFlag,
                    btnValue: that.data.btnValue
                })
                return;
            } else {
                that.data.btnFlag = true
                that.data.btnValue = "(" + countdown + ")";
                that.setData({
                    btnFlag: that.data.btnFlag,
                    btnValue: that.data.btnValue
                })
                countdown--;
            }
            setTimeout(function() {
                settime()
            }, 1000)
        };
        settime()

    },
    // /请求验证码
    // 手机号
    phoneInput: function(e) {
        this.setData({
            phone: e.detail.value
        });
    },
    codeInput: function(e) {
        this.setData({
            code: e.detail.value
        });

    },
    // 登录按钮事件
    login: function() {
        var that = this;
        var _openId = app.globalData.openId;
        var _phoneNum = that.data.phone;
        var _code = that.data.code;
        if (_phoneNum == null || _phoneNum.length != 11 || _code == null) {
            that.wetoast.toast({
                title: '请填写完整',
                titleClassName: 'check_info'
            });
            return
        }
        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        wx.request({
            url: app.globalData.requestUrl + '/auth/verifyCode',
            method: 'POST',
            data: {
                openId: app.globalData.openId,
                nickName: that.data._nickName || '',
                avatarUrl: that.data._avatarUrl || '',
                phoneNum: _phoneNum,
                authCode: _code
            },
            header: {
                'content-type': 'application/json'
            },
            success: function(res) {
                wx.hideToast();

                if (res.statusCode == 200) {

                    that.wetoast.toast({
                        title: '登录成功',
                        titleClassName: 'check_info'
                    });
                    setTimeout(function() {
                        wx.switchTab({
                            url: '../user-mine/user-mine'
                        })
                    }, 1200)


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
})