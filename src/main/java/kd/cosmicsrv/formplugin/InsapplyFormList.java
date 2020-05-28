package kd.cosmicsrv.formplugin;


import com.alibaba.druid.util.StringUtils;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.*;
import kd.bos.form.control.events.*;
import kd.bos.form.events.MessageBoxClosedEvent;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.ksql.util.StringUtil;
import kd.bos.list.IListView;
import kd.bos.list.events.ListRowClickEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.workflow.unittest.util.BusinessDataHelper;
import kd.cosmicsrv.tools.CosmicsrvConfigUtil;

public class InsapplyFormList extends AbstractListPlugin implements ItemClickListener, ClickListener, RowClickEventListener, TreeNodeClickListener{
	
	@Override
	public void listRowDoubleClick(ListRowClickEvent evt) {
		
		 ListSelectedRow o = evt.getCurrentListSelectedRow();
		 String billNo = o.getBillNo();
		 
		 
			 QFilter billNoQfilter = new QFilter("billno", QFilter.equals,
					 billNo);// 结束日期大于等于月初
				QFilter[] dateQfilters = { billNoQfilter };
				/*insapplyDataSet = QueryServiceHelper.queryDataSet("insapply", "kded_insapply",
						"customer",
						dateQfilters, null);*/
				
				DynamicObject queryOne = QueryServiceHelper.queryOne("kded_insapply", "customer", dateQfilters);
				
				String customer = (String) queryOne.get("customer");
				
				FormShowParameter formPara = new FormShowParameter();
				formPara.setCaption("客户项目支持");
				formPara.setFormId("kded_cusproject");
				formPara.setCustomParam("customer", customer);
				formPara.getOpenStyle().setShowType(ShowType.MainNewTabPage);
				this.getView().showForm(formPara);
		
	}
	

	private final static String KEY_SUCEESS_CLOSE = "normal_close";//G
	private final static String KEY_ERROR_CLOSE = "error_close";//E

	/**
	 * 用户点击主菜单按钮时，触发此事件
	 *
	 * 插件可以在此事件，检查选中的列表数据，取消按钮绑定操作执行
	 */
	@Override
	public void beforeItemClick(BeforeItemClickEvent evt) {
		if (StringUtils.equals(KEY_ERROR_CLOSE, evt.getItemKey())||StringUtils.equals(KEY_SUCEESS_CLOSE, evt.getItemKey())){
			// 取消修改操作的执行
			
			
			IListView listview = (IListView)this.getView();
			ListSelectedRowCollection selectedRows= listview.getSelectedRows();
	            //获取当前列表的选中行
	         
	         if (selectedRows.size()  == 1) {
	        	 return;
	         }
	         String msg = selectedRows.size()==0?"请选中一行数据":"请不要选择多行数据";
	         this.getView().showTipNotification(msg,5000);
			 evt.setCancel(true);
		}
	}

	
	/**
	 * 用户点击主菜单按钮时，触发此事件
	 * @remark
	 * 插件可以在此事件中，实现自定义按钮的逻辑处理
	 */
	
	@Override
	public void itemClick(ItemClickEvent evt) {
		//成功关闭，状态为已上线
		if (StringUtils.equals(KEY_ERROR_CLOSE, evt.getItemKey())||StringUtils.equals(KEY_SUCEESS_CLOSE, evt.getItemKey())){
			ConfirmCallBackListener confirmCallBackListener = new ConfirmCallBackListener(StringUtils.equals(KEY_SUCEESS_CLOSE, evt.getItemKey())?KEY_SUCEESS_CLOSE:KEY_ERROR_CLOSE, this);
            //设置页面确认框，参数为：标题，选项框类型，回调监听
            this.getView().showConfirm(StringUtils.equals(KEY_SUCEESS_CLOSE, evt.getItemKey())?"项目上线后将无法回退，请确认是否继续":"项目关闭后将无法回退，请确认是否继续", MessageBoxOptions.OKCancel, confirmCallBackListener);
			
		}

	}
	
