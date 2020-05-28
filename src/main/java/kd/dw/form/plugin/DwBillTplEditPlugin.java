package kd.dw.form.plugin;

import java.awt.Color;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.coderule.api.CodeRuleInfo;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.resource.ResManager;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.datamodel.IBillModel;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.form.ClientProperties;
import kd.bos.form.ConfirmCallBackListener;
import kd.bos.form.IFormView;
import kd.bos.form.IPageCache;
import kd.bos.form.MessageTypes;
import kd.bos.form.ShowType;
import kd.bos.form.control.Control;
import kd.bos.form.control.Image;
import kd.bos.form.control.Label;
import kd.bos.form.control.events.ItemClickListener;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.events.MessageBoxClosedEvent;
import kd.bos.form.field.ComboEdit;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.mvc.bill.BillModel;
import kd.bos.mvc.list.ListView;
import kd.bos.service.DispatchService;
import kd.bos.service.lookup.ServiceLookup;
import kd.dw.form.utils.ShowUserInfoUtils;
import kd.fi.er.business.servicehelper.CoreBaseBillServiceHelper;
import kd.fi.er.business.servicehelper.CreditLevelServiceHelper;
import kd.fi.er.business.utils.ErCommonUtils;
import kd.fi.er.common.ShowPageUtils;
import kd.fi.er.common.utils.Er;
import kd.fi.er.model.FormModel;

/**
 * @autor wyw
 * @date 2020-05-04
 * @description  广告费单据模板基类
 */
