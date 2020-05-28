package kd.dw.form.train.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment.WorkbookNotFoundException;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.exception.KDException;
import kd.bos.message.api.EmailInfo;
import kd.bos.orm.query.QFilter;
import kd.bos.schedule.executor.AbstractTask;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.message.MessageServiceHelper;
import kd.bos.servicehelper.workflow.MessageCenterServiceHelper;
import kd.bos.util.StringUtils;
import kd.bos.workflow.engine.msg.info.MessageInfo;

/**
 * @ClassName: DwBillTask
 * @Description: 调度计划
 */
public class MyTask extends AbstractTask {

	/**
	 * @Title: execute
	 * @Description: 执行任务
	 */
	@Override
	public void execute(RequestContext arg0, Map<String, Object> arg1) throws KDException {
		System.out.println("调度任务:广告费立项申请单调度任务开始");
		QFilter qFilter = QFilter.of("billstatus = ?", "C");
		//QFilter qFilter = QFilter.of("billstatus = ?", "F");
		DynamicObjectCollection objs = QueryServiceHelper.query("dw_apply", "*,applier.*,entryentity.*", new QFilter[] {qFilter} );
		
		StringBuffer billnoStr = new StringBuffer();//单据编码
		List<Long> applierList = new ArrayList<Long>();//申请人ID
		for(DynamicObject obj : objs) {
			BigDecimal userAmount = obj.get("entryentity.useamount")==null?BigDecimal.ZERO:(BigDecimal) obj.get("entryentity.useamount");
			if(userAmount.compareTo(BigDecimal.ZERO)>0) {
				BigDecimal useAmount = (BigDecimal) obj.get("entryentity.useamount");
				String billno = obj.getString("billno");//单据编码
				billnoStr.append(billno).append(",");	
				//DynamicObject applierObj = (DynamicObject) obj.get("applier.id");
				Long applier = (Long) obj.get("applier.id");
				//long applier = applierObj.getLong("id");
				applierList.add(applier);
			}

		}
		
		if(!StringUtils.isEmpty(billnoStr.toString())) {
			StringBuffer content = new StringBuffer("棕熊家电公司广告费报账系统温馨提醒：\r\n\r\n" + 
					"    本月您的广告费立项申请单"+billnoStr+"等仍未报销，请尽快登录系统进行报销。\r\n\r\n" +
					"    链接：http…\r\n\r\n" );
			
			List<String> receiver = new ArrayList<String>();
			QFilter qFilters = new QFilter("id","in", applierList);
			DynamicObjectCollection users = QueryServiceHelper.query("bos_user", "*", new QFilter[] {qFilters} );
			for(DynamicObject user :users) {
				Object maile = user.get("email");
				//邮件
				receiver.add(maile.toString());
				//receiver.add("642279203@qq.com");
				EmailInfo emailInfo = new EmailInfo();
				emailInfo.setContent(content.toString());
				emailInfo.setReceiver(receiver);
				emailInfo.setTitle("棕熊家电公司广告费报账系统温馨提醒");
				MessageServiceHelper.sendEmail(emailInfo);
				
				//消息中心
				MessageInfo messageInfo = new MessageInfo();
				messageInfo.setContent(content.toString());
				messageInfo.setUserIds(applierList);//申请人ID
				messageInfo.setMessageType("message");
				messageInfo.setTitle("棕熊家电公司广告费报账系统温馨提醒");
				MessageCenterServiceHelper.sendMessage(messageInfo);
			}
			
			
			
			System.out.println("调度任务:广告费立项申请单调度任务结束");
		}


	}

}
