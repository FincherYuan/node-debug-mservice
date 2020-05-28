package kd.dw.form.utils;

import java.util.HashMap;
import java.util.Map;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.resource.ResManager;
import kd.bos.dataentity.utils.ObjectUtils;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.form.IFormView;
import kd.bos.form.control.Image;
import kd.bos.form.control.Label;
import kd.bos.servicehelper.org.OrgServiceHelper;
import kd.fi.er.business.servicehelper.BaseCurrencyServiceHelper;
import kd.fi.er.business.servicehelper.CommonServiceHelper;
import kd.fi.er.business.servicehelper.CoreBaseBillServiceHelper;
import kd.fi.er.business.servicehelper.CreditLevelServiceHelper;
import org.apache.commons.lang.StringUtils;

public class ShowUserInfoUtils {
   public static void initUserInfo(IDataModel model, IFormView view) {
      String applierStr = null;
      String deptStr = null;
      String companyStr = null;
      String applierPicUrl = null;
      String positionStr = null;
      DynamicObject applier = (DynamicObject)model.getValue("applier");
      if(applier == null) {
         RequestContext dept = RequestContext.get();
         Long applierPositionText = Long.valueOf(dept.getUserId());
         Map costDept = CoreBaseBillServiceHelper.createNewData(applierPositionText);
         CoreBaseBillServiceHelper.initObjByMap(model, costDept);

         if(model.getValue("company") == null) {
            view.showErrorNotification(ResManager.loadKDString("申请人公司不能为空！！！", "ShowUserInfoUtils_1", "fi-er-formplugin", new Object[0]));
         }

         applier = (DynamicObject)model.getValue("applier");
      }

      DynamicObject dept1 = (DynamicObject)model.getValue("org");
      //Object applierPositionText1 = model.getValue("applierpositionstr");
      DynamicObject company = (DynamicObject)model.getValue("company");
      String telStr = (String)model.getValue("tel");
      if(applier != null) {
         applierStr = applier.getLocaleString("name").getLocaleValue();
         applierPicUrl = applier.getString("picturefield");
      }

     /* if(applierPositionText1 != null) {
         positionStr = ObjectUtils.nullSafeToString(applierPositionText1);
      }*/

      if(dept1 != null) {
         deptStr = dept1.getLocaleString("name").getLocaleValue();
      }

      if(company != null) {
    	  companyStr = company.getLocaleString("name").getLocaleValue();
      }

      ((Label)view.getControl("applier")).setText(applierStr);
      ((Label)view.getControl("company")).setText(companyStr);
      ((Label)view.getControl("dept")).setText( deptStr);
      ((Label)view.getControl("phone")).setText(telStr);
      
     // ((Label)view.getControl("applierpositionv")).setText(positionStr);
      Image applierPic = (Image)view.getControl("applierpic");
      applierPic.setUrl(applierPicUrl);
      if(applier != null) {
         String appilerIdStr = String.valueOf((Long)applier.getPkValue());
         String creditlevelStr = CreditLevelServiceHelper.getCreditLevelByTask(appilerIdStr);
         if(StringUtils.isEmpty(creditlevelStr)) {
            view.setVisible(Boolean.valueOf(false), new String[]{"creditlevel"});
         } else {
            ((Label)view.getControl("creditlevel")).setText(creditlevelStr);
         }
      }

   }

   public static void loadUserData(IDataModel model) {
      DynamicObject applier = (DynamicObject)model.getValue("applier");
      Long currentUserID = Long.valueOf("" + applier.getPkValue());
      Map billMap = CommonServiceHelper.getUserMap(currentUserID);
      model.setValue("applierpositionstr", billMap.get("applierpositionstr"));
   }
   
   public static Map<String, Object> createNewData(Long applierId) {
	      HashMap billMap = new HashMap();
	      if(applierId == null) {
	         return billMap;
	      } else {
	         Map userMap = CommonServiceHelper.getUserMap(applierId);
	         if(userMap == null) {
	            return billMap;
	         } else {
	            DynamicObject dept = (DynamicObject)userMap.get("org");
	            billMap.put("applier", applierId);
	            billMap.put("org", dept);
	            billMap.put("costdept", dept);
	            Long deptId = null;
	            Long companyId = null;
	            if(dept != null) {
	               deptId = (Long)dept.getPkValue();
	               companyId = initCompanyByDept(deptId);
	               billMap.put("company", companyId);
	              // billMap.put("costcompany", getAccountOrgId(deptId));
	            }

	            billMap.put("tel", userMap.get("tel"));
	            billMap.put("applierpositionstr", userMap.get("applierpositionstr"));
	           
	            Long currencyId = BaseCurrencyServiceHelper.getBaseCurrencyId(companyId);
	            if(currencyId != null) {
	               billMap.put("currency", currencyId);
	            }

	            billMap.put("billstatus", "A");
	            billMap.put("creator", CommonServiceHelper.getCurrentUserID());
	            return billMap;
	         }
	      }
	   }
   
   public static Long initCompanyByDept(Long deptId) {
	      Long companyId = Long.valueOf(0L);

	      try {
	         String company = "01";
	         String toOrgType = "01";
	         Long billTypeId = Long.valueOf(0L);
	         companyId = OrgServiceHelper.getToOrg(company, toOrgType, deptId, billTypeId);
	      } catch (Exception arg4) {
	         ;
	      }

	      if(companyId.longValue() == 0L) {
	         Map company1 = OrgServiceHelper.getCompanyfromOrg(deptId);
	         if(company1 != null) {
	            companyId = (Long)company1.get("id");
	         }
	      }

	      return companyId;
	   }
}