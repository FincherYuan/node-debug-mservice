package kd.cosmicsrv.formplugin;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.property.EntryProp;
import kd.bos.form.control.AttachmentPanel;
import kd.bos.form.control.events.AttachmentMarkEvent;
import kd.bos.form.control.events.AttachmentMarkListener;
import kd.bos.form.control.events.UploadEvent;
import kd.bos.form.control.events.UploadListener;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.bos.url.UrlService;

/**
 * @author wyw
 * @下午2:48:08
 * @DownLoadEditPlugin.java
 * @description 下载中心-管理员编辑界面 附件上传之后将附件信息存入附件分录，然后再用户查看界面从该分录加载相关附件信息
 */
public class DownLoadEditPlugin extends AbstractFormPlugin implements UploadListener, AttachmentMarkListener{

	private String STR_entry ="entryentity";//单据体标识
	private String STR_seq ="seq";//seq
	private  String STR_name = "filename";//附件名称
	private  String STR_size = "size";//文件大小
	private  String STR_type = "type";//文件类型
	private  String STR_uid = "uid";//uid
	private  String STR_url = "url";//url
	private  String STR_creator = "filecreator";//创建人
	private  String STR_lastModified = "lastmodified";//最后修改时间
	private  String STR_status = "entrystatus";//附件状态
	private  String STR_endProgress = "endprogress";//endProgress	
	private  String STR_uploadtor = "uploadtor";//上传人
	private  String STR_uploadDate = "uploadDate";//上传时间
	
	/* 
	 * 插件监听附件上传、删除、添加备注事件
	 */
	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
		//附件面板添加监听
	    AttachmentPanel panel = this.getView().getControl("attachmentpanel");    
	    panel.addMarkListener(this);
	    panel.addUploadListener(this);
	}

	@Override
	public void afterUpload(UploadEvent evt) {
	    // 文件上传后进入该方法
		UploadListener.super.afterUpload(evt);
		//文件上传成功之后将数据写入分录附件信息
		  evt.getNames();
		  String userName = RequestContext.get().getUserName();//上传人
		  String loginTime = RequestContext.get().getLoginTime();	//上传时间	  
		  
		  Object[] urls = (Object[]) evt.getUrls();
		  
		  int length = urls.length;	  	  		  
		  for(int i=0;i<urls.length;i++) {
			  HashMap  map= (HashMap) urls[i];//获取上传的附件信息
			  
			  String name = (String) map.get("name");//附件名称
			  int size = (Integer)map.get("size");//文件大小
			  BigDecimal bigSize = new BigDecimal(0);
			  BigDecimal number1024 = new BigDecimal(1024);
			  bigSize=BigDecimal.valueOf((int)size);			   
			  String fileM ="";
			  
			  if(size<1048576) {
				  bigSize = bigSize.divide(number1024).setScale(2, BigDecimal.ROUND_HALF_UP);
				  bigSize.setScale(2, BigDecimal.ROUND_HALF_UP);
				  fileM = bigSize+"KB";	
			  }else {			
				  DecimalFormat df=new DecimalFormat("0.00");//设置保留位数
				  fileM = df.format((float)(size/1024)/1024)+"M";	
			  }
			  				  
			  String type = (String) map.get("type");//文件类型
			  String uid = (String) map.get("uid");//uid
			  String url =  (String) map.get("url");//url
			  LocaleString LocalCreator = (LocaleString) map.get("creator");//创建人
			  String creator = LocalCreator.getLocaleValue_zh_CN();
			  Long lastModified = (Long) map.get("lastModified");//最后修改时间
			  Date date = new Date(lastModified);
			  String status = (String) map.get("status");//附件状态
			  Boolean endProgress = (Boolean) map.get("endProgress");//endProgress
			  
			  //上传成功后将附件信息反写到分录,创建一行，返回行号
			  int rowIndex = this.getModel().createNewEntryRow("entryentity");
			  this.getModel().setValue(STR_name, name, rowIndex);
			  this.getModel().setValue(STR_size, fileM, rowIndex);
			  this.getModel().setValue(STR_type, type, rowIndex);
			  this.getModel().setValue(STR_uid, uid, rowIndex);
			  this.getModel().setValue(STR_url, url, rowIndex);
			  this.getModel().setValue(STR_creator, creator, rowIndex);
			  this.getModel().setValue(STR_lastModified, date, rowIndex);
			  this.getModel().setValue(STR_status, status, rowIndex);
			  this.getModel().setValue(STR_endProgress, endProgress.toString(), rowIndex);
			  this.getModel().setValue(STR_uploadtor, userName.toString(), rowIndex);
			  this.getModel().setValue(STR_uploadDate,loginTime, rowIndex);

		  }
		  

	  }


	@Override
	public void afterRemove(UploadEvent evt) {
		UploadListener.super.afterRemove(evt);
		// 删除附件之后进入该方法,附件删除之后根据uid将附件分录信息删除
		Object[] urls = (Object[]) evt.getUrls();
		int length = urls.length;
		
		//------获取分录数据
		DynamicObject billObj = this.getModel().getDataEntity(true);//单据object
		MainEntityType mainType = this.getModel().getDataEntityType();
		EntryProp entryProp = (EntryProp)mainType.getProperties().get(STR_entry);
		DynamicObjectCollection rows =  (DynamicObjectCollection)entryProp.getValue(billObj);//分录Object
		//将分录的uid和seq放入同一个map中，以供与删除的附件数据进行匹配，进行对应行删除
		HashMap uidMap = new HashMap<>();
		for(int i=0;i<rows.size();i++) {
			DynamicObject  row= (DynamicObject) rows.get(i);//分录数据
			String uid = (String) row.get(STR_uid);//uid
			Integer seq = (Integer) row.get(STR_seq);//seq			
			uidMap.put(uid, seq);
		}
				
		//--------根据uid匹配分录，删除对应的附件信息数据		  
		for(int i=0;i<urls.length;i++) {
			HashMap  map= (HashMap) urls[i];//获取上传的附件信息
			String uid = (String) map.get(STR_uid);//uid
			Integer seq = (Integer) uidMap.get(uid);
			this.getModel().deleteEntryRow(STR_entry,seq-1);
		}
		  
		
	}


	@Override
	public void mark(AttachmentMarkEvent evt) {
		// 提交备注前进入该方法，可修改备注信息
		evt.getSource();
	}
	
	//调用operation后刷新
	@Override
	public void afterDoOperation(AfterDoOperationEventArgs afterDoOperationEventArgs) {
		// TODO Auto-generated method stub
		super.afterDoOperation(afterDoOperationEventArgs);
		if(afterDoOperationEventArgs.getOperateKey().equals("submit")  || afterDoOperationEventArgs.getOperateKey().equals("save")) {
			if(afterDoOperationEventArgs.getOperationResult().isSuccess()) {
				this.getView().updateView();
			}
		}
	}
}
