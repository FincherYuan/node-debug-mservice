package kd.dw.form.train;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.dataentity.resource.ResManager;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.datamodel.IBillModel;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.events.BeforeImportDataEventArgs;
import kd.bos.entity.datamodel.events.BizDataEventArgs;
import kd.bos.entity.datamodel.events.InitImportDataEventArgs;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.entity.operate.Donothing;
import kd.bos.entity.property.BasedataProp;
import kd.bos.entity.property.MulBasedataProp;
import kd.bos.exception.ErrorCode;
import kd.bos.exception.KDException;
import kd.bos.form.ClientProperties;
import kd.bos.form.ConfirmCallBackListener;
import kd.bos.form.IFormView;
import kd.bos.form.IPageCache;
import kd.bos.form.MessageTypes;
import kd.bos.form.ShowType;
import kd.bos.form.StyleCss;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.events.CellClickEvent;
import kd.bos.form.control.events.CellClickListener;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.field.BasedataEdit;
import kd.bos.form.field.events.BeforeF7SelectEvent;
import kd.bos.form.field.events.BeforeF7SelectListener;
import kd.bos.list.ListShowParameter;
import kd.bos.mvc.bill.BillModel;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.basedata.BaseDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.bos.util.StringUtils;
import kd.fi.cas.business.errorcode.BillErrorCode;
import kd.fi.er.business.servicehelper.CommonServiceHelper;
import kd.fi.er.business.servicehelper.CoreBaseBillServiceHelper;
import kd.fi.gl.formplugin.FIGLErrorCode;

public class AppplyEditPlugin extends AbstractBillPlugIn {
	// 单据上多选基础资料字段标识(以用户为例),按实际业务修改
	private static String KEY_MULBASEDATA = "mulapprover";
	
	private static String KEY_ENTRYENTITY1 = "entryentity";
	
	
	
	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
		
		BasedataEdit advertisersCtrl = this.getControl("adv");// 广告商字段
		advertisersCtrl.addBeforeF7SelectListener(new BeforeF7SelectListener() {
			
			@Override
			public void beforeF7Select(BeforeF7SelectEvent evt) {
				   
			        setFilterForAdvF7(evt);
				
			}

			
		});
		
