var app = getApp();
var pageNum;
var pageSize = 12;
var pageSum, dataList;
var count = 2
Page({
    // 页面初始化数据
    data: {
        expandFlag: true,
        imgSrc: null,
        dataList: []

    },
    // 页面初始化生命周期函数
    onLoad: function() {
        new app.WeToast();
        // this.fetchData()
    },
    chooseImage: function(e) {
        var that = this;

        var betNo = e.currentTarget.dataset.betno;

        wx.chooseImage({
            sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
            sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
            success: function(res) {
                var tempFilePaths = res.tempFilePaths[0];
                // that.uploadFile2(tempFilePaths,0);
                for (var i = 0; i < that.data.dataList.length; i++) {

                    if (that.data.dataList[i].betNo == betNo) {


                        that.data.dataList[i].imgSrc = tempFilePaths
                    }
                }
                that.setData({
                    dataList: that.data.dataList
                });

            }
        })

    },
    fetchData: function() {
        var that = this;

        if (that.data.dataList.length != 0) {
            var sum = 0;
            for (var i = 0; i < that.data.dataList.length; i++) {
                if (that.data.dataList[i].disabledFlag) {
                    sum++
                }
            };


            if (sum < that.data.dataList.length) {
                that.wetoast.toast({
                    title: '数据未处理完'
                })
                return
            }
        }
        wx.request({
            url: app.globalData.requestUrl + "/admin/listRecordPaid/" + count,
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {
                console.log(res.data)
                if (res.statusCode === 200) {

                    if (res.data.length == 0) {
                        that.wetoast.toast({
                            title: '暂无数据'
                        });
                        return
                    }



                    for (var i = 0; i < res.data.length; i++) {
                        res.data[i].disabledFlag = false
                    }


                    that.setData({
                        dataList: res.data
                    })

                } else {
                    that.wetoast.toast({
                        title: '数据加载失败'
                    })
                };
            },
            fail: function(res) {},
            complete: function(res) {
                wx.hideToast();
            },
        })
    },
    enter: function(e) {
        var that = this;
        var betNo = e.currentTarget.dataset.betno;
        var imgSrc = e.currentTarget.dataset.imgsrc;
        var index = e.currentTarget.dataset.index;

        if (!imgSrc) {
            that.wetoast.toast({
                title: '请先选择图片'
            })
            return
        }
        console.log(betNo)
        wx.uploadFile({
            url: app.globalData.requestUrl + "/admin//handleTicket/" + betNo,
            filePath: imgSrc,
            header: {
                "X-Authentication-Token": app.globalData.jwtToken
            },
            name: 'file',
            success: function(res) {

                if (res.statusCode == 200) {
                    that.wetoast.toast({
                        title: '出票成功'
                    });
                    that.data.dataList[index].disabledFlag = true
                    that.setData({
                        dataList: that.data.dataList
                    })
                } else {
                    that.wetoast.toast({
                        title: '操作失败'
                    })
                }
            },
            fail: function() {
                that.wetoast.toast({
                    title: '网络错误'
                })
            }
        })
    },
    notTicker: function(e) {
        var that = this;
        var betNo = e.currentTarget.dataset.betno;
        var index = e.currentTarget.dataset.index;
        wx.showModal({
            title: '提示',
            content: '确认不出票',
            success: function(res) {
                if (res.confirm) {
                    wx.request({
                        url: app.globalData.requestUrl + '/admin/handleNoTicket/' + betNo,
                        header: {
                            "Content-Type": "application/json",
                            "X-Authentication-Token": app.globalData.jwtToken
                        },
                        method: 'POST',
                        success: function(res) {
                            wx.hideToast();

                            if (res.statusCode == 200) {
                                that.wetoast.toast({
                                    title: '操作成功'
                                });
                                that.data.dataList[index].disabledFlag = true
                                that.setData({
                                    dataList: that.data.dataList
                                })
                            } else {
                                that.wetoast.toast({
                                    title: '操作失败'
                                })
                            }
                        },
                        fail: function() {
                            wx.hideToast();
                        }
                    })
                }
            }
        })
    }
})