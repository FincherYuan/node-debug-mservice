package kd.cosmicsrv.tools;

import kd.bos.bill.BillOperationStatus;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.control.Image;
import kd.bos.list.IListView;
import kd.bos.orm.ORM;
import kd.bos.orm.datamanager.DataManagerUtils;
import kd.bos.orm.query.QFilter;
import kd.bos.script.annotations.KSMethod;
import kd.bos.script.annotations.KSObject;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DBServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.org.OrgServiceHelper;
import kd.bos.servicehelper.org.OrgUnitServiceHelper;
import kd.bos.servicehelper.org.OrgViewType;
import kd.bos.servicehelper.user.UserServiceHelper;
import org.apache.log4j.Logger;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@KSObject
public class BizUtil {
    protected static Logger logger = Logger.getLogger(BizUtil.class);
    
    @KSMethod
    public static  Long getCurOrgId() {
    	//根据会话找到当前部门的机构id
        Long curUserID = Long.parseLong(RequestContext.get().getUserId());
        Long curOrgId = UserServiceHelper.getUserMainOrgId(curUserID);
        if (curOrgId == null) {
            curOrgId = RequestContext.get().getOrgId();

        }
        
        Long curSrvOrgID = BizUtil.getSysOrgIdFormSrvOrgList(curOrgId);
        return curSrvOrgID;
    }

    public static Set<Long> getOrgIds() {
        DynamicObject[] orgList = BusinessDataServiceHelper.load("cosmicsrv_org", "org", new QFilter[]{new QFilter("enable", QFilter.equals, 1)});
        Set<Long> orgIdList = new HashSet();
        for (int i = 0; i < orgList.length; i++) {
            orgIdList.add((long) orgList[i].getDynamicObject("org").getPkValue());
        }
        return orgIdList;
    }

    @KSMethod
    public static Long getSysOrgIdFormSrvOrgList(Long orgId) {
        Set<Long> orgIdList = getOrgIds();

        while (!orgIdList.contains(orgId)) {
            List<Long> superiorOrgIds = OrgUnitServiceHelper.getSuperiorOrgs(OrgViewType.Admin,orgId);
            if (superiorOrgIds.size() > 0) {
                orgId = superiorOrgIds.get(0);
            } else {
                orgId = null;
                break;
            }
        }

        return orgId;
    }

    @KSMethod
    public static void setBillOrg(IDataModel model, String key) {
        DynamicObject newOrg = null;
        Long newOrgId = null;
        DynamicObject curOrg = model.getDataEntity().getDynamicObject(key);
        if (null != curOrg) {
            Long curOrgId = (long) curOrg.getPkValue();
            newOrgId = BizUtil.getSysOrgIdFormSrvOrgList(curOrgId);
        }
        if (newOrgId == null) {
            Long curOrgId = RequestContext.get().getOrgId();
            newOrgId = BizUtil.getSysOrgIdFormSrvOrgList(curOrgId);
        }
        if (null != newOrgId) {
            newOrg = BusinessDataServiceHelper.loadSingle(newOrgId, "bos_org");
        }
        model.setValue(key, newOrg);
    }

    
    public static DynamicObject getSrvOrgFromSysOrg(DynamicObject SysOrg) {
        Long SysOrgId = (long) SysOrg.getPkValue();
        DynamicObject SrvOrg = BusinessDataServiceHelper.loadSingle("cosmicsrv_org", "manager,orgstgccustmgr,responsibleorg", new QFilter[]{new QFilter("org", QFilter.equals, SysOrgId)});
        return SrvOrg;
    }

    
    public static DynamicObject getSrvOrgInfoByOrgID(Long  orgId) {
        DynamicObject SrvOrg = BusinessDataServiceHelper.loadSingle("cosmicsrv_org", "number,manager,servicedirector", new QFilter[]{new QFilter("org", QFilter.equals, orgId)});
        return SrvOrg;
    }

    public static void setPeriod(IDataModel model, String key, String periodTypeStr) {
        DynamicObject periodType = BusinessDataServiceHelper.loadSingle("cosmicsrv_period_type", "", new QFilter[]{
                new QFilter("number", QFilter.equals, periodTypeStr)
        });
        DynamicObject period = BusinessDataServiceHelper.loadSingle("cosmicsrv_period", "", new QFilter[]{
                new QFilter("enable", QFilter.equals, 1),
                new QFilter("isdefault", QFilter.equals, true),
                new QFilter("periodtype", QFilter.equals, periodType.getPkValue())
        });
        model.setValue(key, period);
    }

