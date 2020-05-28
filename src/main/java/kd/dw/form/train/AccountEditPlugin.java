package kd.dw.form.train;

import java.math.BigDecimal;
import java.util.EventObject;
import java.util.Map;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.IBillModel;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.IPageCache;
import kd.bos.form.ShowType;
import kd.bos.form.StyleCss;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.events.CellClickEvent;
import kd.bos.form.control.events.CellClickListener;
import kd.bos.mvc.bill.BillModel;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.bos.util.StringUtils;
import kd.fi.er.business.servicehelper.CommonServiceHelper;
import kd.fi.er.business.servicehelper.CoreBaseBillServiceHelper;

public class AccountEditPlugin extends AbstractBillPlugIn {
	
	
	
	@Override
	public void initialize() {
		super.initialize();
		EntryGrid entryGrid = this.getControl("entryentity");
		entryGrid.addCellClickListener(new CellClickListener() {
			
			@Override
			public void cellDoubleClick(CellClickEvent arg0) {
				System.out.println("12321");
				
			}
			
			@Override
			public void cellClick(CellClickEvent arg0) {
				cellClickDo(arg0);
				
			}
		});
	}
	
	/**
	 * @Title: cellClickDo
	 * @Description: 点击事件处理
	 * @author Fincher JF.Yuan
	 * @date: 2020年5月8日 下午3:04:21
	 * @param: @param arg0 参数说明
	 * @return: void 返回类型
	 * @throws
	 */
	protected void cellClickDo(CellClickEvent arg0) {
		String fieldKey = arg0.getFieldKey();
		if("applysubno".equals(fieldKey)) {
			IDataModel model = this.getModel();
			String subapplynew = (String)model.getValue("applysubno");
			if(StringUtils.isEmpty(subapplynew)) {
				return;
			}
			QFilter qFilter = QFilter.of("entryentity.subno=?", subapplynew);
			DynamicObjectCollection dw_as = QueryServiceHelper.query("dw_apply", "*,entryentity.*", new QFilter[] {qFilter});
			DynamicObject dw_as_obj = dw_as.get(0);
			Long pkId =(Long) dw_as_obj.get("entryentity.id");
			BillShowParameter billShowParameter = new BillShowParameter();
			billShowParameter.setStatus(OperationStatus.VIEW);
			billShowParameter.setPkId(pkId);
			billShowParameter.setFormId("dw_subentry");
			billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
			StyleCss styleCss = new StyleCss();
			styleCss.setHeight("600");
			styleCss.setWidth("800");
			billShowParameter.getOpenStyle().setInlineStyleCss(styleCss);
		    billShowParameter.setParentFormId("dw_account");
			billShowParameter.setParentPageId(this.getView().getPageId());		
			this.getView().showForm(billShowParameter);
			
		}
		
	}
	
	
	@Override
	public void propertyChanged(PropertyChangedArgs e) {
		super.propertyChanged(e);
		  String key = e.getProperty().getName();
		  if(key.equals("acctedamount") || key.equals("auditamount")) {
		   //判断核定金额是否大于申请金额，大于时给出提示
		   IDataModel model = this.getModel();
		   int rowIndex = e.getChangeSet()[0].getRowIndex();
		      DynamicObject row = model.getEntryRowEntity("entryentity", rowIndex);
		      BigDecimal hdamount = row.get("acctedamount")==null?BigDecimal.ZERO:(BigDecimal)row.get("acctedamount");//报账金额
		      BigDecimal reqamount = row.get("auditamount")==null?BigDecimal.ZERO:(BigDecimal)row.get("auditamount");//核定金额
		      if((reqamount.subtract(hdamount)).compareTo(BigDecimal.ZERO)<0) {
		    	  this.getView().showErrorNotification("报账金额不能大于核定金额！");
		    	  row.set("acctedamount", 0);
		       
		      }
		  }
	}
	
	/**
	 * @Title: afterCreateNewData
	 * @Description: 重写方法的描述
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月21日 下午5:12:06
	 * @param: @param e
	 * @see kd.bos.entity.datamodel.events.IDataModelListener#afterCreateNewData(java.util.EventObject)
	 */
	@Override
	public void afterCreateNewData(EventObject e) {
		super.afterCreateNewData(e);
		
		IDataModel model = this.getView().getModel();
		Long currentUserID = Long.valueOf(0L);
		IPageCache pageCache = (IPageCache)this.getView().getService(IPageCache.class);
		if(pageCache.get("consignorId") != null) {
			currentUserID = Long.valueOf(Long.parseLong(pageCache.get("consignorId")));
			pageCache.remove("consignorId");
		} else if(((IBillModel)model).isFromImport()) {
			currentUserID = (Long)model.getValue("applier");
		} else {
			RequestContext billMap = RequestContext.get();
			currentUserID = Long.valueOf(billMap.getUserId());
		}
		
		Map<String ,Object> billMap1 = CoreBaseBillServiceHelper.createNewData(currentUserID);
		
		CoreBaseBillServiceHelper.initObjByMap(model, billMap1);
		BillModel billModel = (BillModel)model;
		CommonServiceHelper.getCurrentUserID();
		RequestContext ctx = RequestContext.get();
        Long userId = Long.valueOf(ctx.getUserId());
		Map userMap = CommonServiceHelper.getUserMap(userId);
		billModel.setValue("dept", UserServiceHelper.getUserMainOrgId(userId));
		
		
	}

}
