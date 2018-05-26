var app = getApp()
Page({
    data: {
        bankName: "",
        bankNum: "",
        accountMoney: 0,
        condition: false,
        extractValue: 0
    },
    // go_records: function() {
    //     wx.navigateTo({
    //         url: '../withdrawal-records/withdrawal-records',
    //     })
    // },
    bindBankCards: function() {
        wx.navigateTo({
            url: '../user-add-bank-cards/user-add-bank-cards',
        })
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function() {

    },
    onShow: function() {
        new app.WeToast(); //weToatst

        this.getBanksData();
    },
    getBanksData: function(e) {
        var that = this;
        wx.request({
            url: app.globalData.requestUrl + '/user/getAccount',
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {
                var dataArr = res.data;
                if (res.statusCode == 200) {
                    if (dataArr.bankName != null) {
                        var str = '';
                        str = '**** **** **** **** ' + dataArr.bankCard.substr(dataArr.bankCard.length - 4);
                        that.setData({
                            bankName: dataArr.bankName,
                            bankNum: str,
                            condition: true
                        });
                    }
                    that.setData({
                        accountMoney: dataArr.balance,
                    })
                }
            }
        })
    },
    extractAll: function() {
        this.setData({
            extractValue: this.data.accountMoney
        })
    },
    bindValue: function(e) {
        this.setData({
            extractValue: e.detail.value
        })
    },
    extractEnter: function() {
        var that = this;
        if (!this.data.condition) {
            this.wetoast.toast({
                title: '请先绑定银行卡'
            });
            return
        }

        var reg = /^0{1}([.]\d{1,2})?$|^[1-9]\d*([.]{1}[0-9]{1,2})?$/;
        if (!reg.test(that.data.extractValue)) {
            this.wetoast.toast({
                title: '请输入正确的金额格式'
            });
            return
        }
        if (that.data.extractValue <= 0 || that.data.extractValue > that.data.accountMoney) {
            this.wetoast.toast({
                title: '请输入小于当前余额的数字'
            });
            return
        }
        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        wx.request({
            url: app.globalData.requestUrl + '/user/withdraw',
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            data: that.data.extractValue,
            success: function(res) {
                wx.hideToast();
                if (res.statusCode == 200) {
                    wx.showModal({
                        title: '提示',
                        content: '提现成功',
                        showCancel: false,
                        success: function(res) {
                            wx.navigateTo({
                                url: "../user-account-details/user-account-details"
                            });
                        }
                    })
                } else {

                    wx.showModal({
                        title: '提示',
                        content: res.data,
                        showCancel: false,
                        success: function(res) {
                            wx.navigateTo({
                                url: "../user-withdrawal-cash/user-withdrawal-cash"
                            });
                        }
                    })
                }

            },
            fail: function() {
                wx.hideToast();

            }
        })

    }
})