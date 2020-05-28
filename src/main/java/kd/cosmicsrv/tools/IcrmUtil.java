/**
 * @Authod: zitao_su
 * @Description: iCRM接口封装
 */
package kd.cosmicsrv.tools;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class IcrmUtil {
    protected static Logger logger = Logger.getLogger(IcrmUtil.class);
    private static String secret = "5998498670981230171";

    /**
     * 根据产品序列号获取客户信息
     *
     * @param productNo 产品序列号
     * @return 客户信息
     * @throws Exception
     */
    public static Map<String, Object> getCustInfoByProductNo(String productNo) throws Exception {
        System.out.println(productNo);
        String hash = DigestUtils.md5Hex(productNo + secret);
        String url = "http://icrm.kingdee.com:81/service/icrmServiceInterface2!getCustInfoForKSM.action";
        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("productNo", productNo);
        reqBody.put("hash", hash);
        Map<String, Object> res = HttpUtil.get(url, reqBody);
        if (res.get("status").toString().equals("1")) {
            return (Map<String, Object>) res.get("data");
        } else {
            throw new Exception(res.get("msg").toString());
        }
    } // 8060012272

    /**
     * 根据客户号获取客户信息（本方法目前不可用）
     *
     * @param custNumber 客户号
     * @return 客户信息
     * @throws Exception
     */
    public static Map<String, Object> getCustInfoByCustNumber(String custNumber) throws Exception {
        String hash = DigestUtils.md5Hex(custNumber + secret);
        String url = "http://icrm.kingdee.com:81/service/icrmServiceInterface2!getCustInfoForKSM.action";
        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("custNumber", custNumber);
        reqBody.put("hash", hash);
        Map<String, Object> res = HttpUtil.get(url, reqBody);
        if (res.get("status").toString().equals("1")) {
            return (Map<String, Object>) res.get("data");
        } else {
            throw new Exception(res.get("msg").toString());
        }
    } // KH20110727-00872
}
