package kd.dw.form.train;

import java.util.ArrayList;
import java.util.List;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.IDataEntityType;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.property.BasedataProp;
import kd.bos.entity.property.MulBasedataProp;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.api.WorkflowElement;
import kd.bos.workflow.engine.extitf.IWorkflowPlugin;

/**
 * @ClassName: ApplyWorkFlowPlugin
 * @Description: 立项申请单工作流流进入节点插件
 * @author: Fincher JF.Yuan
 * @Company: none
 * @date: 2020年5月23日 上午12:29:53
 * @param: 
 */
public class ApplyWorkFlowPlugin implements IWorkflowPlugin{
	
	@Override
	public void notify(AgentExecution execution) {
		List<Long> currentApprovers = execution.getCurrentApprover();
		WorkflowElement currentFlowElement = execution.getCurrentFlowElement();
		String type = currentFlowElement.getType();
		String BusinessKey = execution.getBusinessKey();
		DynamicObject obj = BusinessDataServiceHelper.loadSingle(BusinessKey, "dw_apply");// 立项申请标识
		DynamicObjectCollection handlersSave = obj.getDynamicObjectCollection("handlers"); // 当前处理人标识
		if("YunzhijiaTask".equals(type)) {// 会审节点编码
			DynamicObjectCollection mulapprovers = obj.getDynamicObjectCollection("mulapprover"); // 会审人标识
			for(DynamicObject mulapprover :mulapprovers) {
				DynamicObject fbasedataid = (DynamicObject) mulapprover.get("fbasedataid");
				currentApprovers.add(fbasedataid.getLong("id"));
			}
			
		}else {
			currentApprovers = execution.getCurrentApprover();
		}
		
		
		DynamicObject temphandlers = null;
		handlersSave.clear();
		for(Long approversId :currentApprovers ) {
			temphandlers = BusinessDataServiceHelper.loadSingle(approversId, "bos_user");
			DynamicObject handler = new DynamicObject(handlersSave.getDynamicObjectType());
			handler.set("fbasedataId", temphandlers);
			handlersSave.add(handler);
			
		}
		SaveServiceHelper.save(new DynamicObject[] {obj} );
		
		
	}

}
