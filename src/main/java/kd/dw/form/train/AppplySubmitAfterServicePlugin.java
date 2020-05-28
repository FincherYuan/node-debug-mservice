package kd.dw.form.train;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.entity.plugin.args.BeginOperationTransactionArgs;

public class AppplySubmitAfterServicePlugin extends AbstractOperationServicePlugIn {
	
	@Override
	public void onAddValidators(AddValidatorsEventArgs e) {
		super.onAddValidators(e);
		 e.getValidators().add(new MySubmitValidator());
	}

	@Override
	 public void beginOperationTransaction(BeginOperationTransactionArgs e) {
	  //单据保存或提交时给分录的子单据号赋值
	  DynamicObject[] dataEntities = e.getDataEntities();
	  BigDecimal totalAmount = BigDecimal.ZERO;
	  for (DynamicObject dataEntity : dataEntities) {
	   String billno = dataEntity.getString("billno");
	   // 取子单据体行
	   DynamicObjectCollection subEntryRows = dataEntity.getDynamicObjectCollection("entryentity");
	   for (DynamicObject subEntryRow : subEntryRows){
	    if(subEntryRow.getInt("Seq")<10) {
	     subEntryRow.set("subno", billno+"-0"+subEntryRow.getInt("Seq"));
	    }else {
	     subEntryRow.set("subno", billno+"-"+subEntryRow.getInt("Seq"));
	    }
	    
	    BigDecimal reqamount = subEntryRow.get("applyamount")==null?BigDecimal.ZERO:(BigDecimal)subEntryRow.get("applyamount");
	    totalAmount = totalAmount.add(reqamount);
	   }
	   dataEntity.set("totalamount", totalAmount);
	  }
	  
	  
	  super.beginOperationTransaction(e);
	 }
}
