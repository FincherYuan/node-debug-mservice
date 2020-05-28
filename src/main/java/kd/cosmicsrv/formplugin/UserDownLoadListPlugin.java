package kd.cosmicsrv.formplugin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.entity.property.EntryProp;
import kd.bos.form.control.Control;
import kd.bos.form.control.Label;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.list.BillList;
import kd.bos.list.IListView;
import kd.bos.list.ListCardView;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.servicehelper.BusinessDataServiceHelper;

/**
 * @author wyw
 * @下午1:57:08
 * @UserDownLoadListPlugin.java
 * @description 下载中心-用户查看下载界面
 */
public class UserDownLoadListPlugin extends AbstractListPlugin{
	
	private  String STR_url = "url";//url
	private  String STR_billlistap = "billlistap";//列表界面列表标识
	
	/* 
	 * @see kd.bos.form.plugin.AbstractFormPlugin#registerListener(java.util.EventObject)
	 */
	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
		Label deploytools_url = this.getView().getControl(STR_url);//url标签添加监听事件
		deploytools_url.addClickListener(this);
	}


	/* 
	 * @see kd.bos.form.plugin.AbstractFormPlugin#click(java.util.EventObject)
	 */
	@Override
	public void click(EventObject evt) {
		super.click(evt);
		
		Control control = (Control)evt.getSource();
        String key = control.getKey();
        if(key.equals(STR_url)) {
    		//查询点击分录的url字段信息，进行下载			
    		// 获取列表界面绑定的单据
    		String formId = ((IListView) this.getView()).getBillFormId();
    		// 获取单据列表控件
    		BillList billList = this.getControl(STR_billlistap);
    		// 获取选中行记录,注意:得到的结果是记录的主键ID的集合
    		ListSelectedRowCollection listSelectedRowCol = billList.getSelectedRows();
    		if (listSelectedRowCol != null && listSelectedRowCol.size() > 0) {
    			List<DynamicObject> listSelectedRowCol2 = new ArrayList<DynamicObject>();
    			DynamicObject tempRowData = null;
    			for (ListSelectedRow tempRowDataId : listSelectedRowCol) {
    				// 查询数据库得到当前行记录的完整结果
    				tempRowData = BusinessDataServiceHelper.loadSingle(tempRowDataId.getPrimaryKeyValue(), formId);
    				String url = (String) tempRowData.get(STR_url);
    				try {
    					this.getView().download(URLEncoder.encode(url,"utf-8"));
    					this.getView().download(url);
    				} catch (UnsupportedEncodingException e) {
    					e.printStackTrace();
    				}

    			}
    		}
		}

		
	}
	
}
