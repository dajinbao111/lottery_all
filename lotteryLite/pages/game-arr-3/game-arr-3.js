var app = getApp()
Page({
    data: {
        arr_3_datas: {
            numList: [{
                id: 1,
                name: '百位',
                numArray: [{
                    id: 11,
                    number: 0,
                }, {
                    id: 12,
                    number: 1,
                }, {
                    id: 13,
                    number: 2,
                }, {
                    id: 14,
                    number: 3,
                }, {
                    id: 15,
                    number: 4,
                }, {
                    id: 16,
                    number: 5,
                }, {
                    id: 17,
                    number: 6,
                }, {
                    id: 18,
                    number: 7,
                }, {
                    id: 19,
                    number: 8,
                }, {
                    id: 20,
                    number: 9,
                }]
            }, {
                id: 2,
                name: '十位',
                numArray: [{
                    id: 21,
                    number: 0,
                }, {
                    id: 22,
                    number: 1,
                }, {
                    id: 23,
                    number: 2,
                }, {
                    id: 24,
                    number: 3,
                }, {
                    id: 25,
                    number: 4,
                }, {
                    id: 26,
                    number: 5,
                }, {
                    id: 27,
                    number: 6,
                }, {
                    id: 28,
                    number: 7,
                }, {
                    id: 29,
                    number: 8,
                }, {
                    id: 30,
                    number: 9,
                }]
            }, {
                id: 3,
                name: '个位',
                numArray: [{
                    id: 31,
                    number: 0,
                }, {
                    id: 32,
                    number: 1,
                }, {
                    id: 33,
                    number: 2,
                }, {
                    id: 34,
                    number: 3,
                }, {
                    id: 35,
                    number: 4,
                }, {
                    id: 36,
                    number: 5,
                }, {
                    id: 37,
                    number: 6,
                }, {
                    id: 38,
                    number: 7,
                }, {
                    id: 39,
                    number: 8,
                }, {
                    id: 40,
                    number: 9,
                }]
            }]
        }
    },
    // 数字按钮点击事件
    changeStyle: function(options) {
        var that = this;
        var id = options.currentTarget.dataset.id;
        that.setData({
            'currentItem': id
        })

    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function(options) {

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

    }
})