const app = getApp();
var arrShop;
// var singleShop = app.globalData.singleShopCart
// 根据属性排序
function compare(property) {
    return function(a, b) {
        var value1 = a[property];
        var value2 = b[property];
        return value1 - value2;
    }
};

Page({
    data: {
        mathData: [],
        totalData: {
            total: 0,
            num: 0,
            multiple: 0,
            singleFlag: false,
            singleBouns: ''
        },
        multipleData: {
            multiple: 10
        },
        nextFlag: true,
        // 判断是任9的数据还是14的数据
        type: null,
        typeFlag: false,
        singleData: {},
        adminArr: [{
            name: 'recommend',
            value: '推荐',
            checked: 'true'
        }, {
            name: 'together',
            value: '合买',
        }, {
            name: 'confirmBet',
            value: '投注',
        }],
        ratioValue: 'recommend',
        // 管理员权限
        admin: false,
        time: '',
        piece: 1,
        togetherFlag: false
    },
    onLoad: function(options) {
        new app.WeToast(); //weToatst
        this.data.type = options.type;
        this.setData({
                type: this.data.type
            })
            // 页面初始化 options为页面跳转所带来的参数
        this.fetchData();
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
    // 获取数据
    fetchData: function() {
        this.setData({
            mathData: []
        });

        if (app.globalData.authorities.indexOf('rec') != -1) {
            this.setData({
                admin: true
            })
        }

        arrShop = [];
        if (this.data.type == 5) {
            // 5是14场
            var shop = app.globalData.fourteenShopCart;
            this.data.typeFlag = false;
            this.data.multipleData.multiple = 1
        } else if (this.data.type == 6) {
            // 6是任9
            var shop = app.globalData.nineShopCart;
            this.data.typeFlag = false
            this.data.multipleData.multiple = 1

        } else if (this.data.type == 8) {
            var shop = app.globalData.singleShopCart;
            this.data.typeFlag = true
            this.data.multipleData.multiple = 10
        }
        this.data.totalData.multiple = this.data.multipleData.multiple
        this.setData({
                multipleData: this.data.multipleData,
                totalData: this.data.totalData
            })
            /*单关进来的*/
        if (this.data.type == 8) {
            this.data.adminArr.splice(1, 1)
            this.data.singleData = shop;
            this.singleBouns(shop)
            for (var k of shop) {
                var item = {};
                var itemArr = [];
                var _flag = false;
                // console.log(itemArr[0]) /*获取gameId*/
                // console.log(itemArr[1]) /*如果是0就是不让球的*/    
                // console.log(itemArr[2]) /*判断胜负平*/
                itemArr = k[0].split("-");
                // console.log(itemArr[3]) /*看赔率*/
                for (var i = 0; i < arrShop.length; i++) {
                    //  如果arrShop里面有这个gameid的话
                    if (itemArr[0] == arrShop[i].gameId) {

                        // 这里就是不让球的
                        if (itemArr[2] == 3) {
                            arrShop[i].flag3 = true
                        }
                        if (itemArr[2] == 1) {
                            arrShop[i].flag1 = true
                        }
                        if (itemArr[2] == 0) {
                            arrShop[i].flag0 = true
                        }
                        _flag = true
                    }

                };
                // 如果arrShop里面是唯一一个 (可能之前修改的flag并未改变，所以如果是唯一一个就根据他的key值来判断胜平负，而把之前修改的修改为false)
                if (!_flag) {
                    if (itemArr[2] == 3) {
                        k[1].flag3 = true
                        k[1].flag1 = false
                        k[1].flag0 = false
                    }
                    if (itemArr[2] == 1) {
                        k[1].flag3 = false
                        k[1].flag1 = true
                        k[1].flag0 = false
                    }
                    if (itemArr[2] == 0) {
                        k[1].flag3 = false
                        k[1].flag1 = false
                        k[1].flag0 = true
                    }
                    item = k[1];
                    arrShop.push(item)
                }
            }
            this.data.singleData.no = arrShop[0].no.substring(2);
            this.data.singleData.flag3 = arrShop[0].flag3;
            this.data.singleData.flag0 = arrShop[0].flag0;
            this.data.singleData.flag1 = arrShop[0].flag1;
            this.data.singleData.homeTeam = arrShop[0].hostTeam;
            this.data.singleData.visitingTeam = arrShop[0].guestTeam;
            this.data.totalData.singleFlag = true
            this.setData({
                singleData: this.data.singleData,
                adminArr: this.data.adminArr,
                typeFlag: true,
                totalData: this.data.totalData
            });

            this.singleCount()
            return
        }
        // 单关完
        for (var k of shop) {
            var item = {};
            var _flag = false;
            for (var i = 0; i < arrShop.length; i++) {
                // arrshop里面有此序号
                if (arrShop[i].no == k[1].no) {
                    if (k[0].substr(-1, 1) == 3) {
                        arrShop[i].flag3 = true
                    }
                    if (k[0].substr(-1, 1) == 1) {
                        arrShop[i].flag1 = true
                    }
                    if (k[0].substr(-1, 1) == 0) {
                        arrShop[i].flag0 = true
                    }
                    _flag = true
                };
            };
            // 如果arrShop里面是唯一一个 (可能之前修改的flag并未改变，所以如果是唯一一个就根据他的key值来判断胜平负，而把之前修改的修改为false)
            if (!_flag) {
                if (k[0].substr(-1, 1) == 1) {
                    k[1].flag1 = true;
                    k[1].flag0 = false;
                    k[1].flag3 = false;
                }
                if (k[0].substr(-1, 1) == 0) {
                    k[1].flag1 = false;
                    k[1].flag0 = true;
                    k[1].flag3 = false;
                }
                if (k[0].substr(-1, 1) == 3) {
                    k[1].flag1 = false;
                    k[1].flag0 = false;
                    k[1].flag3 = true;
                }
                item = k[1];
                arrShop.push(item)
            }
        }
        arrShop.sort(compare('no'))
        this.data.totalData.singleFlag = false

        this.setData({
            mathData: arrShop,
            totalData: this.data.totalData
        });
        this.count(arrShop)
    },

    // 算金额
    count: function(arrShop) {
        // 算金额
        var sumArr = [];
        var sum = 1;
        for (var j = 0; j < arrShop.length; j++) {
            var count = 0;
            for (var key in arrShop[j]) {
                if (arrShop[j][key] === true) {
                    count++
                };
            }
            sumArr.push(count)
        }
        for (var idx = 0; idx < sumArr.length; idx++) {
            sum *= sumArr[idx]
        }

        // 金额   注数*倍数*2
        this.data.totalData.total = sum * this.data.multipleData.multiple * 2
            // 注数
        this.data.totalData.num = sum
        this.setData({
            totalData: this.data.totalData
        });
    },

    // 输入倍数
    bindMultiple: function(e) {
        if (e.detail.value.length == 1) {
            e.detail.value = e.detail.value.replace(/[^1-9]/g, '')
        } else {
            e.detail.value = e.detail.value.replace(/\D/g, '')
        }
        if (e.detail.value >= app.globalData.maxMultiple) {
            this.wetoast.toast({
                title: '理性投注,量力而行'
            });
            return
        }
        if (this.data.type == 8) {
            this.data.multipleData.multiple = e.detail.value;

            this.data.totalData.multiple = this.data.multipleData.multiple
            this.setData({
                multipleData: this.data.multipleData,
                totalData: this.data.totalData
            });
            this.singleCount()
            return
        }
        this.data.multipleData.multiple = e.detail.value;
        this.data.totalData.multiple = this.data.multipleData.multiple
        this.setData({
            multipleData: this.data.multipleData,
            totalData: this.data.totalData
        });
        this.count(arrShop)
    },
    // 确认投注
    betEnter: function() {
        var that = this;
        var url = '';
        // 如果倍数为负数直接返回
        if (this.data.multipleData.multiple == '') {
            this.wetoast.toast({
                title: '请确认倍数为正整数'
            });
            return
        }
        /*单关的*/
        if (this.data.type == 8) {
            var singleBet = ''
            for (var k of app.globalData.singleShopCart) {
                singleBet += k[0] + ','
            };
            singleBet = singleBet.substring(0, singleBet.length - 1)
            if (this.data.ratioValue == 'recommend') {
                url = '/admin/recommend'
            } else {
                url = '/bet/confirmBet'
            }
            wx.showToast({
                title: '加载中',
                icon: 'loading',
                duration: 5000
            });
            wx.request({
                url: app.globalData.requestUrl + url,
                data: {
                    lotteryType: that.data.type,
                    /*下注金额*/
                    betAmount: that.data.totalData.total,
                    /*下注倍数*/
                    betTimes: that.data.multipleData.multiple,
                    /*多少注*/
                    betPiece: that.data.totalData.num,
                    passType: '单关',
                    betDetail: singleBet,
                    bonus: that.data.totalData.singleBouns
                },
                header: {
                    "Content-Type": "application/json",
                    "X-Authentication-Token": app.globalData.jwtToken
                },
                method: 'POST',
                success: function(res) {
                    wx.hideToast()
                        // res.data为后台返回给我 的id
                    if (res.statusCode == 200) {
                        if (that.data.ratioValue == 'recommend') {
                            wx.navigateTo({
                                url: "../follow/follow"
                            });
                        } else {
                            wx.navigateTo({
                                url: "../payment/payment?id=" + res.data
                            });
                        }
                    } else {
                        that.wetoast.toast({
                            title: '网络错误'
                        });
                    }
                },
                fail: function() {
                    wx.hideToast()
                    that.wetoast.toast({
                        title: '网络错误'
                    });
                }
            });
            return
        };
        // console.log(that.data.type)
        // console.log('下注金额' + that.data.totalData.total)
        // console.log('下注倍数' + that.data.multipleData.multiple)
        // console.log('多少注' + that.data.totalData.num)
        var dataStr = '' /*传给后台拼接后的字符串*/
        var dataArr = []; /*已选择所有的'013'数据*/
        var phaseId = this.data.mathData[0].phaseId;
        var mathData = this.data.mathData;
        // 14
        if (that.data.type == 5) {
            // 得到类似'013的数组'
            for (var i = 0; i < mathData.length; i++) {
                var str = '';
                if (mathData[i].flag3) {
                    str += '3'
                };
                if (mathData[i].flag1) {
                    str += '1'
                };
                if (mathData[i].flag0) {
                    str += '0'
                };
                dataArr.push(str)
            };
            //  /得到类似'013的数组'
            // 把数组拼接成字符串
        } else if (that.data.type == 6) {
            for (var ii = 0; ii < 14; ii++) {
                dataArr.push('*')
            }
            for (var i = 0; i < mathData.length; i++) {
                var index = mathData[i].no;
                var str6 = '';
                if (mathData[i].flag3) {
                    str6 += '3';
                }
                if (mathData[i].flag1) {
                    str6 += '1';
                }
                if (mathData[i].flag0) {
                    str6 += '0';
                }
                dataArr[index - 1] = str6
            }
        }
        for (var j = 0; j < dataArr.length; j++) {
            dataStr += dataArr[j] + ','
        };
        dataStr = dataStr.substring(0, dataStr.length - 1)
        dataStr = phaseId + "-" + dataStr;
        console.log(dataStr)
            // 判断管理员是选择哪种
        if (this.data.ratioValue == 'recommend') {
            // 推荐的
            url = '/admin/recommend'
        } else {
            url = '/bet/confirmBet'
        }
        // 判断管理员是选择哪种
        // 数据发送给后台
        wx.request({
            url: app.globalData.requestUrl + url,
            data: {
                lotteryType: that.data.type,
                /*下注金额*/
                betAmount: that.data.totalData.total,
                /*下注倍数*/
                betTimes: that.data.multipleData.multiple,
                /*多少注*/
                betPiece: that.data.totalData.num,
                /**投注类型 */
                passType: that.data.totalData.num > 1 ? '复式' : '单式',
                betDetail: dataStr
            },
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {
                // res.data为后台返回给我 的id
                if (res.statusCode == 200) {

                    if (that.data.ratioValue == 'recommend') {
                        if (that.data.type == 5) {
                            // 14的推荐
                            wx.navigateTo({
                                url: "../follow/follow?type=5"
                            });
                        } else {
                            /*9的推荐*/
                            wx.navigateTo({
                                url: "../follow/follow?type=6"
                            });
                        }

                    } else {
                        wx.navigateTo({
                            url: "../payment/payment?id=" + res.data
                        });
                    }

                } else {
                    that.wetoast.toast({
                        title: '网络错误'
                    });
                }
            },
            fail: function() {
                that.wetoast.toast({
                    title: '网络错误'
                });
            }
        });
    },
    minus: function() {
        var count;
        count = --this.data.multipleData.multiple;
        if (count <= 0) {
            this.wetoast.toast({
                title: '倍数必须大于0'
            });
            this.data.multipleData.multiple = 1;
            this.setData({
                multipleData: this.data.multipleData
            });
            return
        }
        if (this.data.type == 8) {
            this.data.multipleData.multiple = count;
            this.data.totalData.multiple = this.data.multipleData.multiple;
            this.singleBouns(this.data.singleData)
            this.setData({
                multipleData: this.data.multipleData,
                totalData: this.data.totalData
            });
            this.singleCount()
            return
        }
        this.data.multipleData.multiple = count
        this.data.totalData.multiple = this.data.multipleData.multiple
        this.setData({
            multipleData: this.data.multipleData,
            totalData: this.data.totalData
        });
        this.count(arrShop)
    },
    add: function() {
        if (this.data.multipleData.multiple >= app.globalData.maxMultiple) {
            this.wetoast.toast({
                title: '理性投注,量力而行'
            });
            return
        }
        if (this.data.type == 8) {
            this.data.multipleData.multiple = ++this.data.multipleData.multiple
            this.data.totalData.multiple = this.data.multipleData.multiple
            this.singleBouns(this.data.singleData)
            this.setData({
                multipleData: this.data.multipleData,
                totalData: this.data.totalData
            });
            this.singleCount()
        } else {
            this.data.multipleData.multiple = ++this.data.multipleData.multiple
            this.data.totalData.multiple = this.data.multipleData.multiple
            this.setData({
                multipleData: this.data.multipleData,
                totalData: this.data.totalData
            });
            this.count(arrShop)
        }
    },
    // 单关算钱
    singleCount: function() {
        var sum = 1;
        this.data.totalData.num = this.data.singleData.size /*注数*/
        this.data.totalData.total = this.data.totalData.num * this.data.multipleData.multiple * 2;
        this.setData({
            totalData: this.data.totalData
        });
    },
    // 管理员的
    adminRatio: function(e) {
        this.setData({
            ratioValue: e.detail.value
        })
        if (e.detail.value == 'together') {
            this.setData({
                togetherFlag: true
            })
        } else {
            this.setData({
                togetherFlag: false
            })
        }
    },
    listenerTimePickerSelected: function(e) {
        //调用setData()重新绘制
        this.setData({
            time: e.detail.value,
        });
    },
    /*增加份数*/
    addPiece: function() {
        var that = this;
        if (this.data.piece > app.globalData.maxMultiple) {
            that.wetoast.toast({
                title: '理性投注,量力而行'
            });
            return
        }
        this.setData({
            piece: ++this.data.piece
        })
    },
    minusPiece: function() {
        var count;
        count = --this.data.piece;
        if (count <= 0) {
            this.wetoast.toast({
                title: '倍数必须大于0'
            });
            this.setData({
                piece: 1
            })
            return
        }
        this.data.piece = count
        this.setData({
            piece: this.data.piece
        });
    },
    bindMultiplePiece: function(e) {
        var that = this;
        if (e.detail.value.length == 1) {
            e.detail.value = e.detail.value.replace(/[^1-9]/g, '')
        } else {
            e.detail.value = e.detail.value.replace(/\D/g, '')
        }
        if (e.detail.value >= app.globalData.maxMultiple) {
            that.wetoast.toast({
                title: '理性投注,量力而行,'
            });
            return
        }
        this.setData({
            piece: e.detail.value
        })
    },
    // 单关算理论奖金
    singleBouns: function(cartShop) {
        // 如果选择只有一个的话
        var itemShop = [];
        var that = this;
        var odds = []
        for (var key of cartShop) {
            itemShop = key[0].split("-");
            odds.push(itemShop[3]);
        }
        if (odds.length == 1) {
            console.log('长度1');
            that.data.totalData.singleBouns = (odds[0] * 2 * that.data.multipleData.multiple).toFixed(2);
            console.log(that.data.totalData.singleBouns)
        } else {
            console.log('长度大于1');
            console.log(app.maxMin(odds));
            var min = (app.maxMin(odds)[0] * 2 * that.data.multipleData.multiple).toFixed(2);
            var max = (app.maxMin(odds)[1] * 2 * that.data.multipleData.multiple).toFixed(2);
            that.data.totalData.singleBouns = min + '~' + max
        }
        this.setData({
            totalData: this.data.totalData
        })
    }
})