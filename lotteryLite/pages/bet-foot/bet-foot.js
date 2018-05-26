const app = getApp();
var arrShop = [];
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
        betData: [],
        checkFlag: true,
        multiple: 10,
        nextFlag: true,
        // 抽屉层
        animationData: false,
        typeData: [],
        chooseArr: [],
        chooseStr: '',
        typeWorld: false,
        betSize: 0,
        /*页面上的注数*/
        costSum: 0,
        /*投注的钱*/
        bonus: '0',
        /*奖金*/
        betListArr: [],
        /*传给后台的betList*/
        adminArr: [{
            name: 'recommend',
            value: '推荐',
            checked: 'true'
        }, {
            name: 'confirmBet',
            value: '投注',
        }, ],
        ratioValue: 'recommend',
        admin: false
            // 管理员权限
    },
    onLoad: function(options) {
        new app.WeToast(); //weToatst
        this.typeInit(); /*初始化几串1*/
        // 页面初始化 options为页面跳转所带来的参数
        this.fetchData()
            // 初始化默认type
        this.defaultType();


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
    /*获取数据*/
    fetchData: function() {
        var shop = app.globalData.raceShopCart;

        if (app.globalData.authorities.indexOf('rec') != -1) {
            this.setData({
                admin: true
            })
        }
        this.setData({
            betData: []
        });
        arrShop = [];
        for (var k of shop) {
            var item = {};
            var itemArr = [];
            var _flag = false;
            // console.log(itemArr[0]) /*获取gameId*/
            // console.log(itemArr[1]) /*如果是0就是不让球的*/    
            // console.log(itemArr[2]) /*判断胜负平*/
            itemArr = k[0].split("-")

            for (var i = 0; i < arrShop.length; i++) {
                //  如果arrShop里面有这个gameid的话
                if (itemArr[0] == arrShop[i].gameId) {
                    if (itemArr[1] == 0) {
                        // 这里就是不让球的
                        if (itemArr[2] == 3) {
                            arrShop[i].hadWinFlag = true
                        }
                        if (itemArr[2] == 1) {
                            arrShop[i].hadDrawFlag = true
                        }
                        if (itemArr[2] == 0) {
                            arrShop[i].hadLoseFlag = true
                        }
                    } else {
                        // 这里就是让球的
                        if (itemArr[2] == 3) {
                            arrShop[i].hhadWinFlag = true
                        }
                        if (itemArr[2] == 1) {
                            arrShop[i].hhadDrawFlag = true
                        }
                        if (itemArr[2] == 0) {
                            arrShop[i].hhadLoseFlag = true
                        }
                    }
                    _flag = true
                }

            };
            // 如果arrShop里面是唯一一个 (可能之前修改的flag并未改变，所以如果是唯一一个就根据他的key值来判断胜平负，而把之前修改的修改为false)
            if (!_flag) {
                if (itemArr[1] == 0) {
                    // 这里就是不让球的
                    if (itemArr[2] == 3) {
                        k[1].hadWinFlag = true
                        k[1].hadDrawFlag = false
                        k[1].hadLoseFlag = false
                        k[1].hhadLoseFlag = false
                        k[1].hhadDrawFlag = false
                        k[1].hhadWinFlag = false

                    }
                    if (itemArr[2] == 1) {
                        k[1].hadDrawFlag = true
                        k[1].hadWinFlag = false
                        k[1].hadLoseFlag = false
                        k[1].hhadLoseFlag = false
                        k[1].hhadDrawFlag = false
                        k[1].hhadWinFlag = false
                    }
                    if (itemArr[2] == 0) {
                        k[1].hadLoseFlag = true
                        k[1].hadDrawFlag = false
                        k[1].hadWinFlag = false
                        k[1].hhadLoseFlag = false
                        k[1].hhadDrawFlag = false
                        k[1].hhadWinFlag = false
                    }
                } else {
                    // 这里就是让球的
                    if (itemArr[2] == 3) {
                        k[1].hhadWinFlag = true
                        k[1].hhadDrawFlag = false
                        k[1].hhadLoseFlag = false
                        k[1].hadLoseFlag = false
                        k[1].hadDrawFlag = false
                        k[1].hadWinFlag = false
                    }
                    if (itemArr[2] == 1) {
                        k[1].hhadDrawFlag = true
                        k[1].hhadWinFlag = false
                        k[1].hhadLoseFlag = false
                        k[1].hadLoseFlag = false
                        k[1].hadDrawFlag = false
                        k[1].hadWinFlag = false
                    }
                    if (itemArr[2] == 0) {
                        k[1].hhadLoseFlag = true
                        k[1].hhadDrawFlag = false
                        k[1].hhadWinFlag = false
                        k[1].hadLoseFlag = false
                        k[1].hadDrawFlag = false
                        k[1].hadWinFlag = false
                    }
                }
                item = k[1];
                arrShop.push(item)
            }
        }
        /*按照id的排序方法*/
        arrShop.sort(compare('gameId'));

        this.setData({
            betData: arrShop
        });
        // this.count(arrShop)
    },

    /*删除单独项*/
    delItem: function(e) {
        var that = this;
        wx.showModal({
            title: '清空已选',
            content: '是否确定已选',
            success: function(res) {
                if (res.confirm) {
                    if (that.data.betData.length <= 2) {
                        that.wetoast.toast({
                            title: '请至少保留两场'
                        });
                    } else {
                        var reGameId = e.currentTarget.dataset.gameid;
                        for (var i = 0; i < that.data.betData.length; i++) {
                            if (that.data.betData[i].gameId == reGameId) {
                                that.data.betData.splice(i, 1)
                            }
                        };

                        that.setData({
                            betData: that.data.betData
                        });
                        that.defaultType();
                    }
                }
            }
        })

    },
    /* /删除单独项*/

    checkboxChange: function(e) {

        this.setData({
            checkFlag: !this.data.checkFlag
        })


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

        this.data.multiple = e.detail.value;
        this.setData({
            multiple: this.data.multiple
        });
        this.countPiece()

    },


    minus: function() {
        var count;
        count = --this.data.multiple;
        if (count <= 0) {
            this.wetoast.toast({
                title: '倍数必须大于0'
            });
            this.setData({
                multiple: 1
            });
            return
        }
        this.setData({
            multiple: count
        });

        this.countPiece()
    },
    add: function() {

        if (this.data.multiple >= app.globalData.maxMultiple) {
            this.wetoast.toast({
                title: '理性投注,量力而行'
            });
            return
        } else {
            this.setData({
                multiple: ++this.data.multiple
            });
            this.countPiece()
        }
    },
    /*抽屉层*/
    setModalStatus: function(e) {
        var that = this;
        var animation = wx.createAnimation({
            duration: 200,
            timingFunction: "linear",
            delay: 0
        })
        this.animation = animation
        animation.translateY(300).step()
        this.setData({
            animationData: animation.export()
        })
        if (e.currentTarget.dataset.status == 1) {
            that.setData({
                showModalStatus: true
            });
        }
        setTimeout(function() {
            animation.translateY(0).step()
            this.setData({
                animationData: animation
            })
            if (e.currentTarget.dataset.status == 0) {

                this.setData({
                    showModalStatus: false
                });
            }
        }.bind(this), 200)
    },
    /*选择过关方式或则取消*/
    itemTypeChoose: function(e) {
        var that = this;
        var idx = e.currentTarget.dataset.id;

        if (!that.data.typeData[idx].chooseFlag) {
            return
        }

        this.data.typeData[idx].flag = !this.data.typeData[idx].flag;

        this.setData({
            typeData: this.data.typeData
        });

        this.pushTypeItem();
        this.countPiece() /*算注数*/

    },
    /*先把过关方式可以点击的筛选出来*/
    defaultType: function() {
        var that = this;
        for (var i = 0; i < this.data.typeData.length; i++) {
            /*默认2串1*/
            if (i < that.data.betData.length - 1) {
                if (i == that.data.betData.length - 2) {
                    this.data.typeData[i].chooseFlag = true;
                    this.data.typeData[i].flag = true;
                    continue
                }
                this.data.typeData[i].chooseFlag = true;
                this.data.typeData[i].flag = false
            } else {
                this.data.typeData[i].chooseFlag = false;
                this.data.typeData[i].flag = false
            }
            if (that.data.betData.length >= 8) {
                this.data.typeData[6].flag = true
            }
        };
        this.setData({
            typeData: this.data.typeData
        })
        this.pushTypeItem();
        this.countPiece()
    },

    // 初始化几串1
    typeInit: function() {
        var that = this;
        // this.data.typeData
        for (var i = 2; i < 9; i++) {
            var item = {};
            item.flag = false;
            /*可选不可选*/
            item.chooseFlag = false;
            item.data = i;
            item.content = i + '串1';
            that.data.typeData.push(item)
        };
        this.setData({
            typeData: this.data.typeData
        })
    },
    /*选择的过关方式放入数组*/
    pushTypeItem: function() {
        var that = this;
        var chooseArr = [];
        var chooseStr = ''
        for (var i = 0; i < this.data.typeData.length; i++) {
            if (this.data.typeData[i].flag) {
                chooseArr.push(this.data.typeData[i]);
                chooseStr += this.data.typeData[i].content + ','
            }
        };
        chooseStr = chooseStr.substring(0, chooseStr.length - 1)
        if (chooseArr.length == 0) {
            this.data.typeWorld = true
        } else {
            this.data.typeWorld = false
        }
        this.setData({
            chooseStr: chooseStr,
            chooseArr: chooseArr,
            typeWorld: this.data.typeWorld
        });
    },
    /*调用算注数方法*/
    countPiece: function() {

        var that = this;
        var countArr = [];
        var betCount = 0;
        var betList = [];
        var maxMinArr = [];
        var sum = 0
        var shop = app.globalData.raceShopCart;
        var betListArr = []
        for (var k of shop) {

            countArr.push(k[0])
        };

        // console.log(betList)
        /*算出注数*/
        if (this.data.chooseArr.length > 0) {
            for (var i = 0; i < this.data.chooseArr.length; i++) {
                var maxMin = [];
                betList = that.combinationSelectMain(countArr, this.data.chooseArr[i].data);
                betListArr.push(betList);
                betCount += betList.length /*注数*/
                    // 算金额
                maxMin = this.min_max_prize(betList);
                sum += maxMin[1];

                maxMinArr.push(maxMin[0], sum);
            }
        }


        // 算出了最大值和最小值
        maxMinArr = app.maxMin(maxMinArr);

        if (typeof(maxMinArr[0]) != 'undefined') {

            if (maxMinArr[0] == maxMinArr[1]) {
                this.data.bonus = (maxMinArr[0] * this.data.multiple).toFixed(2)
            } else {
                this.data.bonus = (maxMinArr[0] * this.data.multiple).toFixed(2) + '~' + (maxMinArr[1] * this.data.multiple).toFixed(2)
            }
        } else {
            this.data.bonus = '0'
        }
        this.data.costSum = betCount * 2 * this.data.multiple

        this.setData({
            betSize: betCount,
            costSum: this.data.costSum,
            bonus: this.data.bonus,
            betListArr: betListArr
        });

        /*/算出注数*/

    },
    /*确定跳转*/
    enter: function() {
        var that = this;
        if (!this.data.checkFlag) {
            return
        }

        if (this.data.chooseArr.length == 0) {
            this.wetoast.toast({
                title: '请选择过关方式'
            });

            return
        }
        if (this.data.multiple == '') {

            return
        }
        /*发送数据给后台*/
        var betDetail = '';

        for (var k of app.globalData.raceShopCart) {
            betDetail += k[0] + ","
        }
        betDetail = betDetail.substring(0, betDetail.length - 1)

        // console.log(that.data.betListArr)
        // console.log('下注金额' + that.data.costSum)
        // console.log('下注倍数' + that.data.multiple)
        // console.log('多少注' + that.data.betSize)

        // 判断是否是管理员，若是管理员选择的 是哪一个
        var url = '';
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
                lotteryType: 7,
                betAmount: that.data.costSum,
                /*下注金额*/
                betTimes: that.data.multiple,
                /*下注倍数*/
                betPiece: that.data.betSize,
                /*下注倍数*/
                passType: that.data.chooseStr,
                /*多少注*/
                bonus: that.data.bonus,
                /*理论奖金*/
                betDetail: betDetail
            },
            header: {
                "Content-Type": "application/json",
                "X-Authentication-Token": app.globalData.jwtToken
            },
            method: 'POST',
            success: function(res) {
                // res.data为后台返回给我 的id


                wx.hideToast();

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
                wx.hideToast();

                that.wetoast.toast({
                    title: '网络错误'
                });
            }
        });
    },






    // 算注数的方法
    // 计算阶乘，即n! = n * (n-1) * ... * 2 * 1
    factorial: function(n) {
        return (n > 1) ? n * this.factorial(n - 1) : 1
    },

    // 计算组合数，即C(n, m) = n!/((n-m)! * m!)
    combination: function(n, m) {
        return (n >= m) ? this.factorial(n) / this.factorial(n - m) / this.factorial(m) : 0
    },

    combinationSelectMain: function(dataList, n) {
        var betList = []
        this.combinationSelect(dataList, 0, new Array(n), 0, betList)
        return betList
    },

    combinationSelect: function(dataList, dataIndex, resultList, resultIndex, betList) {
        var resultLen = resultList.length
        var resultCount = resultIndex + 1
        if (resultCount > resultLen) {
            if (!this.duplicate(resultList)) {
                var tempList = resultList.slice();
                betList.push(tempList)
            }
            return;
        }
        // 递归选择下一个
        for (var i = dataIndex; i < dataList.length + resultCount - resultLen; i++) {
            resultList[resultIndex] = dataList[i]
            this.combinationSelect(dataList, i + 1, resultList, resultIndex + 1, betList)
        }
    },

    // 检查场次重复
    duplicate: function(resutList) {
        var gameIdArr = []
        for (var i = 0; i < resutList.length; i++) {
            var gameId = resutList[i].substring(0, resutList[i].indexOf('-'));
            if (gameIdArr.indexOf(gameId) != -1) {
                return true
            } else {
                gameIdArr.push(gameId)
            }
        }
        return false
    },

    // 计算最小，最大奖金,betList为二维数组
    min_max_prize: function(betList) {
        //存放最小值，最大值的数组，第一位存最小值，第二位存最大值
        var min_max_arr = [0.0, 0.0]
            // 多组最大值累加后最终最大值
        var max = 0.0
            //存放每场比赛中的最大赔率
        var ratioMap = new Map()
        for (var i = 0; i < betList.length; i++) {
            var cur = 2.0
            for (var j = 0; j < betList[i].length; j++) {
                var item = betList[i][j]
                var game = item.substring(0, item.indexOf('-'))
                var ratio = item.substring(item.lastIndexOf('-') + 1)
                ratio = parseFloat(ratio)
                cur *= ratio

                //更新每场比赛中的最大赔率
                if (ratioMap.has(game)) {
                    if (ratioMap.get(game) < ratio) {
                        ratioMap.set(game, ratio)
                    }
                } else {
                    ratioMap.set(game, ratio)
                }
            }
            //第一位存最小值
            min_max_arr[0] = min_max_arr[0] == 0.0 ? cur : cur < min_max_arr[0] ? cur : min_max_arr[0]
        }

        //算最大值
        for (var i = 0; i < betList.length; i++) {
            var cur = 2.0
                //做累加的标识
            var flag = true
            for (var j = 0; j < betList[i].length; j++) {
                var item = betList[i][j]
                var game = item.substring(0, item.indexOf('-'))
                var ratio = item.substring(item.lastIndexOf('-') + 1)
                ratio = parseFloat(ratio)
                    //一旦该注里面的只要有一个赔率不是该场比赛的最大赔率，跳出，继续计算下一注
                if (ratioMap.get(game) != ratio) {
                    flag = false
                    break
                }
                cur *= ratio
            }
            //多注最大值做累加
            if (flag) {
                max += cur
            }
        }
        min_max_arr[1] = max
        return min_max_arr
    },
    // 管理员的
    adminRatio: function(e) {

        this.setData({
            ratioValue: e.detail.value
        })

    }
})