public class DwBillTplEditPlugin extends AbstractBillPlugIn implements ItemClickListener{
	private static Log logger = LogFactory.getLog(DwBillTplEditPlugin.class);
	private Long orgId = null; // 受控组织ID，暂用为调用编码规则时使用

	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
		//注册控件监听事件
		this.addClickListeners(new String[]{ "changeapplier"});//申请人
		
	}
	
	/**
	 * @Title: setOrgId
	 * @Description: 设置受控组织ID
	 * @author Fincher JF.Yuan
	 * @date: 2020年5月7日 下午1:54:51
	 * @param: @param orgId 参数说明
	 * @return: void 返回类型
	 * @throws
	 */
	public void setOrgId(Long orgId) {
		this.orgId= orgId;
	}
	
	@Override
	public void afterCreateNewData(EventObject e) {
		super.afterCreateNewData(e);
		//在单据新增时，给字段赋值
		IDataModel model = this.getView().getModel();
		Long currentUserID = Long.valueOf(0L);
		IPageCache pageCache = (IPageCache)this.getView().getService(IPageCache.class);
		if(pageCache.get("consignorId") != null) {
			currentUserID = Long.valueOf(Long.parseLong(pageCache.get("consignorId")));
			pageCache.remove("consignorId");
		} else if(((IBillModel)model).isFromImport()) {
			currentUserID = (Long)model.getValue("applier_id");
		} else {
			RequestContext billMap = RequestContext.get();
			currentUserID = Long.valueOf(billMap.getUserId());
		}
		
		Map<String ,Object> billMap1 = CoreBaseBillServiceHelper.createNewData(currentUserID);
		
		CoreBaseBillServiceHelper.initObjByMap(model, billMap1);
		BillModel billModel = (BillModel)model;
		MainEntityType mainEntityType = billModel.getMainEntityType();
		DynamicObject dataEntity = model.getDataEntity();
		billMap1 = createNewCustomData(mainEntityType.getName(),this.orgId,dataEntity,billMap1);
		if(model.getValue("company") == null) {
			this.getView().showMessage(ResManager.loadKDString("申请人公司为空，请联系管理员设置。", "DwBillTplEditPlugin_1", "fi-er-formplugin", new Object[0]), MessageTypes.Permission, new ConfirmCallBackListener("ok", this));
		}

/*		if(model.getValue("company") != null && model.getValue("currency") == null) {
			this.getView().showErrorNotification(ResManager.loadKDString("请联系管理员设置本位币", "ErExpenseBaseEdit_3", "fi-er-formplugin", new Object[0]));
		}*/
		
		
	}
	
	/**
	 * @Title: createNewCustomData
	 * @Description: 新增其他自定义的数据
	 * @author Fincher JF.Yuan
	 * @date: 2020年5月7日 下午1:45:51
	 * @param: @param object
	 * @param: @param object2
	 * @param: @param object3
	 * @param: @param billMap1
	 * @param: @return 参数说明
	 * @return: Map<String,Object> 返回类型
	 * @throws
	 */
	public Map<String, Object> createNewCustomData(String entityNum,Long orgId ,DynamicObject dataInfo,
			Map<String, Object> billMap1) {
		
		billMap1 = getBillNoByBillCodeRule(entityNum,orgId,dataInfo,billMap1);
		
		return billMap1;
	}

	/**
	 * @Title: getBillNoByBillCodeRule
	 * @Description: 根据单据编码规则获取billno
	 * @author Fincher JF.Yuan
	 * @date: 2020年5月7日 下午1:43:30
	 * @param: @param entityNum 
	 * @param: @param orgId
	 * @param: @param dataInfo
	 * @param: @param billMap
	 * @param: @return 参数说明
	 * @return: Map<String,Object> 返回类型
	 * @throws
	 */
	public Map<String, Object> getBillNoByBillCodeRule(String entityNum,Long orgId ,DynamicObject dataInfo,Map<String, Object> billMap) {
		
		return null;
		
	}
	
	@Override
	public void afterBindData(EventObject e) {
		super.afterBindData(e);
		//标准界面控件与数据绑定之后，初始化个性化控件绑定数据显示
		IFormView view = this.getView();//界面
		IDataModel model = this.getModel();//数据
		ShowUserInfoUtils.initUserInfo(model, view);
		
		//根据数据初始化界面控件颜色
		String billstatus = (String) model.getValue("billstatus");
		if("A".equals(billstatus) ) {
			HashMap<String,Object> fieldMap = new HashMap<>();
			//设置前景色
			fieldMap.put(ClientProperties.ForeColor,"#2b87f3");
			//同步指定元数据到控件
			this.getView().updateControlMetadata("billstatus",fieldMap);

		}else if("B".equals(billstatus)) {
			HashMap<String,Object> fieldMap = new HashMap<>();
			//设置前景色
			fieldMap.put(ClientProperties.ForeColor,"#11ea73");
			//同步指定元数据到控件
			this.getView().updateControlMetadata("billstatus",fieldMap);
		}else if("C".equals(billstatus)) {
			HashMap<String,Object> fieldMap = new HashMap<>();
			//设置前景色X
			fieldMap.put(ClientProperties.ForeColor,"#ef5a1e");
			//同步指定元数据到控件
			this.getView().updateControlMetadata("billstatus",fieldMap);
		}else if("D".equals(billstatus)) {
			HashMap<String,Object> fieldMap = new HashMap<>();
			//设置前景色
			fieldMap.put(ClientProperties.ForeColor,"#edf10c");
			//同步指定元数据到控件
			this.getView().updateControlMetadata("billstatus",fieldMap);
		}else if("E".equals(billstatus)) {
			HashMap<String,Object> fieldMap = new HashMap<>();
			//设置前景色
			fieldMap.put(ClientProperties.ForeColor,"#736f6f");
			//同步指定元数据到控件
			this.getView().updateControlMetadata("billstatus",fieldMap);
		}else if("F".equals(billstatus)) {
			HashMap<String,Object> fieldMap = new HashMap<>();
			//设置前景色
			fieldMap.put(ClientProperties.ForeColor,"#ef0c0c");
			//同步指定元数据到控件
			this.getView().updateControlMetadata("billstatus",fieldMap);
		}
		
	}
	
	@Override
	public void click(EventObject evt) {
		super.click(evt);
		//点击事件
		String key = ((Control)evt.getSource()).getKey();
	      byte arg3 = -1;
	      switch(key.hashCode()) {
	      case 858035931:
	         if(key.equals("changeapplier")) {
	            arg3 = 0;
	         }
	      }

	      switch(arg3) {
	      case 0:
	         HashMap customParam = new HashMap();
	         customParam.put("showProps", "applierprops");
	         FormModel formModel = new FormModel("er_changeapplier", ResManager.loadKDString("个人信息", "DwBillTplEditPlugin_0", "fi-er-formplugin", new Object[0]), "1", true, customParam);
	         formModel.setShowType(ShowType.Modal);
	         ShowPageUtils.showPage(formModel, this);
	         break;
	      case 2:
	         DynamicObject applier = (DynamicObject)this.getModel().getValue("applier");
	         CreditLevelServiceHelper.showCreditFilesForm(this.getView(), String.valueOf(applier.getPkValue()));
	      }

	}
	
	@Override
	public void closedCallBack(ClosedCallBackEvent e) {
		super.closedCallBack(e);
		//callBack事件
		String actionId = e.getActionId();
		IDataModel model = this.getModel();
		Map returnData;
		if ("er_changeapplier".equals(actionId)) {
			logger.info("申请人变更——选择返回");
			if (e.getReturnData() != null) {
				returnData = (Map) e.getReturnData();
				long deptId = Long.valueOf(returnData.get("deptId").toString()).longValue();
				String userId = returnData.get("consignorId").toString();
				String newtel = returnData.get("newtel").toString();
				DynamicObject applier = (DynamicObject) model.getValue("applier");
				String applierId = applier.getPkValue().toString();
				String returnDataJson = Er.objToJson(returnData);
				logger.info(String.format("申请人变更——选择返回数据: %s", new Object[]{returnDataJson}));
				String formId;
				if (!applierId.equals(userId)) {
					model.setValue("applier", Long.valueOf(Long.parseLong(userId)));
					DynamicObject propControl = (DynamicObject) model.getValue("applier");
					formId = propControl.getString("picturefield");
					Image applierPic = (Image) this.getControl("applierpic");
					applierPic.setUrl(formId);
					model.setValue("tel", newtel);
					IPageCache pageCache = (IPageCache) this.getView().getService(IPageCache.class);
					pageCache.put("consignorId", userId);
				}

				model.setValue("applierpositionstr", (String) returnData.get("newPositionStr"));
				Label propControl1 = (Label) this.getControl("applierpositionv");
				propControl1.setText((String) returnData.get("newPositionStr"));
				model.setValue("company", Long.valueOf(deptId));
				model.setValue("org", ErCommonUtils.getObjectValue(returnData.get("newCompanyId")));
				
				//formId = (String) model.getValue("formid");
				

			}
		}
		
	}
	
	@Override
	public void confirmCallBack(MessageBoxClosedEvent messageBoxClosedEvent) {
		super.confirmCallBack(messageBoxClosedEvent);
		if ("ok".equalsIgnoreCase(messageBoxClosedEvent.getCallBackId())) {
			if (!(this.getView() instanceof ListView)) {
				this.getModel().setDataChanged(false);
			}

			this.getView().close();
		}
	}
}