		// 侦听主菜单按钮点击事件
        this.addItemClickListeners("advcontoolbarap");
		
	}
	@Override
	public void propertyChanged(PropertyChangedArgs e) {
		super.propertyChanged(e);
		  String key = e.getProperty().getName();
		  if(key.equals("auditamount")) {
		   //判断核定金额是否大于申请金额，大于时给出提示
		   IDataModel model = this.getModel();
		   int rowIndex = e.getChangeSet()[0].getRowIndex();
		      DynamicObject row = model.getEntryRowEntity("entryentity", rowIndex);
		      BigDecimal hdamount = row.get("auditamount")==null?BigDecimal.ZERO:(BigDecimal)row.get("auditamount");//核定金额
		      BigDecimal reqamount = row.get("applyamount")==null?BigDecimal.ZERO:(BigDecimal)row.get("applyamount");//申请金额
		      if((reqamount.subtract(hdamount)).compareTo(BigDecimal.ZERO)<0) {
		    	  this.getView().showErrorNotification("核定金额不能大于申请金额！");
		    	  row.set("auditamount", 0);
		       
		      }
		      row.set("useamount", hdamount);// 核定金额等于可用余额
		  }
	}
	
	
	@Override
	public void createNewData(BizDataEventArgs e) {
		// TODO Auto-generated method stub
		super.createNewData(e);
	}
	
	
	
	
	@Override
	public void initImportData(InitImportDataEventArgs e) {
		// TODO Auto-generated method stub
		super.initImportData(e);
	}
	
	
	@Override
	public void beforeImportData(BeforeImportDataEventArgs e) {
		// TODO Auto-generated method stub
		super.beforeImportData(e);
	}
	
	@Override
	public void itemClick(ItemClickEvent evt) {
		super.itemClick(evt);
		evt.getParamsMap();
		if ("clearzero".equals(evt.getItemKey())){ // 立项清0
			IDataModel model=this.getModel();
			DynamicObjectCollection rows = null;

			// 方法二：自行从单据数据包中获取
			DynamicObject billObj = this.getModel().getDataEntity(true);
			rows = billObj.getDynamicObjectCollection(KEY_ENTRYENTITY1);

			//创建单据对象
			DynamicObject bill = BusinessDataServiceHelper.newDynamicObject("dw_apply");
			//设置单据属性
			String userId =RequestContext.get().getUserId();

			bill.set("billstatus", "C");
			bill.set("applytype", "B");
			bill.set("phone", model.getValue("phone"));
			bill.set("applier", model.getValue("applier"));
			
			// 取属性对象
		    MainEntityType mainType = this.getModel().getDataEntityType();
			// 多选基础资料：集合属性，与物理表格对应
			MulBasedataProp mulBasedataProp = (MulBasedataProp)mainType.findProperty("mulapprover");
			// 下级子实体：集合元素对应的实体对象，其下包含了普通基础资料属性对象
			DynamicObjectType subEntityType = (DynamicObjectType)mulBasedataProp.getDynamicCollectionItemPropertyType();
			DynamicObjectCollection mulpersons = (DynamicObjectCollection)model.getValue("mulapprover");
			BasedataProp basedataProp = (BasedataProp)subEntityType.getProperties().get("fbasedataid");
			List list=new ArrayList();
			for (DynamicObject mrow : mulpersons){
				DynamicObject basedataObj = (DynamicObject)basedataProp.getValue(mrow);
				Long basedataId = (Long) basedataObj.getPkValue();
				Long newBasedataId = basedataId;	// 主键
				// 根据主键，自行到数据库读取数据包
				DynamicObject newBasedataObj = BusinessDataServiceHelper.loadSingle(newBasedataId, "bos_user");
				// 给多选基础资料数据集合，增加新行，在新行上填写基础资料
				list.add(basedataId);
				
			}
			DynamicObjectCollection usersForSave = bill.getDynamicObjectCollection("mulapprover");
			DynamicObject tempUserInfo = null;
			DynamicObjectType usertype = EntityMetadataCache.getDataEntityType("bos_user");
			for (Object tempId : list.toArray()) {
				tempUserInfo = BusinessDataServiceHelper.loadSingle(tempId, usertype);
				DynamicObject Tempuser = new DynamicObject(usersForSave.getDynamicObjectType());
				Tempuser.set("fbasedataId", tempUserInfo);
				usersForSave.add(Tempuser);
			}
			//给多选基础资料 评估品类塞值
			
			bill.set("mulapprover", usersForSave);
			bill.set("org", model.getValue("org"));
			bill.set("dept", model.getValue("dept"));
			bill.set("applydate", model.getValue("applydate"));
			bill.set("creator",  model.getValue("creator"));
			bill.set("auditor",  model.getValue("auditor"));
			//获取单据体集合
			DynamicObjectCollection entrys = bill.getDynamicObjectCollection("entryentity");
			//获取单据体的Type
			DynamicObjectType type = entrys.getDynamicObjectType();
			//根据Type创建单据体对象
			DynamicObject entry = new DynamicObject(type);
			//设置单据体属性
			for (DynamicObject dynamicObject : rows) {
				entry.set("subno", dynamicObject.get("subno"));
				entry.set("canal", dynamicObject.get("canal"));
				entry.set("adv", dynamicObject.get("adv"));
				entry.set("product", dynamicObject.get("product"));
				entry.set("applyamount", dynamicObject.get("useamount"));
				entry.set("auditamount", dynamicObject.get("applyamount"));
				entry.set("usedamount", 0);
				entry.set("status","A" );
				
				//可用余额 状态
				dynamicObject.set("useamount", 0);
				dynamicObject.set("status", "C");
			}
			
			//添加到单据体集合
			entrys.add(entry);
			//保存
			SaveServiceHelper.saveOperate("dw_apply", new DynamicObject[] {bill});

			SaveServiceHelper.save(new DynamicObject[] {billObj});
			this.getView().updateView();
			
			}
	}
	
	
	
	/**
	 * @Title: setFilterForAdvF7
	 * @Description: 设置F7过滤
	 * @author Fincher JF.Yuan
	 * @date: 2020年5月21日 下午18:54:14
	 * @param: @param evt 参数说明
	 * @return: void 返回类型
	 * @throws
	 */
	protected void setFilterForAdvF7(BeforeF7SelectEvent evt) {
		IFormView view = this.getView();
		IDataModel model = view.getModel();
		
		DynamicObject canal = (DynamicObject)model.getValue("canal");// 投放方式字段名
		if(canal==null) {
			this.getView().showErrorNotification("请先选择投放方式！");
			evt.setCancel(true);
			return;
		}
		List<Long> ids = new ArrayList<>();
		Long canal_id = canal.getLong("id");
		ids.add(canal_id);
		
		// 设置过滤
		
		QFilter orgF = new QFilter("canal.fbasedataid.id" ,"in" ,ids);
		ListShowParameter paramd = (ListShowParameter) evt.getFormShowParameter();
        paramd.getListFilterParameter().setFilter(orgF);
		
	}
	
	
	/**
	 * @Title: afterCreateNewData
	 * @Description: 重写方法的描述
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月21日 下午5:12:06
	 * @param: @param e
	 * @see kd.bos.entity.datamodel.events.IDataModelListener#afterCreateNewData(java.util.EventObject)
	 */
	@Override
	public void afterCreateNewData(EventObject e) {
		super.afterCreateNewData(e);
		
		IDataModel model = this.getView().getModel();
		Long currentUserID = Long.valueOf(0L);
		IPageCache pageCache = (IPageCache)this.getView().getService(IPageCache.class);
		if(pageCache.get("consignorId") != null) {
			currentUserID = Long.valueOf(Long.parseLong(pageCache.get("consignorId")));
			pageCache.remove("consignorId");
		} else if(((IBillModel)model).isFromImport()) {
			currentUserID = (Long)model.getValue("applier");
		} else {
			RequestContext billMap = RequestContext.get();
			currentUserID = Long.valueOf(billMap.getUserId());
		}
		
		Map<String ,Object> billMap1 = CoreBaseBillServiceHelper.createNewData(currentUserID);
		
		CoreBaseBillServiceHelper.initObjByMap(model, billMap1);
		BillModel billModel = (BillModel)model;
		CommonServiceHelper.getCurrentUserID();
		RequestContext ctx = RequestContext.get();
        Long userId = Long.valueOf(ctx.getUserId());
		Map userMap = CommonServiceHelper.getUserMap(userId);
		String phone = (String)userMap.get("tel");
		billModel.setValue("phone", phone);
		billModel.setValue("dept", UserServiceHelper.getUserMainOrgId(userId));
		
		
	}
	
	
	@Override
	public void afterBindData(EventObject e) {
		super.afterBindData(e);
		//标准界面控件与数据绑定之后，初始化个性化控件绑定数据显示
		IDataModel model = this.getModel();//数据
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
	
	

}
