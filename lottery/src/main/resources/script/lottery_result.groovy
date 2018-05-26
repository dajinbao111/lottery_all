import com.alibaba.fastjson.JSON
import org.wisestar.lottery.util.http.SimpleHttpClient

def get_data() {
    def rs = []

    def dlt = [:],qxc = [:],pls = [:],plw=[:]
    def url2 = "http://www.sporttery.cn/digitallottery/"
    String rs2 = SimpleHttpClient.current.get(url2).responseText

    def mm = rs2 =~ /'(超级大乐透|七星彩|排列3|排列5)':\{\s*'issue'\s*:\s*'(\d+)'\s*,\s*'date'\s*:\s*'(\d{4}-\d{2}-\d{2}\s*\d{2}:\d{2})'\s*\}/

    for (int i in 0..<4) {
        if ("超级大乐透".equals(mm[i][1])) {
            dlt["typeId"] = "1"
            dlt["phaseId"] = mm[i][2]
            dlt["dueTime"] = mm[i][3]
        } else if ("七星彩".equals(mm[i][1])) {
            qxc["typeId"] = "4"
            qxc["phaseId"] = mm[i][2]
            qxc["dueTime"] = mm[i][3]
        } else if ("排列3".equals(mm[i][1])) {
            pls["typeId"] = "2"
            pls["phaseId"] = mm[i][2]
            pls["dueTime"] = mm[i][3]
        } else if ("排列5".equals(mm[i][1])) {
            plw["typeId"] = "3"
            plw["phaseId"] = mm[i][2]
            plw["dueTime"] = mm[i][3]
        }
    }


    def url1 = "http://info.sporttery.cn/interface/lottery_num.php?action=new&_=" + System.currentTimeMillis()
    String rs1 = SimpleHttpClient.current.get(url1).responseText

    def m = rs1 =~ /(大乐透|七星彩|排列3|排列5)\|(\d{5,6})\|(\d{1,2},?){3,7}\|(2\d{3}-[0-3][0-9]-[0-3][0-9])\|(([0-9]+|[0-9]{1,3}(,[0-9]{3})*)(.[0-9]{1,2})?){0,1}\|/

    for (int i in 0..<4) {
        String d = m[i][0]
        String[] arr = d.split("\\|")

        if ("大乐透".equals(arr[0])) {
            dlt["lastRoundResult"] = arr[2]
            dlt["totalPoolBalance"] = arr[4]
        } else if ("七星彩".equals(arr[0])) {
            qxc["lastRoundResult"] = arr[2]
            qxc["totalPoolBalance"] = arr[4]
        } else if ("排列3".equals(arr[0])) {
            pls["lastRoundResult"] = arr[2]
        } else if ("排列5".equals(arr[0])) {
            plw["lastRoundResult"] = arr[2]
        }
    }
    rs.add(dlt)
    rs.add(qxc)
    rs.add(pls)
    rs.add(plw)

    return JSON.toJSONString(rs)
}