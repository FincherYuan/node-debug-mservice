package kd.cosmicsrv.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.MainEntityType;
import kd.bos.exception.KDException;
import kd.bos.orm.query.QFilter;
import kd.bos.schedule.executor.AbstractTask;
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.cosmicsrv.tools.KSMUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedbackListSyncTask extends AbstractTask {
    protected static Logger logger = Logger.getLogger(FeedbackListSyncTask.class);
    private String BILLSTATUS_DEFAULT_VALUE = "A";//单据状态，默认给个A，暂存状态
    private String CUSTNUMBER_VALUE = "KH20150526-00580";//客户号暂时写死
    private String PRODUCTNO_DEFAULT_VALUE = "8060007926";//产品序列号暂时写死

    @Override
    public void execute(RequestContext requestContext, Map<String, Object> map) throws KDException {
        List<DynamicObject> dynObjs = new ArrayList<DynamicObject>();
        MainEntityType type = EntityMetadataCache.getDataEntityType("kded_feedback_list");

        //先清空表数据,再做插入操作
        DeleteServiceHelper.delete("kded_feedback_list", new QFilter[]{});

        //从KSM查询客户号为KH20150526-00580，产品序列号为8060007926 下的所有提单
        JSONObject ret = KSMUtil.listFeedbackFromKSM(CUSTNUMBER_VALUE,PRODUCTNO_DEFAULT_VALUE);
        if (ret != null && ret.getIntValue("status") == 1 && ret.getJSONArray("data").size() > 0){
            logger.info("[EASSRV]开始同步KSM提单，提单数量[" + ret.getJSONArray("data").size() + "]");
            //遍历所有提单，判断提单编号，若本地库中有则更新，没有则新增
            JSONArray data = ret.getJSONArray("data");
            for(int i=0;i<data.size();i++){
                JSONObject retdata = (JSONObject) data.get(i);
                String fbknumKSM = retdata.getString("feedbackNumber");//提单号
                String backkeyKSM = retdata.getString("backkey");//主题
                String custName = this.getCustName(backkeyKSM);

                DynamicObject fbknew = new DynamicObject(type);
                fbknew.set("feedbacknumber", fbknumKSM);
                fbknew.set("inputtime", retdata.getString("inputTime"));
                fbknew.set("backkey", retdata.getString("backkey"));
                fbknew.set("dealstatusmean", retdata.getString("dealStatusMean"));
                fbknew.set("dealstatus", retdata.getString("dealStatus"));
                fbknew.set("finaldealname", retdata.getString("finaldealname"));
                fbknew.set("custname", custName);
                fbknew.set("billstatus", BILLSTATUS_DEFAULT_VALUE);
                fbknew.set("backtype", retdata.getString("backType"));
                dynObjs.add(fbknew);
            }
            if(dynObjs.size()>0){
//                logger.info("插入提单:" + dynObjs + "......");
                SaveServiceHelper.save(type,dynObjs.toArray());
            }
        }
    }

    /**
     * 截取标题字段，取出客户名称
     * 第二组方括号"【】"中的内容为客户名称
     * @param backkeyKSM
     * @return
     */
    private String getCustName(String backkeyKSM){
        Matcher slashMatcherStart = Pattern.compile("【").matcher(backkeyKSM);
        Matcher slashMatcherEnd = Pattern.compile("】").matcher(backkeyKSM);
        int x1 = 0;
        int x2 = 0;
        while(slashMatcherStart.find()){
            x1++;
            if(x1==2){
                break;
            }
        }
        while(slashMatcherEnd.find()){
            x2++;
            if(x2==2){
                break;
            }
        }
        int start = slashMatcherStart.start();//第二个"【"的坐标
        int end = slashMatcherEnd.start();//第二个"】"的坐标
        String custname = backkeyKSM.substring(start+1,end);
        return custname;
    }
}
