var app = getApp();
// 定义AllBills类（面向对象的写法）
class AllBills {
    constructor() {};
    getAllBillsData(page, num, callBack) {
        wx.request({
            url: app.globalData.requestUrl + '/admin/listRecordAll/' + num + '/' + page,
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {
                callBack(res);
            },
            fail: function(res) {},
        })
    }
}
export {
    AllBills
};