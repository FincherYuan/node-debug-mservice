package kd.cosmicsrv.wfplugin;


import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.component.approvalrecord.IApprovalRecordItem;
import kd.bos.workflow.engine.extitf.IWorkflowPlugin;
import kd.bos.workflow.unittest.util.BusinessDataHelper;

public class ApprovalThrWFPlugin implements IWorkflowPlugin {
	
	
    /**
     * 监听器使用，离开节点后修改单据状态为：审核通过
     * @param execution
     */
    @Override
    public void notify(AgentExecution execution) {
    	
   	 // 获取用户id
        long userId = UserServiceHelper.getCurrentUserId();
    	
        String businessKey = execution.getBusinessKey();
        DynamicObject specialSupportBill = BusinessDataServiceHelper.loadSingle(businessKey, "kded_insapply");
        specialSupportBill.set("state", "D");
        specialSupportBill.set("billstatus", "C");
        specialSupportBill.set("auditor", userId);
        BusinessDataHelper.updateBusinessData(specialSupportBill);
    }
  
}
