package kd.dw.form.train;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.validate.AbstractValidator;
import kd.bos.servicehelper.BusinessDataServiceHelper;

/**
 * @ClassName: AmountNotNegaValidator
 * @Description: 金额不为负数校验器
 * @author: Fincher JF.Yuan
 * @Company: none
 * @date: 2020年5月22日 上午12:30:40
 * @param: 
 */
public class AmountNotNegaValidator extends AbstractValidator {

	@Override
	public void validate() {
		//遍历单据校验
        for (ExtendedDataEntity dataEntity : dataEntities) {
        	DynamicObject dynamicObject = dataEntity.getDataEntity();
        	Object id = dynamicObject.get("id");
        	if(id != null && id != Long.valueOf("0")) {
            	dynamicObject = BusinessDataServiceHelper.loadSingle(id, "dw_account");
        	}
        	
        	DynamicObjectCollection coll = (DynamicObjectCollection) dynamicObject.get("entryentity");
            for(DynamicObject entry : coll) {
            	
            }
        }

	}

}
