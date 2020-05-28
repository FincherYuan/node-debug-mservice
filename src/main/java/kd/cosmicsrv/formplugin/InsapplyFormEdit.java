package kd.cosmicsrv.formplugin;

import java.util.*;


import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.events.ChangeData;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.events.BeforeClosedEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.bos.servicehelper.workflow.MessageCenterServiceHelper;
import kd.bos.workflow.engine.msg.info.MessageInfo;
import kd.cosmicsrv.tools.BizUtil;
import kd.cosmicsrv.tools.CosmicsrvConfigUtil;
import org.apache.commons.lang3.StringUtils;

public class InsapplyFormEdit extends AbstractFormPlugin {

	@Override
	public void beforeBindData(EventObject e) {
		super.beforeBindData(e);
		//在初始化数据过程中 给字段绑定数据
	}

	@Override
	public void afterBindData(EventObject e) {
		Long currentUserId = Long.valueOf(RequestContext.get().getUserId());

		boolean permitFlag = false;
		//获取权限编码
		String rolesNumbersString = CosmicsrvConfigUtil.safeGetValue("modify_rolesnumber");
		//查询当前登录用户所包含角色编码
		QFilter roleFilter = new QFilter("user", "=", currentUserId);
		QFilter[] roleFilters = new QFilter[]{roleFilter};
		DynamicObject[] roleNumbersCollection = BusinessDataServiceHelper.load("perm_userrole", "role.number", roleFilters, "");//当前登录用户角色集合
		for (DynamicObject d :
				roleNumbersCollection) {
			if (rolesNumbersString == null){
				break;
			}
			if (Objects.requireNonNull(rolesNumbersString).contains(d.getString("role.number"))) {
				permitFlag = true;
			}
		}

		//非管理员，不可修改单据数据,同时单据处不处于暂存状态
		QFilter qFilter = new QFilter("billNo", QCP.equals, this.getModel().getValue("billno"));
		QFilter[] filters = new QFilter[]{qFilter};
		DynamicObject specialSupportBill = BusinessDataServiceHelper.loadSingle("kded_insapply","state",filters);
		if (specialSupportBill !=null ){//如果不是新建的表单
			if (!permitFlag&&!specialSupportBill.get("state").equals("A")) {//普通用户权限设置，同时不是暂存的表单
				this.getView().setEnable(false, "saleman");
				this.getView().setEnable(false, "telsale");
				this.getView().setEnable(false, "projectleader");
				this.getView().setEnable(false, "telpro");
				this.getView().setEnable(false, "contractamount");
				this.getView().setEnable(false, "developleader");
				this.getView().setEnable(false, "teldeve");
				this.getView().setEnable(false, "state");
				this.getView().setEnable(false, "daterange");
				this.getView().setEnable(false, "progress");
				this.getView().setEnable(false, "customertype");
				this.getView().setEnable(false, "events");
				this.getView().setEnable(false, "desc");
			}else if (specialSupportBill.get("state").equals("A")){//暂存的表单，以下字段不允许修改
				this.getView().setEnable(false, "progress");
				this.getView().setEnable(false, "customertype");
				this.getView().setEnable(false, "events");
			}
		}


		//指定部门下用户
		Long number = Long.valueOf(CosmicsrvConfigUtil.safeGetValue("cosmicsrvOrgId"));
		List<Long> users = UserServiceHelper.getAllUsersOfOrg(number);
		//权限判定
		if (users.contains(currentUserId)) {//属于支持部的
			//支持部可以添加日志
			this.getView().setEnable(false, "adds");
			this.getView().setEnable(false, "deletes");
			this.getView().setEnable(false, -1, "question");
		} else {//外部人员 可以添加重大事件
			this.getView().setEnable(false, "addhelper");
			this.getView().setEnable(false, "delete");
			this.getView().setEnable(false, "helpstyle");
			this.getView().setEnable(false, -1, "answer");
			this.getView().setEnable(false, -1, "problemer");
			this.getView().setEnable(false, -1, "helps");
			this.getView().setEnable(false, -1, "context");
			this.getView().setEnable(false, -1, "dateranges");

		}
	}

