var app = getApp();
// 去除空格
function Trim(str) {
    return str.replace(/(^\s*)|(\s*$)/g, "");
}


Page({
    data: {
        userInputCardNo: '',
        onoff: null,
        bankNames: '',
        userName: '',
        bankNumOld: null,
        valData: null
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function(options) {
        new app.WeToast();
    },

    // 输入银行卡号触发的事件
    bankNumTap: function(e) {

        var val = e.detail.value;

        val = val.replace(/(\d{4})(?=\d)/g, "$1 ");

        // 回退删除事件
        if (this.data.userInputCardNo.length > val.length) {


            if (val.length == 20 || val.length == 5 || val.length == 15 || val.length == 10) {

                val = val.substring(0, val.length - 1);

            }
        }

        this.setData({
            userInputCardNo: val,
        });


        // 去除空格传后台数据
        this.data.valData = val.replace(/\s/g, '');
        this.setData({
            valData: this.data.valData
        })
    },

    userNameTap: function(e) {
        this.setData({
            userName: e.detail.value
        })
    },
    submitTap: function() {
        var that = this;

        if (this.data.userName.length <= 0 || this.data.userInputCardNo.length <= 0) {

            that.wetoast.toast({
                title: '请输入完整',
                titleClassName: 'check_info'
            });
        }

        wx.showToast({
            title: '加载中',
            icon: 'loading',
            duration: 5000
        });
        wx.request({
            url: app.globalData.requestUrl + '/user/addBankCard',
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            data: {
                bankAccount: that.data.userName,
                bankCard: that.data.valData,
            },
            method: 'POST',
            success: function(res) {

                wx.hideToast();

                if (res.statusCode == 200) {

                    wx.navigateTo({
                        url: '../user-withdrawal-cash/user-withdrawal-cash'
                    })
                } else {
                    if (res.data == '错误的卡号') {
                        that.wetoast.toast({
                            title: '错误的卡号',
                            titleClassName: 'check_info'
                        });
                    } else if (res.data == '不支持信用卡') {
                        that.wetoast.toast({
                            title: '不支持信用卡',
                            titleClassName: 'check_info'
                        });
                    } else {
                        that.wetoast.toast({
                            title: '未知错误',
                            titleClassName: 'check_info'
                        });
                    }

                }
            },
            fail: function() {
                wx.hideToast();

                that.wetoast.toast({
                    title: '网络错误',
                    titleClassName: 'check_info'
                });
            }
        })

    },

})