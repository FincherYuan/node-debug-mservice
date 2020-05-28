package kd.cosmicsrv.formplugin.test;

import java.util.EventObject;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.entity.datamodel.events.IDataModelListener;
import kd.bos.form.control.events.ItemClickEvent;

/**
 * @ClassName: TestEditPlugins
 * @Description: 测试插件类
 * @author: Fincher JF.Yuan
 * @Company: none
 * @date: 2020年5月20日 下午6:00:57
 * @param: 
 */
public class TestEditPlugins extends AbstractBillPlugIn implements IDataModelListener{
	
	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}
	
	/**
	 * @Title: afterCreateNewData
	 * @Description: 重写方法的描述
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月20日 下午6:03:05
	 * @param: @param e
	 * @see kd.bos.entity.datamodel.events.IDataModelListener#afterCreateNewData(java.util.EventObject)
	 */
	@Override
	public void afterCreateNewData(EventObject e) {
		
	}
	
	
	@Override
	public void itemClick(ItemClickEvent evt) {
		super.itemClick(evt);
	}

}
