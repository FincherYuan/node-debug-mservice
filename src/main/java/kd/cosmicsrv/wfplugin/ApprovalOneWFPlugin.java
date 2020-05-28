package kd.cosmicsrv.wfplugin;


import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.engine.extitf.IWorkflowPlugin;
import kd.bos.workflow.unittest.util.BusinessDataHelper;

public class ApprovalOneWFPlugin implements IWorkflowPlugin {
	

    /**
     * 监听器使用，离开节点后修改单据状态为：已提交
     * @param execution
     */
    @Override
    public void notify(AgentExecution execution) {
        String businessKey = execution.getBusinessKey();
        DynamicObject specialSupportBill = BusinessDataServiceHelper.loadSingle(businessKey, "kded_insapply");
        specialSupportBill.set("state", "B");
        BusinessDataHelper.updateBusinessData(specialSupportBill);
    } 
  
}
