package kd.cosmicsrv.formplugin;

import java.util.ArrayList;
import java.util.List;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.AfterOperationArgs;
import kd.bos.entity.plugin.args.BeginOperationTransactionArgs;
import kd.bos.entity.validate.ErrorLevel;
import kd.bos.entity.validate.ValidationErrorInfo;
import kd.bos.exception.KDBizException;
import kd.bos.list.IListView;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.workflow.unittest.util.BusinessDataHelper;

public class InsapplyListOperationEvent extends AbstractOperationServicePlugIn { 

		
		@Override
		public void afterExecuteOperationTransaction(AfterOperationArgs e) {
			
			List<DynamicObject> successObjs = new ArrayList<>();
			
			// 获取反审核成功的单据
			for(DynamicObject obj : e.getDataEntities()){
				String billno = (String) obj.get("billno");//把状态设为A 暂存
				QFilter qFilter = new QFilter("billNo", QCP.equals, billno);
    	        QFilter[] filters = new QFilter[]{qFilter};
    	        
    	        DynamicObject specialSupportBill = BusinessDataServiceHelper.loadSingle("kded_insapply","state",filters);
    	        specialSupportBill.set("state", "A");
    	        BusinessDataHelper.updateBusinessData(specialSupportBill);
			}
			
		}
}
