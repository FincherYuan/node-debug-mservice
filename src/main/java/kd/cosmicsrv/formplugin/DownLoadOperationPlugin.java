package kd.cosmicsrv.formplugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.AfterOperationArgs;
import kd.bos.entity.plugin.args.BeginOperationTransactionArgs;
import kd.bos.entity.plugin.args.EndOperationTransactionArgs;
import kd.bos.servicehelper.AttachmentServiceHelper;
import kd.bos.servicehelper.BusinessDataServiceHelper;

/**
 * @author wyw
 * @下午3:40:13
 * @DownLoadOperationPlugin.java
 * @description
 */
public class DownLoadOperationPlugin extends AbstractOperationServicePlugIn {
	
	private String STR_Bill ="kded_download";//表单标识
	private String STR_entry ="entryentity";//单据体标识
	private String STR_attPanel ="attachmentpanel";//附件面板标识
	private String STR_contenttype ="kded_contenttype";//文件类型标识
	private String STR_uid = "uid";//uid
	private String STR_url = "url";//url
	private String STR_save = "save";//保存按钮标识
	
	private String STR_name = "name";//主题
	private String STR_abstract = "abstract";//摘要
	private String STR_lable = "lable";//标签
	private String STR_group = "group";//内容类型
	
	private String STR_entryname = "entryname";//分录主题
	private String STR_entryabstract = "entryabstract";//分录摘要
	private String STR_entrylable = "entrylable";//分录标签
	private String STR_entrygroup = "entrygroup";//分录内容类型
	
	

	/* 
	 * @see kd.bos.entity.plugin.IOperationServicePlugIn#beginOperationTransaction(kd.bos.entity.plugin.args.BeginOperationTransactionArgs)
	 */
	@Override
	public void beginOperationTransaction(BeginOperationTransactionArgs e) {
		
		super.beginOperationTransaction(e);
		
		if(e.getOperationKey().equals("save")) {
			//保存时获取正式的附件面板中附件的正式url更新到附件分录信息界面
			DynamicObject[] dataEntities = e.getDataEntities();
			for (DynamicObject dataEntity : dataEntities) {
				DynamicObject obj = dataEntity;
				String name = obj.getString(STR_name);//主题
				String billabstract = obj.getString(STR_abstract);//摘要
				String lable = obj.getString(STR_lable);//标签
				DynamicObject groupData = obj.getDynamicObject(STR_group);//内容类型
				//Long groupId = obj.getLong(STR_group);//内容类型
				//DynamicObject groupData = BusinessDataServiceHelper.loadSingle(groupId, STR_contenttype);//获取文件类型
				
				//根据表单标识、表单ID，附件面板标识获取附件信息
				Map attMap =new HashMap();
				List<Map<String, Object>> atts = AttachmentServiceHelper.getAttachments(STR_Bill, obj.getPkValue().toString(),STR_attPanel);
				for (Map<String, Object>  att: atts) {
					String uid = (String) att.get(STR_uid);
					String url = (String) att.get(STR_url);
					attMap.put(uid, url);
				}
				
				DynamicObjectCollection attEntryRows = dataEntity.getDynamicObjectCollection(STR_entry);
				for (DynamicObject attEntryRow : attEntryRows){
					String  uid = (String) attEntryRow.get(STR_uid);
					if(attMap !=null && attMap.containsKey(uid)) {
						String url = (String) attMap.get(uid);
						attEntryRow.set(STR_url, url);
						
						attEntryRow.set(STR_entryname, name);//分录主题
						attEntryRow.set(STR_entryabstract, billabstract);//分录摘要
						attEntryRow.set(STR_entrylable, lable);//分录标签
						attEntryRow.set(STR_entrygroup, groupData);//分录内容类型
					}
				}
			
			}
		}

		
	}
	
	/* 
	 * @see kd.bos.entity.plugin.IOperationServicePlugIn#endOperationTransaction(kd.bos.entity.plugin.args.EndOperationTransactionArgs)
	 */
	@Override
	public void endOperationTransaction(EndOperationTransactionArgs e) {
		
		super.endOperationTransaction(e);

	}
	
	
	/* 
	 * @see kd.bos.entity.plugin.IOperationServicePlugIn#afterExecuteOperationTransaction(kd.bos.entity.plugin.args.AfterOperationArgs)
	 */
	@Override
	public void afterExecuteOperationTransaction(AfterOperationArgs e) {
		
		super.afterExecuteOperationTransaction(e);
	}
}
