package kd.bos.dy.dev.plugins.pur.openapi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import kd.bos.bill.IBillWebApiPlugin;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.dataentity.metadata.IDataEntityType;
import kd.bos.dataentity.metadata.clr.DataEntityPropertyCollection;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.LinkSetElement;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.PermissionControlType;
import kd.bos.entity.api.ApiResult;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.entity.property.EntryProp;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.isc.iscb.platform.core.util.DynamicObjectUtil;

public class BusinessorderbillWebApiPlugin implements IBillWebApiPlugin{
	
	
	/**
	 * @Title: doCustomService
	 * @Description: 重写方法的描述
	 * @author: Fincher JF.Yuan
	 * @date: 2020年4月28日 下午3:41:16
	 * @param: @param params
	 * @param: @return
	 * @see kd.bos.bill.IBillWebApiPlugin#doCustomService(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ApiResult doCustomService(Map<String, Object> params) {
		String entity ="yjf_businessorderbill";
		String entry = "yjf_businessorderbillentry";
		LinkedHashMap<String, Object> _params = (LinkedHashMap<String, Object>)params;
		String jsonString = JSON.toJSONString(_params);
		jsonString = jsonString.toLowerCase();
		JSONObject js= JSONObject.parseObject(jsonString,Feature.InitStringFieldAsEmpty);
		_params.put("org_id", "100000");
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
		String format = formater.format(new Date());
		long currentTimeMillis = System.currentTimeMillis();
		_params.put("billno", "API-" + format+"-" +currentTimeMillis);
		js = processData(js);
		JSONArray testitemsinfos = js.getJSONArray("testitemsinfos");
		DynamicObject obj = null;
		try {
			obj = DynamicObjectUtil.map2Object(entity, _params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DynamicObjectCollection entryentitys = (DynamicObjectCollection)obj.get("entryentity");
		
		/*MainEntityType parentEntityType = EntityMetadataCache.getDataEntityType(entity);
		DataEntityPropertyCollection properties = parentEntityType.getProperties();
		IDataEntityProperty iDataEntityProperty = properties.get(0);
		PermissionControlType permissionControlType = EntityMetadataCache.getPermissionControlType(entity);
		LinkSetElement linkSet = EntityMetadataCache.getLinkSet(entry);*/
		DynamicObject[] dataEntities = new DynamicObject[1];
		dataEntities[0] = obj;
		for(int i=0;i<testitemsinfos.size();i++) {
			JSONObject testitemsinfo = (JSONObject)testitemsinfos.get(i);
			Map<String,Object> _objEntrys = (Map<String, Object>) JSON.parse(testitemsinfo.toString());
			DynamicObject addNew = entryentitys.addNew();
			Set<String> keySet = _objEntrys.keySet();
			Iterator<String> iterator = keySet.iterator();
			while(iterator.hasNext()) {
				String key = iterator.next();
				IDataEntityType dataEntityType = addNew.getDataEntityType();
				IDataEntityProperty pro = dataEntityType.getProperties().get(key);
				if(pro !=null) {
					addNew.set(key, _objEntrys.get(key));
				}
				
				
			}
			//addNew.set("barcode", _objEntrys.get("barcode"));
		}
		OperationResult executeOperate = OperationServiceHelper.executeOperate("save", "yjf_businessorderbill", dataEntities,OperateOption.create());
		
		return ApiResult.success(executeOperate);
	}
	
	
	/**
	 * @Title: processData
	 * @Description: 处理数据
	 * @author Fincher JF.Yuan
	 * @date: 2020年4月29日 下午3:18:07
	 * @param: @param data
	 * @param: @return 参数说明
	 * @return: JSONObject 返回类型
	 * @throws
	 */
	public JSONObject processData(JSONObject data) {
		/*String laboratorynumber = data.getString("laboratorynumber");// 实验室 == 组织
		String lboratoryname = data.getString("lboratoryname");
		String accountcustomernumber = data.getString("accountcustomernumber");// 对账客户
		String accountcustomername = data.getString("accountcustomername");*/
		JSONArray testitemsinfos = data.getJSONArray("testitemsinfos");
		for(int i=0;i<testitemsinfos.size();i++) {
			JSONObject testitemsinfo =(JSONObject) testitemsinfos.get(i);
			String chargeitemnumber = testitemsinfo.getString("chargeitemnumber");
			String chargeitemname = testitemsinfo.getString("chargeitemname");
			QFilter qFilter = QFilter.of("number=? and name=?", chargeitemnumber,chargeitemname);
			boolean exists = QueryServiceHelper.exists("yjf_chargeitem", new QFilter[] {qFilter});
			if(!exists) {
				DynamicObject obj = null;
				Map<String, Object> _params = new HashMap<String, Object>();
				_params.put("name", chargeitemname);
				_params.put("number", chargeitemnumber);
				_params.put("createorg_id", "100000");
				_params.put("org_id", "100000");
				try {
					obj = DynamicObjectUtil.map2Object("yjf_chargeitem", _params);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String chargeitemid = addnewother("save","yjf_chargeitem",new DynamicObject[] {obj});
				testitemsinfo.put("chargeitemnumber_id", chargeitemid);
			}else {
				DynamicObjectCollection chargeitems = QueryServiceHelper.query("yjf_chargeitem", "id", new QFilter[] {qFilter});
				DynamicObject chargeitem = chargeitems.get(0);
				testitemsinfo.put("chargeitemnumber_id", chargeitem.get("id"));
			}
			/*String testitemnumber = testitemsinfo.getString("chargeitemnumber");
			String testitemname = testitemsinfo.getString("testitemname");*/
			
		}
		
		return data;
		
	}


	/**
	 * @Title: addnewChargeItem
	 * @Description: 描述这个方法的作用
	 * @author Fincher JF.Yuan
	 * @date: 2020年4月29日 下午4:04:23
	 * @param: @return 参数说明
	 * @return: String 返回类型
	 * @throws
	 */
	private String addnewother(String operationKey, String entityNumber, DynamicObject[] dataEntities) {
		 OperationResult executeOperate = OperationServiceHelper.executeOperate(operationKey, entityNumber, dataEntities,OperateOption.create());
		 List<Object> successPkIds = executeOperate.getSuccessPkIds();
		 String successPkId = (String)successPkIds.get(0);
		return successPkId;
		
	}
	
	

}
