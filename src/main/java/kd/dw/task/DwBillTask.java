package kd.dw.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.exception.KDException;
import kd.bos.message.api.EmailInfo;
import kd.bos.orm.query.QFilter;
import kd.bos.schedule.executor.AbstractTask;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.message.MessageServiceHelper;

/**
 * @ClassName: DwBillTask
 * @Description: 调度计划
 * @author: Fincher JF.Yuan
 * @Company: none
 * @date: 2020年5月8日 下午11:11:38
 * @param: 
 */
public class DwBillTask extends AbstractTask {

	/**
	 * @Title: execute
	 * @Description: 执行任务
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月8日 下午11:11:53
	 * @param: @param arg0
	 * @param: @param arg1
	 * @param: @throws KDException
	 * @see kd.bos.schedule.api.Task#execute(kd.bos.context.RequestContext, java.util.Map)
	 */
	@Override
	public void execute(RequestContext arg0, Map<String, Object> arg1) throws KDException {
		System.out.println("123");
		QFilter qFilter = QFilter.of("billstatus = ?", "C");
		DynamicObjectCollection objs = QueryServiceHelper.query("dw_applybill", "*,entryentity.*", new QFilter[] {qFilter} );
		StringBuffer content = new StringBuffer("单据：广告费申请单，单据编码：");
		for(DynamicObject obj : objs) {
			String billno = obj.getString("billno");
			content.append(billno).append("\r\n");
			 
		}
		content.append("已审核，请知悉！");
		List<String> receiver = new ArrayList<String>();
		receiver.add("569750581@qq.com");
		EmailInfo emailInfo = new EmailInfo();
		emailInfo.setContent(content.toString());
		emailInfo.setReceiver(receiver);
		emailInfo.setTitle("审核通知");
		MessageServiceHelper.sendEmail(emailInfo);
		

	}

}
