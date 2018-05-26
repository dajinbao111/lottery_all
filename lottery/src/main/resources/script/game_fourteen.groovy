import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.wisestar.lottery.exception.ServiceException
import org.wisestar.lottery.util.http.SimpleHttpClient

def get_num(num = "") {
    def rs = [:]
    def url1 = "http://i.sporttery.cn/wap/fb_lottery/fb_lottery_nums?key=wilo&num=" + num + "&f_callback=getNumBack&_=" + System.currentTimeMillis()
    String rs1 = SimpleHttpClient.current.get(url1).responseText
    if (rs1.contains("getNumBack")) {
        rs1 = rs1.substring(11, rs1.size() - 2)
        JSONObject data = JSON.parseObject(rs1)
        JSONObject result = data.getJSONObject("result")

        rs["num"] = result.getString("num")
        rs["start"] = result.getString("start")
        rs["end"] = result.getString("end")
        rs["last"] = result.getString("last")
        rs["next"] = result.getDate("next")
        rs["prize"] = result.getString("prize")
        rs["format"] = "yyyy/MM/dd HH:mm"

    } else {
        throw new ServiceException("getNumBack fail, rs1=" + rs1)
    }
    return JSON.toJSONString(rs)
}

def get_data(num = "") {
    def rs = []

    def url1 = "http://i.sporttery.cn/wap/fb_lottery/fb_lottery_nums?key=wilo&num=" + num + "&f_callback=getNumBack&_=" + System.currentTimeMillis()
    String rs1 = SimpleHttpClient.current.get(url1).responseText
    def com = [:]
    if (rs1.contains("getNumBack")) {
        rs1 = rs1.substring(11, rs1.size() - 2)
        JSONObject data = JSON.parseObject(rs1)
        JSONObject result = data.getJSONObject("result")

        com["phaseId"] = result.getString("num")
        com["startPostTime"] = result.getString("start")
        com["endPostTime"] = result.getString("end")
        com["format"] = "yyyy/MM/dd HH:mm"
    } else {
        throw new ServiceException("getNumBack fail, rs1=" + rs1)
    }

    def url2 = "http://i.sporttery.cn/wap/fb_lottery/fb_lottery_match?key=wilo&num=" + num + "&f_callback=getDataBack&_=" + System.currentTimeMillis()
    String rs2 = SimpleHttpClient.current.get(url2).responseText

    if (rs2.contains("getDataBack")) {
        rs2 = rs2.substring(12, rs2.size() - 2)
        JSONObject data = JSON.parseObject(rs2)

        JSONObject result = data.getJSONObject("result")
        //按key排序
        String[] keys = result.keySet().toArray()
        keys.sort()
        def index = [:]
        for (int i = 0; i < keys.length; i++) {
            index[keys[i]] = i + 1
        }

        for (Map.Entry entry : result.entrySet()) {
            def item = [:]

            JSONObject entryValue = entry.getValue()
            item["serialId"] = index[entry.getKey()]
            item["phaseId"] = com["phaseId"]
            item["startPostTime"] = com["startPostTime"]
            item["endPostTime"] = com["endPostTime"]
            item["format"] = com["format"]
            item["gameId"] = entry.getKey()
            item["gameType"] = entryValue.getString("league")
            item["hostTeam"] = entryValue.getString("h_cn")
            item["guestTeam"] = entryValue.getString("a_cn")
            item["startTime"] = entryValue.getString("date") + " " + entryValue.getString("time")
            item["winRatio"] = entryValue.getString("h")
            item["drawRatio"] = entryValue.getString("d")
            item["loseRatio"] = entryValue.getString("a")
            item["result"] = entryValue.getString("result")
            item["point"] = entryValue.getString("full")

            rs.add(item)
        }

    } else {
        throw new ServiceException("getDataBack fail, rs2=" + rs2)
    }

    return JSON.toJSONString(rs)
}

def get_result(num) {
    def rs = [:]
    def url = "http://www.okooo.com/zucai/" + num
    String content = SimpleHttpClient.current.get(url).responseText
    Document doc = Jsoup.parse(content)

    Elements els = doc.select(".qihao_infor")
    def cm = els.text() =~ /(\d+)/
    rs["phaseId"] = cm[0][1]

    def result = ""
    els = doc.select(".alltrObj .td8")
    for (int i in 0..<els.size()) {
        def r = els.get(i).text()
        if (r != "3" && r != "1" && r != "0") {
            r = "-"
        }
        result += r + ","
    }
    rs["result"] = result.substring(0, result.length() - 1)

    els = doc.select(".history_info #SaleInfoFirstWager")
    rs["prize1"] = els.get(0).text()
    rs["prize2"] = els.get(1).text()

    url = "http://www.okooo.com/zucai/ren9/"+ num
    content = SimpleHttpClient.current.get(url).responseText
    doc = Jsoup.parse(content)

    els = doc.select("#LotteryNo")
    els = doc.select(".history_info #SaleInfoFirstWager")
    rs["ren9"] = els.text()

    return JSON.toJSONString(rs)
}

