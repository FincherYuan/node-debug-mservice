package kd.cosmicsrv.tools;

import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CosmicsrvConfigUtil {

    private static final Logger log = LoggerFactory.getLogger(CosmicsrvConfigUtil.class);

    private static DistributeSessionlessCache disCache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("Cosmicsrv");
    private static boolean init = false;

    private static final String URL_KSM4EAS = "Cosmicsrv.url.ksm4eas";

    public CosmicsrvConfigUtil() {
    }

    public static String safeGetValue(String key) {
        if (StringUtils.isNotBlank(key)) {
            DynamicObject item = BusinessDataServiceHelper.loadSingle("cosmicsrv_config", "key, value", new QFilter[]{new QFilter("key", QFilter.equals, key)});
            if (item != null) {
                return item.getString("value");
            }
        }
        return null;
    }

    private static synchronized void lazyLoadConfigs() {
        DynamicObject[] configList = BusinessDataServiceHelper.load("cosmicsrv_config", "key,value", (QFilter[])null);
        if (configList != null) {
            for(int i = 0; i < configList.length; i++) {
                DynamicObject config = configList[i];
                String key = config.getString("key");
                String value = config.getString("value");
                disCache.put(getDBKey() + ".con." + key, value);
            }
        }
        init = true;
    }

    public static String getDBKey() {
        return RequestContext.get().getTenantId();
    }

    public static void refresh() {
        log.info("refresh cosmicsrv config");
        lazyLoadConfigs();
    }

    public static String getKSM4EASUrlPrefix() {
        return safeGetValue(URL_KSM4EAS);
    }

}
