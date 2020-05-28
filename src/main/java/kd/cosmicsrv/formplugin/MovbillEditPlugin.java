package kd.cosmicsrv.formplugin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.events.BizDataEventArgs;
import kd.bos.entity.datamodel.events.IDataModelListener;
import kd.bos.ext.form.control.Video;
import kd.bos.fileservice.FileItem;
import kd.bos.fileservice.FileService;
import kd.bos.fileservice.FileServiceFactory;
import kd.bos.form.ClientActions;
import kd.bos.form.IClientViewProxy;
import kd.bos.form.control.events.ItemClickEvent;


/**
 * @ClassName: MovbillEditPlugin
 * @Description: 视频预演单据插件
 * @author: Fincher JF.Yuan
 * @Company: none
 * @date: 2020年5月12日 下午3:23:35
 * @param: 
 */
public class MovbillEditPlugin extends AbstractBillPlugIn implements IDataModelListener{
	
	/**
	 * @Title: registerListener
	 * @Description: 注册监听
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月12日 下午3:26:18
	 * @param: @param e
	 * @see kd.bos.bill.AbstractBillPlugIn#registerListener(java.util.EventObject)
	 */
	@Override
	public void registerListener(EventObject e) {
		
		
		super.registerListener(e);
	}
	
	/**
	 * @Title: initialize
	 * @Description: 初始化
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月12日 下午3:26:14
	 * @param: 
	 * @see kd.bos.form.plugin.AbstractFormPlugin#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();
		
	}
	
	@Override
	public void itemClick(ItemClickEvent evt) {
		super.itemClick(evt);
		String itemKey = evt.getItemKey();
		if("btn_upload".equals(itemKey)){
			FileService fs=FileServiceFactory.getAttachmentFileService();
			String path = "/SYS/BASE/dev1212/test/vedio/cmmr05.mp4";
			FileItem fi = null;
			try {
				fi = new FileItem("cmmr05.mp4",path,new FileInputStream("P:/kingdee/debug_resource/static-file-service/webapp/vedio/cmmr05.mp4"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			fi.setCreateNewFileWhenExists(true);
			path= fs.upload(fi);

		}
		
	}
	
	
	@Override
	public void createNewData(BizDataEventArgs e) {
		System.out.println("24324");
		
	}
	
	@Override
	public void afterCreateNewData(EventObject e) {
		IDataModel model = this.getView().getModel();
		model.setValue("billno", "Test-20200512-1");
		/*Video video =this.getView().getControl("videoap");
		
		video.setSrc("http://www.kingdee.com/customs/video/cmmr05.mp4");*/
		
		Map<String, Object> dataMap = new HashMap<>();
	    dataMap.put("autoPlay", true); //设置为自动播放
	    dataMap.put("k", "videoap");
	    IClientViewProxy proxy = this.getView().getService(IClientViewProxy.class);
	    proxy.addAction(ClientActions.updateControlStates, dataMap);
		
	}
	
	
	
	
	
	
	
	

}