	@Override
	public void propertyChanged(PropertyChangedArgs e) {

		String propertyName = e.getProperty().getName();
		EntryGrid grid = this.getView().getControl("helplists");

		int[] rowindexs = grid.getEntryState().getSelectedRows();
		int totalday = 0;
		//动态生成对接人的部门
		if ("helper".equals(propertyName)) {
			DynamicObject helper = (DynamicObject) this.getModel().getValue("helper");
			if (helper == null) {
				return;
			}
			Long helpId = (Long) helper.get("id");
			this.getModel().setValue("helporg", UserServiceHelper.getUserMainOrgId(helpId));

			//给对接人发送通知
			ChangeData[] changeset = e.getChangeSet();
			DynamicObject oldHelper = (DynamicObject) changeset[0].getOldValue();
			this.sendMassageToHelper(helper, oldHelper);
		} else if ("helps".equals(propertyName)) {    //动态生成对接人的部门
			DynamicObject helper = (DynamicObject) this.getModel().getValue("helps");

			Long helpid = (Long) helper.get("id");

			this.getModel().setValue("orgs", UserServiceHelper.getUserMainOrgId(helpid), rowindexs[0]);
		} else if ("end".equals(propertyName)) {//动态生成人天
			Date start = (Date) this.getModel().getValue("start");
			Date end = (Date) this.getModel().getValue("end");

			int days = BizUtil.caclDayInterval(start, end);

			this.getModel().setValue("persondays", days, rowindexs[0]);
		} else if ("persondays".equals(propertyName)) {//动态生成人天
			DynamicObjectCollection entrys = this.getModel().getEntryEntity("helplists");
			for (int i = 0; i < entrys.size(); i++) {
				int day = (int) entrys.get(i).get("persondays");
				totalday += day;
			}
			this.getModel().setValue("peopleday", totalday);
		} else if ("question".equals(propertyName)) {//动态生成重大事件
			DynamicObjectCollection entrys = this.getModel().getEntryEntity("problem");
			this.getModel().setValue("events", entrys.size());
		}
		super.propertyChanged(e);
	}

	//页面关闭事件
	@Override
	public void beforeClosed(BeforeClosedEvent e) {
		super.beforeClosed(e);

	}

	private void sendMassageToHelper(DynamicObject helper, DynamicObject oldHelper) {
		//获取数据进行前后比较
		//DynamicObject helper = (DynamicObject) this.getModel().getValue("helper");
		//DynamicObject oldHelper = (DynamicObject) this.getModel().getValue("oldHelper");
		if (helper == null) {
			return;
		}
		Long helperId = helper.getLong("id");
		Long oldHelperId = oldHelper == null ? -1L : oldHelper.getLong("id");
		//对接人出现了变更 进行消息通知
		if (!helperId.equals(oldHelperId)) {
			MessageInfo message = new MessageInfo();
			message.setType(MessageInfo.TYPE_MESSAGE);
			String msg = "小C通知：【" + String.valueOf(RequestContext.get().getUserName()) + "】已将你指定为【" + this.getModel().getValue("billno") + "-" + this.getModel().getValue("customer") + "】项目的总部支持对接人，详情请登录 小C-开发项目支持申请 查阅！";
			message.setTitle(msg);
			message.setContent(msg);
			ArrayList<Long> receivers = new ArrayList<Long>();
			receivers.add(helperId);
			message.setUserIds(receivers);
			message.setSenderName("小C");
			message.setEntityNumber(this.getModel().getDataEntityType().getName());
			message.setOperation("view");
			//message.setBizDataId((Long) this.getModel().getValue("billno"));
			message.setTag("重要,必读");
			Long msgId = MessageCenterServiceHelper.sendMessage(message);

		}
	}


}
