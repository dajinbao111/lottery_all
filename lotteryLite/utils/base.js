class Base {
    constructor() {
        this.baseRequestUrl = 'https://www.wisestar.org/lottery';
    };
    request(params) {
        var url = this.baseRequestUrl + params.url;
        if (!params.type) {
            params.type = "GET"
        };
         wx.request({
            url: url,
            data: params.data,
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: params.type,
            success: function(res) {
                if (params.sCallBack) {
                    params.sCallBack(res)
                }
            },
            fail: function(err) {

            },
        })
    }
}
export {
    Base
}