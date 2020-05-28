package kd.cosmicsrv.tools;

import com.alibaba.fastjson.JSONObject;
import kd.bos.util.HttpUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class KSM4EASUtil {

    private static final Logger log = LoggerFactory.getLogger(KSM4EASUtil.class);

    private static final String SECURITY_KEY = "c46b3d6667322fc092452a94afe4b755";
    private static final String APP_NAME = "ierp";
    //private static final String URL_PREFIX = "https://easksm.kingdee.com/thirdapi";
    //private static final String URL_PREFIX = "http://127.0.0.1:8779/thirdapi";
    private static final String URL_CUSTINFO = "/level1/custinfo/detail";
    private static final String URL_SPECIALSUPPORT_SUBMIT = "/level1/specialSupport/add";


    public static JSONObject getCustInfo(String productNo) {



        JSONObject ret = null;
        try {
            Map<String, String> params = new HashMap<>();
            params.put("productNo", productNo);
            String url = generateApiGetUrl(CosmicsrvConfigUtil.getKSM4EASUrlPrefix() + URL_CUSTINFO, params);
            log.info(url);
            String result = HttpUtils.request(url);
            ret = JSONObject.parseObject(result);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return ret;
    }

    private static String generateParams(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        Set<String> keySet = params.keySet();
        for(String key : keySet) {
            try {
                builder.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        log.info(builder.toString());
        return builder.toString();
    }

    private static String generateApiGetUrl(String url, Map<String, String> params) {
        params.put("signature", generateApiSignature(params));
        String getUrl = url + "?" + generateParams(params);
        return getUrl;
    }

    private static String generateApiSignature(Map<String, String> params) {
        params.put("nonce", UUID.randomUUID().toString().replaceAll("-", ""));
        params.put("timestamp", System.currentTimeMillis() + "");
        params.put("appName", APP_NAME);
        Set<String> keySet = params.keySet();
        List<String> data = new ArrayList<>();
        for (String key : keySet) {
            data.add(params.get(key));
        }
        data.add(SECURITY_KEY);
        data.sort((o1, o2) -> o1.compareTo(o2));
        String join = StringUtils.join(data, "");
        String sign = DigestUtils.shaHex(join);
        return sign;
    }

    public static void main(String[] args) {
        JSONObject ret = getCustInfo("8060002373");
        System.out.println(ret.toJSONString());
    }

    public static JSONObject pushSpecialSupport2KSM4EAS(Map<String, String> params) {
        JSONObject ret = null;
        params.put("signature", generateApiSignature(params));
        Map<String, String> headers = new HashMap<>();
        //headers.put("Content-Type", "application/json;charset=UTF-8");
        String result = HttpUtils.post(CosmicsrvConfigUtil.getKSM4EASUrlPrefix() + URL_SPECIALSUPPORT_SUBMIT, headers, generateParams(params));
        log.info(result);
        if (StringUtils.isNotBlank(result)) {
            ret = JSONObject.parseObject(result);
        }
        return ret;
    }

}
