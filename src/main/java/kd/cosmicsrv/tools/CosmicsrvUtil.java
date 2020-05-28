/**
 * @Authod: zitao_su
 * @Description: 通用工具类
 */
package kd.cosmicsrv.tools;

import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;

public class CosmicsrvUtil {
    protected static Logger logger = Logger.getLogger(CosmicsrvUtil.class);

    public static String MD5(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(s.getBytes("utf-8"));
        return bytesToHex(bytes);
    }

    public static String bytesToHex(byte[] bytes) {
        BigInteger bigInt = new BigInteger(1, bytes);
        return bigInt.toString(16);
    }
}
