package kd.dw.form.plugin;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.EndOperationTransactionArgs;
import kd.bos.servicehelper.operation.SaveServiceHelper;

/**
 * @ClassName: DsASBillSaveAfterOperationPlugin
 * @Description: 保存操作插件
 * @author: Fincher JF.Yuan
 * @Company: none
 * @date: 2020年5月21日 下午1:03:28
 * @param: 
 */
public class DsASBillSaveAfterOperationPlugin extends AbstractOperationServicePlugIn {
	
	
	@Override
	public void endOperationTransaction(EndOperationTransactionArgs e) {
		super.endOperationTransaction(e);
		DynamicObject[] dataEntities2 = e.getDataEntities();
		for(DynamicObject obj :dataEntities2 ) {
			String billno =obj.getString("billno");
			DynamicObjectCollection entryentitys = obj.getDynamicObjectCollection("entryentity");
			for(int index =0 ;index <entryentitys.size();index++) {
				DynamicObject entryentity = entryentitys.get(index);
				String value = "";
				if(index +1< 10 ) {
					value ="0"+String.valueOf(index+1);
				}else {
					value =String.valueOf(index+1);
				}
				entryentity.set("subbillno", billno + value);
			}
			
		}
		SaveServiceHelper.save(dataEntities2);
	}
}
