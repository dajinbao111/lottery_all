var app = getApp();
var page;
var pageSize = 14;
var pagesTotal;
Page({
    data: {
        moneyTextColor: false, //若为true，金额字体颜色显示为绿色（收入）
        dataFlag: true,
        isLoadMore: false,
        loadingText: '— 上拉加载更多 —',
        itemList: []
    },
    // 获取数据函数
    getDatas: function() {
        var that = this;



        if (pagesTotal) {
            if (pagesTotal <= page) {
                that.wetoast.toast({
                    title: '没有更多了',
                    titleClassName: 'check_info'
                });

                that.setData({
                    loadingText: '— 没有更多数据 —'
                })
                return
            }
        };
        that.setData({
            loadingText: '— 上拉加载更多 —'
        })
        page++;
        wx.showToast({
            title: '数据加载中...',
            icon: 'loading',
        });
        wx.request({
            url: app.globalData.requestUrl + "/user/listAccountRecord/" + pageSize + "/" + page,
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {

                var dataList = res.data.list;
                if (res.statusCode === 200) {

                    wx.hideNavigationBarLoading() //完成停止加载
                    wx.stopPullDownRefresh() //停止下拉刷新
                        // 判断有无数据
                    pagesTotal = res.data.pages
                    if (dataList.length === 0) {
                        that.data.dataFlag = false;
                    };
                    // 判断是否需要分页




                    for (var i = 0; i < dataList.length; i++) {
                        that.data.itemList.push(dataList[i]);
                        if (dataList[i].changeAmount > 0) {
                            that.data.moneyTextColor = true; //正数
                        };
                    };
                    that.setData({
                        itemList: that.data.itemList,
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
    onLoad: function() {
        new app.WeToast();
        page = 0;
        this.getDatas();

    },
    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function() {
        // page = 1;
        // if (this.data.isLoadMore) {
        //     this.getDatas();
        // }
        this.getDatas();

    },
    onPullDownRefresh: function() {
        wx.showNavigationBarLoading() //在标题栏中显示加载
        this.setData({
            itemList: []
        })
        page = 0;
        this.getDatas()
    }
})