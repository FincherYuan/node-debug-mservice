package kd.dw.form.plugin;

import java.math.BigDecimal;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.RowDataEntity;
import kd.bos.entity.datamodel.events.AfterAddRowEventArgs;
import kd.bos.entity.datamodel.events.BeforeAddRowEventArgs;
import kd.bos.entity.datamodel.events.ChangeData;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.FormShowParameter;
import kd.bos.form.IFormView;
import kd.bos.form.control.Control;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.events.CellClickEvent;
import kd.bos.form.control.events.CellClickListener;
import kd.bos.form.control.events.EntryGridBindDataEvent;
import kd.bos.form.control.events.EntryGridBindDataListener;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.control.events.ItemClickListener;
import kd.bos.form.field.AmountEdit;
import kd.bos.form.field.BasedataEdit;
import kd.bos.form.field.events.BeforeF7SelectEvent;
import kd.bos.form.field.events.BeforeF7SelectListener;
import kd.bos.list.ListShowParameter;
import kd.bos.mvc.bill.BillModel;
import kd.bos.orm.query.QFilter;
import kd.bos.service.DispatchService;
import kd.bos.service.lookup.ServiceLookup;
import kd.bos.servicehelper.basedata.BaseDataServiceHelper;
import kd.bos.util.StringUtils;

/**
 * @ClassName: 立项单插件
 * @Description: none
 * @author: Fincher JF.Yuan
 * @Company: none
 * @date: 2020年5月8日 下午2:38:27
 * @param: 
 */
public class DwASBillEditPlugin extends DwBillTplEditPlugin {
	
	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
		
		BasedataEdit advertisersCtrl = this.getControl("advertisers");
		advertisersCtrl.addBeforeF7SelectListener(new BeforeF7SelectListener() {
			
			@Override
			public void beforeF7Select(BeforeF7SelectEvent evt) {
				   
			        setFilterForAdvF7(evt);
				
			}
		});
		
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
		
		/*String key = property.getName();
		if("applyamount".equals(key)) {
			ChangeData[] changeSet = e.getChangeSet();
			ChangeData changeData = changeSet[0];
			int rowIndex = changeData.getRowIndex();
			BigDecimal newValue = (BigDecimal) changeData.getNewValue();
			this.getModel().setValue("approveamount", newValue,rowIndex);
			
			
		}*/
	}
	
	/**
	 * @Title: setFilterForAdvF7
	 * @Description: 设置F7过滤
	 * @author Fincher JF.Yuan
	 * @date: 2020年5月21日 下午12:54:14
	 * @param: @param evt 参数说明
	 * @return: void 返回类型
	 * @throws
	 */
	protected void setFilterForAdvF7(BeforeF7SelectEvent evt) {
		IFormView view = this.getView();
		IDataModel model = view.getModel();
		int row = evt.getRow();
		/*DynamicObjectCollection canal = (DynamicObjectCollection)model.getValue("mulcanal", 0);
		DynamicObject dynamicObject = canal.get(0);
		DynamicObject canal_id_ = (DynamicObject)dynamicObject.get("fbasedataid");
		String canal_id = canal_id_.get("id").toString();*/
		//QFilter orgF = BaseDataServiceHelper.getBaseDataFilter("canal.id", Long.valueOf(canal_id));
		
		//--------------------
		DynamicObject canal = (DynamicObject)model.getValue("canal", row);
		String canal_id = canal.get("id").toString();
		QFilter orgF = new QFilter("canalnew.id", "=", canal_id);
        ListShowParameter paramd = (ListShowParameter) evt.getFormShowParameter();
       // List<QFilter> filters = paramd.getListFilterParameter().getQFilters();
        //filters.add(orgF);
        paramd.getListFilterParameter().setFilter(orgF);
		
	}

	@Override
	public void itemClick(ItemClickEvent evt) {
		// TODO Auto-generated method stub
		super.itemClick(evt);
	}
	
	
	/**
	 * @Title: createNewCustomData
	 * @Description: 设置自定义数据
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月7日 下午2:45:14
	 * @param: @param entityNum
	 * @param: @param orgId
	 * @param: @param dataInfo
	 * @param: @param billMap1
	 * @param: @return
	 * @see kd.dw.form.plugin.DwBillTplEditPlugin#createNewCustomData(java.lang.String, java.lang.Long, kd.bos.dataentity.entity.DynamicObject, java.util.Map)
	 */
	@Override
	public Map<String, Object> createNewCustomData(String entityNum, Long orgId, DynamicObject dataInfo,
			Map<String, Object> billMap1) {
		Map<String, Object> billMap = super.createNewCustomData(entityNum, orgId, dataInfo, billMap1);
		IDataModel model = this.getModel();
		String billno =model.getValue("billno").toString();
		model.setValue("subbillno", billno+"-01", 0);
		
		return billMap;
		
	}
	
	/**
	 * @Title: getBillNoByBillCodeRule
	 * @Description: 重写方法的描述
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月7日 上午10:14:19
	 * @param: @param entityNum
	 * @param: @param orgId
	 * @param: @param dataInfo
	 * @param: @param billMap
	 * @param: @return
	 * @see kd.dw.form.plugin.DwBillTplEditPlugin#getBillNoByBillCodeRule(java.lang.String, java.lang.Long, kd.bos.dataentity.entity.DynamicObject, java.util.Map)
	 */
	@Override
	public Map<String, Object> getBillNoByBillCodeRule(String entityNum, Long orgId, DynamicObject dataInfo,
			Map<String, Object> billMap) {/*
		if(StringUtils.isEmpty(entityNum)) {
			entityNum = "dw_as";
		}*/
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
	
	
	@Override
	public void beforeAddRow(BeforeAddRowEventArgs e) {
		
		super.beforeAddRow(e);
	}
	
	
	

}
