package kd.dw.form.plugin;

import java.util.Map;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.RowDataEntity;
import kd.bos.entity.datamodel.events.AfterAddRowEventArgs;
import kd.bos.service.DispatchService;
import kd.bos.service.lookup.ServiceLookup;

/**
 * @ClassName: 广告费申请单插件
 * @Description: none
 * @author: Fincher JF.Yuan
 * @Company: none
 * @date: 2020年5月8日 下午4:17:48
 * @param: 
 */
public class DwApplyBillEditPlugin extends DwBillTplEditPlugin {
	
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

}
