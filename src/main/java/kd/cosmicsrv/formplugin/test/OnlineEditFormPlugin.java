package kd.cosmicsrv.formplugin.test;

import java.util.EventObject;

import kd.bos.entity.datamodel.events.IDataModelListener;
import kd.bos.form.IFormView;
import kd.bos.form.control.Control;
import kd.bos.form.control.IFrame;
import kd.bos.form.field.TextEdit;
import kd.bos.form.plugin.AbstractFormPlugin;

public class OnlineEditFormPlugin extends AbstractFormPlugin implements IDataModelListener{
	
	@Override
	public void initialize() {
		super.initialize();
	}
	
	
	
	@Override
	public void afterCreateNewData(EventObject e) {
		IFormView view = this.getView();
		/*TextEdit  control = view.getControl("billno");
		control.setSensitiveInfo(true);*/
		/*IFrame iframe = view.getControl("iframeap");
		iframe.setSrc("./isv/kded/test_md/cus_md/html/cus_md.html?version=2");*/
		
	}
	

}