	/**
     * 选择框回调函数
     * @param messageBoxClosedEvent
     */
    @Override
    public void confirmCallBack(MessageBoxClosedEvent messageBoxClosedEvent) {
        super.confirmCallBack(messageBoxClosedEvent);
        //判断是否是对应确认框的点击回调事件
        if (StringUtils.equals(KEY_SUCEESS_CLOSE, messageBoxClosedEvent.getCallBackId())||StringUtils.equals(KEY_ERROR_CLOSE, messageBoxClosedEvent.getCallBackId())) {
            if (MessageBoxResult.Yes.equals(messageBoxClosedEvent.getResult())) {
                //如果点击确认按钮，则把当前页面相关值清空
            	// 获取用户id
    			String state = StringUtils.equals(KEY_SUCEESS_CLOSE, messageBoxClosedEvent.getCallBackId())?"G":"E";
    			IListView listview = (IListView)this.getView();
    			ListSelectedRowCollection selectedRows= listview.getSelectedRows();
	           
    	        QFilter qFilter = new QFilter("billNo", QCP.equals, selectedRows.get(0).getBillNo());
    	        QFilter[] filters = new QFilter[]{qFilter};
    	        
    	        DynamicObject specialSupportBill = BusinessDataServiceHelper.loadSingle("kded_insapply","state",filters);
    	        
    	        if(!specialSupportBill.get("state").equals("D")) {
    	        	this.getView().showTipNotification("只能修改审核通过的单据",5000);
    	        	return;
    	        }
    	        specialSupportBill.set("state", state);
    	        BusinessDataHelper.updateBusinessData(specialSupportBill);
    	        listview.refresh();
                
            } else if (MessageBoxResult.No.equals(messageBoxClosedEvent.getResult())) {
            // 点击否的相关处理逻辑。。。。
            } else if (MessageBoxResult.Cancel.equals(messageBoxClosedEvent.getResult())) {
            // 点击取消的相关处理逻辑。。。。
            }
        }
    }

	/**
	 * 在开始对列表数据进行过滤取数前，触发此事件
	 * @remark
	 * 1. 使用本地组织值，生成列表过滤条件，添加到列表过滤条件中
	 */
	@Override
	public void setFilter(SetFilterEvent e) {
		//如果有权限直接通过 不处理
		if (viewAllListPermit()){
			return;
		}
		String userName = RequestContext.get().getUserName();
		/*
		 * saleman 销售
		 * projectleader 项目经理
		 * developleader 开发经理
		 * helper 总部对接人
		 * helplist helps 支持日志，无法过滤 出现报错
		 * creator 创建人
		 * auditor 审核人
		 */
		QFilter filter = new QFilter("creator.name",QFilter.equals,userName).
				or(new QFilter("auditor.name",QFilter.equals,userName)).
				or(new QFilter("saleman",QFilter.equals,userName)).
				or(new QFilter("projectleader",QFilter.equals,userName)).
				or(new QFilter("developleader",QFilter.equals,userName)).
				or(new QFilter("helper.name",QFilter.equals,userName));

		e.getQFilters().add(filter);
	}
	private boolean viewAllListPermit(){
		String permit = CosmicsrvConfigUtil.safeGetValue("view_rolesnumber");//权限列表
		//查询当前登录用户所包含角色编码
		QFilter roleFilter = new QFilter("user", "=", RequestContext.get().getUserId());
		QFilter[] roleFilters = new QFilter[]{roleFilter};
		DynamicObject[] roleNumbersCollection = BusinessDataServiceHelper.load("perm_userrole", "role.number", roleFilters, "");//当前登录用户角色集合
		for (DynamicObject d : roleNumbersCollection) {
			if (StringUtil.equals(d.getString("role.number"),permit)){
				return true;
			}
		}
		return false;
	}




}