    public static Set<Long> getPeriodListIds(String periodTypeStr, Boolean isOnlyEnable) {
        DynamicObject periodType = BusinessDataServiceHelper.loadSingle("cosmicsrv_period_type", "", new QFilter[]{
                new QFilter("number", QFilter.equals, periodTypeStr)
        });

        List<QFilter> periodQrArr = new ArrayList<>();
        periodQrArr.add(new QFilter("periodtype", QFilter.equals, periodType.getPkValue()));
        if (true == isOnlyEnable) {
            periodQrArr.add(new QFilter("enable", QFilter.equals, 1));
        }
        DynamicObject[] periodList = BusinessDataServiceHelper.load("cosmicsrv_period", "", periodQrArr.toArray(new QFilter[0]));
        Set<Long> periodListIds = new HashSet();
        for (int i = 0; i < periodList.length; i++) {
            periodListIds.add((long) periodList[i].getPkValue());
        }
        return periodListIds;
    }

    //获取指定部门下的所有用户
    public static Set<Long> getAllUsersOfSrvOrg(String[] deptNumber) {
    	Set<Long> userIdList = new HashSet<Long>();
    	DynamicObject cosmicsrvDept;
		for (int i = 0; i < deptNumber.length; i++) {
			cosmicsrvDept = BusinessDataServiceHelper.loadSingle("bos_org", "id,name",new QFilter[] { new QFilter("number", QFilter.equals, deptNumber[i]) });
			if (cosmicsrvDept != null) {
				userIdList.addAll(UserServiceHelper.getAllUsersOfOrg(cosmicsrvDept.getLong("id")));
			}
		}
		return userIdList;
	}


    //直接更新数据库中单据的某个属性值
    public static void update(Object[] pkValues, String EntityName, String prop,String value){
        DynamicObject[] datas = new DynamicObject[pkValues.length];
        for( int i = 0; i < pkValues.length; ++i) {
            datas[i] = BusinessDataServiceHelper.loadSingle(pkValues[i], EntityName ,prop);
        }

        Stream.of(datas).forEach((v) -> {
            v.set(prop, value);
        });
        SaveServiceHelper.save(datas);
    }

    public static boolean matcher(String str,String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static boolean isProductNo(String productNo) {
        String regex = "\\d{10}";
        return matcher(productNo, regex);
    }

    public static String format(){
        return format("yyyy-MM-dd HH:mm:ss",new Date());
    }
    public static String format(String format){
        return format(format,new Date());
    }
    public static String format(String format,Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    public static String format(int timestamp){
        long time = (long)timestamp * 1000;
        return format("yyyy-MM-dd HH:mm:ss",new Date(time));
    }

    /**
     * List转化成字符串组"A1,A2,..."
     * @param list
     * @return
     */
    public static String ListToString(List list) {
        StringBuffer strBuf = new StringBuffer();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) strBuf.append(",");
            strBuf.append(list.get(i));
        }
        return strBuf.toString();
    }
    
    //计算时间间隔
    @KSMethod
    public static int caclDayInterval(Date startDate, Date endDate) {
    	int interval = 0;
    	if (startDate != null && endDate != null) {
    		interval= (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 24 * 3600)) +1;
    	}
    	return interval;
	}
    
    //Image对象设置图片路径：用于脚本插件
    @KSMethod
    public static void setImage(Image imageCtrl, String url) {
    	imageCtrl.setUrl(url);
	}
    
    //获取用户主业务组织：用于脚本插件
    @KSMethod
    public static List<Long>  getUserMainOrgIds(List<Long> userIds) {
    	List<Long> orgIds= UserServiceHelper.getUserMainOrgIds(userIds); 
    	return orgIds;
	}
    
    //分录插入行：用于脚本插件
    @KSMethod
    public static int  insertEntryRow(IDataModel model,String entryKey,int rowKey) {
    	int newRowKey = model.insertEntryRow(entryKey, rowKey);
    	return newRowKey;
	}
    
    //列表获取选中行单据ID：用于脚本插件
    @KSMethod
    public static Object[] getSelectRowPKValues (IListView listView) {
    	ListSelectedRowCollection rorColl = listView.getSelectedRows();
    	Object[] pkValues = rorColl.getPrimaryKeyValues();
    	return pkValues;
	}
    
    //获取单据状态值：用于脚本插件
    @KSMethod
    public static int toBillStatusValue (BillOperationStatus status) {
    	return status.getValue();
	}

}
