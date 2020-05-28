package kd.dw.form.plugin;

import java.math.BigDecimal;
import java.util.EventObject;
import java.util.Map;

import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.RowDataEntity;
import kd.bos.entity.datamodel.events.AfterAddRowEventArgs;
import kd.bos.entity.datamodel.events.ChangeData;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowFormHelper;
import kd.bos.form.ShowType;
import kd.bos.form.StyleCss;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.events.CellClickEvent;
import kd.bos.form.control.events.CellClickListener;
import kd.bos.form.control.events.ClickListener;
import kd.bos.form.control.events.EntryGridBindDataEvent;
import kd.bos.form.control.events.EntryGridBindDataListener;
import kd.bos.form.control.events.ItemClickListener;
import kd.bos.form.events.HyperLinkClickEvent;
import kd.bos.form.events.HyperLinkClickListener;
import kd.bos.form.field.TextEdit;
import kd.bos.orm.query.QFilter;
import kd.bos.service.DispatchService;
import kd.bos.service.lookup.ServiceLookup;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.taxc.common.showpage.PageShowCommon;

/**
 * @ClassName: 对账单编辑插件
 * @Description: none
 * @author: Fincher JF.Yuan
 * @Company: none
 * @date: 2020年5月8日 下午2:38:53
 * @param: 
 */
public class DwAccountBillEditPlugin extends DwBillTplEditPlugin {
	
	@Override
	public void initialize() {
		super.initialize();
		EntryGrid entryGrid = this.getControl("entryentity");
		entryGrid.addCellClickListener(new CellClickListener() {
			
			@Override
			public void cellDoubleClick(CellClickEvent arg0) {
				System.out.println("12321");
				
			}
			
			@Override
			public void cellClick(CellClickEvent arg0) {
				cellClickDo(arg0);
				
			}
		});
	}
	
	/**
	 * @Title: cellClickDo
	 * @Description: 点击事件处理
	 * @author Fincher JF.Yuan
	 * @date: 2020年5月8日 下午3:04:21
	 * @param: @param arg0 参数说明
	 * @return: void 返回类型
	 * @throws
	 */
	protected void cellClickDo(CellClickEvent arg0) {
		String fieldKey = arg0.getFieldKey();
		if("subapplynew".equals(fieldKey)) {
			IDataModel model = this.getModel();
			String subapplynew = (String)model.getValue("subapplynew");
			QFilter qFilter = QFilter.of("entryentity.subbillno=?", subapplynew);
			DynamicObjectCollection dw_as = QueryServiceHelper.query("dw_applybill", "*,entryentity.*", new QFilter[] {qFilter});
			DynamicObject dw_as_obj = dw_as.get(0);
			Long pkId =(Long) dw_as_obj.get("entryentity.id");
			BillShowParameter billShowParameter = new BillShowParameter();
			billShowParameter.setStatus(OperationStatus.VIEW);
			billShowParameter.setPkId(pkId);
			billShowParameter.setFormId("dw_subapply");
			billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
			StyleCss styleCss = new StyleCss();
			styleCss.setHeight("600");
			styleCss.setWidth("800");
			billShowParameter.getOpenStyle().setInlineStyleCss(styleCss);
		    billShowParameter.setParentFormId("dw_expense");
			billShowParameter.setParentPageId(this.getView().getPageId());		
			this.getView().showForm(billShowParameter);
			
		}
		
	}

	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
		this.addClickListeners(new String[]{ "subapplynew"});//申请子单号
		/*TextEdit subapplynew =this.getControl("subapplynew");
		subapplynew.addClickListener(new ClickListener() {
			@Override
			public void click(EventObject evt) {
				// TODO Auto-generated method stub
				ClickListener.super.click(evt);
			}
		});*/
	}
	
	
	
	
	
	@Override
	public Map<String, Object> createNewCustomData(String entityNum, Long orgId, DynamicObject dataInfo,
			Map<String, Object> billMap1) {
		Map<String, Object> createNewCustomData = super.createNewCustomData(entityNum, orgId, dataInfo, billMap1);
		IDataModel model = this.getModel();
		String billno =model.getValue("billno").toString();
		model.setValue("subbillno", billno+"-01", 0);
		return createNewCustomData;
	}
	
	@Override
	public Map<String, Object> getBillNoByBillCodeRule(String entityNum, Long orgId, DynamicObject dataInfo,
			Map<String, Object> billMap) {
		IDataModel model = this.getView().getModel();
		dataInfo = model.getDataEntity();
		//第二个参数是基础服务的应用标识
		DispatchService service = ServiceLookup.lookup(DispatchService.class, "base");
		String newNum = (String)service.invoke("kd.bos.service.ServiceFactory", "ICodeRuleService", "getNumber", entityNum, dataInfo, orgId);
		model.setValue("billno", newNum);
		return billMap;
	}
	
	/**
	 * @Title: afterAddRow
	 * @Description: 分录新增时，设置行数据
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月7日 下午1:40:38
	 * @param: @param e
	 * @see kd.bos.entity.datamodel.events.IDataModelChangeListener#afterAddRow(kd.bos.entity.datamodel.events.AfterAddRowEventArgs)
	 */
	@Override
	public void afterAddRow(AfterAddRowEventArgs e) {
		IDataModel model = this.getModel();
		DynamicObject dataEntity = model.getDataEntity();
		String billno = dataEntity.get("billno").toString();
		RowDataEntity[] rowDataEntities = e.getRowDataEntities();
		RowDataEntity rowDataEntity = rowDataEntities[0];
		int rowIndex = rowDataEntity.getRowIndex();
		String value = "";
		if(rowIndex +1< 10 ) {
			value ="0"+String.valueOf(rowIndex+1);
		}else {
			value =String.valueOf(rowIndex+1);
		}
		model.setValue("subbillno", billno+"-"+value, rowIndex);
		
		
		
		
	}
	
	/**
	 * @Title: propertyChanged
	 * @Description: 值改变事件
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月7日 下午7:56:46
	 * @param: @param e
	 * @see kd.bos.entity.datamodel.events.IDataModelChangeListener#propertyChanged(kd.bos.entity.datamodel.events.PropertyChangedArgs)
	 */
	@Override
	public void propertyChanged(PropertyChangedArgs e) {
		
		super.propertyChanged(e);
		IDataEntityProperty property = e.getProperty();
		/*
		String key = property.getName();
		if("applyamount".equals(key)) {
			ChangeData[] changeSet = e.getChangeSet();
			ChangeData changeData = changeSet[0];
			int rowIndex = changeData.getRowIndex();
			BigDecimal newValue = (BigDecimal) changeData.getNewValue();
			this.getModel().setValue("approveamount", newValue,rowIndex);
			
			
		}*/
	}
	
	
	
	
	

}
