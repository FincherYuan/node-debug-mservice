package kd.dw.form.train;

import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;

public class AccountSaveAfterServicePlugin extends AbstractOperationServicePlugIn {
	
	@Override
	public void onAddValidators(AddValidatorsEventArgs e) {
		super.onAddValidators(e);
		 e.getValidators().add(new AmountNotNegaValidator());
	}

}
