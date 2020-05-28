package kd.dw.form.train;

import java.util.List;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.validate.AbstractValidator;
import kd.bos.servicehelper.BusinessDataServiceHelper;

/**
 * @ClassName: MySubmitValidator
 * @Description: none
 * @author: Fincher JF.Yuan
 * @Company: none
 * @date: 2020年5月21日 下午6:40:11
 * @param: 
 */
public class MySubmitValidator extends AbstractValidator {

	@Override
	public void validate() {
		//遍历单据校验
        for (ExtendedDataEntity dataEntity : dataEntities) {
        	DynamicObject dynamicObject = dataEntity.getDataEntity();
        	Object id = dynamicObject.get("id");
        	if(id != null && id != Long.valueOf("0")) {
            	dynamicObject = BusinessDataServiceHelper.loadSingle(id, "dw_apply");
        	}
        	
        	DynamicObjectCollection coll = (DynamicObjectCollection) dynamicObject.get("mulapprover");
            if(coll.size()<2) {
            	addErrorMessage(dataEntity, "会审人至少于两个！");
            	return;
            }
        }
    }

}
