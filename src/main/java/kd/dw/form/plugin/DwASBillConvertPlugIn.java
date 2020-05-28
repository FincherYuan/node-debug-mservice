package kd.dw.form.plugin;

import java.util.List;
import java.util.Map;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicProperty;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.ExtendedDataEntitySet;
import kd.bos.entity.botp.plugin.AbstractConvertPlugIn;
import kd.bos.entity.botp.plugin.args.AfterFieldMappingEventArgs;
import kd.bos.entity.botp.runtime.ConvertConst;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;

/**
 * @ClassName: 立项单BOTP转换插件
 * @Description: none
 * @author: Fincher JF.Yuan
 * @Company: none
 * @date: 2020年5月8日 下午2:39:38
 * @param: 
 */
public class DwASBillConvertPlugIn extends AbstractConvertPlugIn {
	
	@Override
	public void afterFieldMapping(AfterFieldMappingEventArgs e) {
		String srcName = this.getSrcMainType().getName();
		String tgtName = this.getTgtMainType().getName();
		Map<String, DynamicProperty> fldProperties = e.getFldProperties();
		ExtendedDataEntitySet targetExtDataEntitySet = e.getTargetExtDataEntitySet();
		ExtendedDataEntity[] dataEntitys = targetExtDataEntitySet.FindByEntityKey(tgtName);
		/*for(ExtendedDataEntity dataEntity : dataEntitys) {
			List<DynamicObject> values = (List<DynamicObject>)dataEntity.getValue(ConvertConst.ConvExtDataKey_SourceRows);
			DynamicObject srcRow = values.get(0);
			Long id = (Long) e.getFldProperties().get("id").getValue(srcRow);
			QFilter qFilter = QFilter.of("billno=?", billno);
			DynamicObjectCollection dw_as = QueryServiceHelper.query("dw_as", "*,entryentity.*", new QFilter[] {qFilter});
			DynamicObject dw_as_obj = dw_as.get(0);
			Long entryentity_id =(Long) dw_as_obj.get("id");
			
			
			DynamicObject dw_as = BusinessDataServiceHelper.loadSingle(id, "dw_as");
			DynamicObjectCollection entryentity = dw_as.getDynamicObjectCollection("entryentity");
			dataEntity.setValue("entryentity.subas", entryentity.get(0));
			
			
		}*/
	}

}
