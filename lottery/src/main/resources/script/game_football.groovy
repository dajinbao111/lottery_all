import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.wisestar.lottery.exception.ServiceException
import org.wisestar.lottery.util.http.SimpleHttpClient


def get_updated() {
    def rs = [:]
    def url = "http://i.sporttery.cn/odds_calculator/get_odds?i_format=json&i_callback=getData&poolcode[]=hhad&poolcode[]=had&_=" + System.currentTimeMillis()
    String rs1 = SimpleHttpClient.current.get(url).responseText

    if (rs1.contains("getData")) {
        rs1 = rs1.substring(8, rs1.size() - 2)
        JSONObject data1 = JSON.parseObject(rs1)

        //数据最后更新时间
        String lastUpdated = data1.getJSONObject("status").getString("last_updated")
        rs["lastUpdated"] = lastUpdated
        rs["format"] = "yyyy-MM-dd HH:mm:ss"
    } else {
        throw new ServiceException("get_updated fail, rs1=" + rs1)
    }

    return JSON.toJSONString(rs)
}

println(get_updated())

def get_data() {
    def rs = []

    def url = "http://i.sporttery.cn/odds_calculator/get_odds?i_format=json&i_callback=getData&poolcode[]=hhad&poolcode[]=had&_=" + System.currentTimeMillis()
    String rs2 = SimpleHttpClient.current.get(url).responseText

    if (rs2.contains("getData")) {
        rs2 = rs2.substring(8, rs2.size() - 2)
        JSONObject data2 = JSON.parseObject(rs2)

        //数据最后更新时间
        String lastUpdated = data2.getJSONObject("status").getString("last_updated")

        JSONObject data = data2.getJSONObject("data")

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            JSONObject entryValue = entry.getValue()
            def info = [:]
            info["gameId"] = entryValue.getString("id")
            info["gameEventType"] = entryValue.getString("l_cn_abbr")
            info["hostTeam"] = entryValue.getString("h_cn_abbr")
            info["guestTeam"] = entryValue.getString("a_cn_abbr")
            info["weekday"] = entryValue.getString("num")
            info["dueTime"] = entryValue.getString("date") + " " + entryValue.getString("time")
            info["lastUpdated"] = lastUpdated
            info["gameDate"] = entryValue.getString("b_date")

            def ratios = []
            if (entryValue.containsKey("hhad")) {   //hhad属于让球
                JSONObject hhad = entryValue.getJSONObject("hhad")
                def ratio = [:]
                ratio["gameId"] = entryValue.getString("id")
                ratio["rangqiu"] = hhad.getString("fixedodds")
                info["rangqiu"] = hhad.getString("fixedodds")
                ratio["winRatio"] = hhad.getString("h")
                ratio["drawRatio"] = hhad.getString("d")
                ratio["loseRatio"] = hhad.getString("a")
                ratio["lastUpdated"] = lastUpdated
                ratios.add(ratio)
            }

            if (entryValue.containsKey("had")) {    //不让球
                JSONObject had = entryValue.getJSONObject("had")
                def ratio = [:]
                ratio["gameId"] = entryValue.getString("id")
                ratio["rangqiu"] = had.getString("fixedodds") == "" ? "0" : had.getString("fixedodds")
                ratio["winRatio"] = had.getString("h")
                ratio["drawRatio"] = had.getString("d")
                ratio["loseRatio"] = had.getString("a")
                ratio["lastUpdated"] = lastUpdated

                //单关
                if (had.getIntValue("single") == 1) {
                    def single = [:]
                    single["gameId"] = entryValue.getString("id")
                    single["rangqiu"] = had.getString("fixedodds") == "" ? "0" : had.getString("fixedodds")
                    single["winRatio"] = had.getString("h")
                    single["drawRatio"] = had.getString("d")
                    single["loseRatio"] = had.getString("a")
                    single["lastUpdated"] = lastUpdated
                    ratio["single"] = single
                }
                ratios.add(ratio)
            }

            info["ratio"] = ratios
            rs.add(info)
        }

    } else {
        throw new ServiceException("getData fail, rs2=" + rs2)
    }

    return JSON.toJSONString(rs)
}

def get_rs(game_id) {
    def rs = [:]

    def url1 = "http://i.sporttery.cn/api/fb_match_info/get_pool_rs/?f_callback=pool_prcess&mid=" + game_id + "&_=" + System.currentTimeMillis()
    String rs1 = SimpleHttpClient.current.get(url1).responseText

    if (rs1.contains("pool_prcess")) {
        rs1 = rs1.substring(12, rs1.size() - 2)
        JSONObject data = JSON.parseObject(rs1)

        JSONObject result = data.getJSONObject("result")
        Object object = result.get("pool_rs")
        if (object instanceof JSONObject) {
            JSONObject poolrs = data.getJSONObject("result").getJSONObject("pool_rs")
            if (poolrs.containsKey("crs")) {
                def point = poolrs.getJSONObject("crs").getString("prs_name")     //比分

                if (point == "负其他" || point == "胜其他" || point == "平其他") {
                    def url2 = "http://i.sporttery.cn/api/match_info_live_2/get_match_live?m_id=" + game_id + "&f_callback=getMatchLiveBack&0.09895681666048595&_=" + System.currentTimeMillis()
                    String rs2 = SimpleHttpClient.current.get(url2).responseText
                    if (rs2.contains("getMatchLiveBack")) {
                        rs2 = rs2.substring(17, rs2.size() - 2)
                        JSONObject data2 = JSON.parseObject(rs2)

                        def ph = data2.getJSONObject("data").getString("fs_h")
                        def pa = data2.getJSONObject("data").getString("fs_a")
                        rs["point"] = ph + ":" + pa
                    }
                } else {
                    rs["point"] = point
                }
            }
            if (poolrs.containsKey("had")) {
                //非让球彩果
                def prs = poolrs.getJSONObject("had").getString("prs_name")
                if ("胜" == prs) {
                    rs["hadResult"] = "3"
                } else if ("负" == prs) {
                    rs["hadResult"] = "0"
                } else if ("平" == prs) {
                    rs["hadResult"] = "1"
                }
            } else {
                String[] p = rs["point"].toString().split(":")
                if (p[0] > p[1]) {
                    rs["hadResult"] = "3"
                } else if (p[0] < p[1]) {
                    rs["hadResult"] = "0"
                } else if (p[0] == p[1]) {
                    rs["hadResult"] = "1"
                }
            }
            if (poolrs.containsKey("hhad")) {
                //让球彩果
                def prs = poolrs.getJSONObject("hhad").getString("prs_name")
                if ("胜" == prs) {
                    rs["hhadResult"] = "3"
                } else if ("负" == prs) {
                    rs["hhadResult"] = "0"
                } else if ("平" == prs) {
                    rs["hhadResult"] = "1"
                }
            }
        }

    } else {
        throw new ServiceException("get_pool_rs fail, rs1=" + rs1)
    }

    return JSON.toJSONString(rs)
}

//println(get_data())
//println(get_rs("100928"))