package kd.cosmicsrv.tools;

import com.alibaba.fastjson.JSONObject;
import kd.cosmicsrv.tools.webservice.CloudFeedbackWebService;
import kd.cosmicsrv.tools.webservice.CloudFeedbackWebServiceProxy;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class KSMUtil {

    protected static Logger logger = Logger.getLogger(KSMUtil.class);

    private static final String examineCode = "5998498670981230171";
    public static final String CURRENTUSERNAME = "敬宗明";
    public static final String CURRENTUSERACCOUNT = "jimmy_jing";
    public static final String CALLSOURCE = "KSMforEAS";


    /**
     * 从KSM获取提单列表信息
     * @param custNumber
     * @param productNo
     * @return
     */
    public static JSONObject listFeedbackFromKSM(String custNumber,String productNo){
        JSONObject params = new JSONObject();
        params.put("systemSource", CALLSOURCE);
//        params.put("feedbackNumber", feedback.getFeedbackNumber());
        params.put("hash", DigestUtils.md5Hex(custNumber + examineCode));
        params.put("productno", productNo);
        params.put("custNumber", custNumber);
        params.put("cruuentUserName", CURRENTUSERNAME);
        params.put("cruuentUserAccount", CURRENTUSERACCOUNT);
        params.put("callSource", CALLSOURCE);
        logger.info(params.toJSONString());
        CloudFeedbackWebService p = new CloudFeedbackWebServiceProxy();
        JSONObject result = null;
        String ret = null;
        try {
            ret = p.listFeedback(params.toJSONString());
            result = JSONObject.parseObject(ret);
        } catch (RemoteException e) {
            logger.error(params.toJSONString() + ";" + e.getLocalizedMessage(), e);
        }
        return result;
    }

}
