package kd.dw.rpt;

import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.events.PackageDataEvent;
import kd.bos.entity.report.ReportQueryParam;
import kd.bos.report.plugin.AbstractReportFormPlugin;

public class DWBillRptFormPlugin extends AbstractReportFormPlugin {
	
	@Override
	public void afterQuery(ReportQueryParam queryParam) {
		super.afterQuery(queryParam);
	}
	
	
	@Override
	public void processRowData(String gridPK, DynamicObjectCollection rowData, ReportQueryParam queryParam) {
		super.processRowData(gridPK, rowData, queryParam);
	}
	
	
	@Override
	public void packageData(PackageDataEvent packageDataEvent) {
		super.packageData(packageDataEvent);
	}
	
	

}
