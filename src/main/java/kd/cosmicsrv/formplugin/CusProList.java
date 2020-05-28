package kd.cosmicsrv.formplugin;

import java.util.ArrayList;
import java.util.EventObject;

import kd.bos.algo.DataSet;
import kd.bos.algo.Row;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.events.BeforeClosedEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.workflow.MessageCenterServiceHelper;
import kd.bos.workflow.engine.msg.info.MessageInfo;

public class CusProList extends AbstractListPlugin {

	@Override
	public void afterCreateNewData(EventObject e) {
		
		FormShowParameter vlue = this.getView().getFormShowParameter();
		String name = vlue.getCaption();
		Object customer = vlue.getCustomParam("customer");
		
		IDataModel model = this.getModel();
		model.setValue("customer", customer);
		 DataSet insapplyDataSet = null;
		 try {
			 QFilter customerQfilter = new QFilter("customer", QFilter.equals,
					 customer);// 结束日期大于等于月初
				QFilter[] dateQfilters = { customerQfilter };
				insapplyDataSet = QueryServiceHelper.queryDataSet("insapply","kded_insapply", "billno,desc,contractamount,state,helper,peopleday,helpstyle,startdate,enddate", dateQfilters,"billno");

				model.deleteEntryData("entryentity");
				int rowIndex = 0;
				for (Row row : insapplyDataSet) {
					model.batchCreateNewEntryRow("entryentity", 1);
					model.setValue("billno", row.get("billno"), rowIndex);
					model.setValue("prodesc", row.get("desc"), rowIndex);
					model.setValue("amount", row.get("contractamount"), rowIndex);
					model.setValue("state", row.get("state"), rowIndex);
					model.setValue("helper", row.get("helper"), rowIndex);
					model.setValue("peopleday", row.get("peopleday"), rowIndex);
					model.setValue("helpstyle", row.get("helpstyle"), rowIndex);
					model.setValue("startdate", row.get("startdate"), rowIndex);
					model.setValue("enddate", row.get("enddate"), rowIndex);
					rowIndex++;
				}
		 
		 } catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				if (insapplyDataSet != null)
					insapplyDataSet.close();
			}
		
	}



	
}
