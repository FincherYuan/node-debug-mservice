package kd.bos.dy.dev.plugins.pur.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.dataentity.metadata.IDataEntityType;

public class DynamicObjectUtil {
	
	/**
	 * @Title: mapToObj
	 * @Description: Map转换为DynamicObject对象
	 * @author Fincher JF.Yuan
	 * @date: 2020年4月28日 下午3:11:45
	 * @param: @param params
	 * @param: @return 参数说明
	 * @return: DynamicObject 返回类型
	 * @throws
	 */
	public static DynamicObject mapToObj(Map<String, Object> params) {
		DynamicObject obj = new DynamicObject();
		Set<String> keySet = params.keySet();
		Iterator<String> iterator = keySet.iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			obj.set(key, params.get(key));
		}
		return obj;
		
	}
	
	
	/**
	 * @Title: mapToObjForEntry
	 * @Description: 构建分录DynamicObject对象
	 * @author Fincher JF.Yuan
	 * @date: 2020年4月29日 下午6:04:01
	 * @param: @param source
	 * @param: @param params
	 * @param: @return 参数说明
	 * @return: DynamicObject 返回类型
	 * @throws
	 */
	public static DynamicObject mapToObjForEntry(DynamicObject source , Map<String, Object> params) {
		Set<String> keySet = params.keySet();
		Iterator<String> iterator = keySet.iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			IDataEntityType dataEntityType = source.getDataEntityType();
			IDataEntityProperty pro = dataEntityType.getProperties().get(key);
			if(pro !=null) {
				source.set(key, params.get(key));
			}
			
			
		}
		return source;
		
	}

}
