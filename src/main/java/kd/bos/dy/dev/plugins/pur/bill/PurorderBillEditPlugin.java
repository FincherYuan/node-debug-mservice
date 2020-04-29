package kd.bos.dy.dev.plugins.pur.bill;

import java.util.EventObject;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.bill.IBillPlugin;
import kd.bos.bill.IBillView;
import kd.bos.entity.datamodel.IBillModel;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.events.BizDataEventArgs;
import kd.bos.form.IFormView;
import kd.bos.form.control.Control;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.events.CellClickEvent;
import kd.bos.form.control.events.CellClickListener;
import kd.bos.form.plugin.AbstractFormPlugin;

public class PurorderBillEditPlugin extends AbstractBillPlugIn implements CellClickListener   {
	
	private static String KEY_Materia = "materia";
	
	
	
	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
		EntryGrid  entryFrid = this.getView().getControl("entryentity");
		entryFrid.addCellClickListener(this);
	}
	
	
	
	@Override
	public void afterCreateNewData(EventObject e) {
		super.afterCreateNewData(e);
		//this.getModel().setValue("address", "address");
		IBillView  view = (IBillView )this.getView();
		view.getModel().setValue("address", "address222");
	}
	/**
	 * @Title: createNewData
	 * @Description: 重写方法的描述
	 * @author: Fincher JF.Yuan
	 * @date: 2020年4月17日 下午2:45:31
	 * @param: @param e
	 * @see kd.bos.entity.datamodel.events.IDataModelListener#createNewData(kd.bos.entity.datamodel.events.BizDataEventArgs)
	 */
	@Override
	public void createNewData(BizDataEventArgs e) {
		
		
		super.createNewData(e);
		//this.getModel().setValue("address", "address222");
	}



	@Override
	public void cellClick(CellClickEvent arg0) {
		if(arg0.getFieldKey().equals("materia")) {
			
		}
		System.out.println("1231");
		
	}



	@Override
	public void cellDoubleClick(CellClickEvent arg0) {
		System.out.println("1231");
		
	}

}
