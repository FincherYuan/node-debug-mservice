/**
 * @Authod: zitao_su
 * @Description: Http工具类
 */
package kd.cosmicsrv.tools;

import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.util.HttpClientUtils;
import org.apache.log4j.Logger;

import java.util.Map;

public class HttpUtil {
    protected static Logger logger = Logger.getLogger(HttpUtil.class);

    public static Map<String, Object> get(String url) throws Exception {
        String res = HttpClientUtils.get(url);
        logger.info("httpgetRsp:" + res);
        return SerializationUtils.fromJsonString(res,Map.class);
    }

    public static Map<String, Object> get(String url, Map<String, Object> reqBody) throws Exception {
        String res = HttpClientUtils.get(url, reqBody);
        logger.info("httpgetRsp:" + res);
        return SerializationUtils.fromJsonString(res,Map.class);
    }
}
