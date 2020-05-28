package kd.cosmicsrv.formplugin;

import java.util.Date;

import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.cosmicsrv.tools.BizUtil;

public class InsapplyFormAdd extends AbstractFormPlugin{
	
	
	@Override
	public void propertyChanged(PropertyChangedArgs e) {
	
		String propertyName =  e.getProperty().getName();
		//判断项目申请时间
		if("startdate".equals(propertyName)) {
			Date startdate = (Date) this.getModel().getValue("startdate");
				//计算时间间隔
				int days = BizUtil.caclDayInterval(new Date(),startdate);
				if(days<2||days==2) {
					this.getView().showMessage("项目支持请提前3天申请");
					this.getModel().setValue("startdate","");
				}
			
		}
		if("enddate".equals(propertyName)) {
			Date startdate = (Date) this.getModel().getValue("startdate");
				//计算时间间隔
				if(startdate==null) {
					this.getModel().setValue("enddate","");
				}
			
		}
		
		super.propertyChanged(e);
	}

}
