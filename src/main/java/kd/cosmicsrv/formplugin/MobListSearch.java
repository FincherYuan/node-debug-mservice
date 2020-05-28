package kd.cosmicsrv.formplugin;

import kd.bos.bill.BillOperationStatus;
import kd.bos.bill.BillShowParameter;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.form.ShowType;
import kd.bos.form.control.Search;
import kd.bos.form.control.events.SearchEnterEvent;
import kd.bos.form.control.events.SearchEnterListener;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.ORM;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import akka.stream.impl.fusing.Map;


public class MobListSearch extends AbstractFormPlugin implements SearchEnterListener {
	private final static String KEY_SEARCH = "mobilesearch";	// 搜索控件标识
	
  public void registerListener(EventObject e) {

    // 注册监听
    Search search = this.getControl(KEY_SEARCH);
    search.addEnterListener(this);
  }
  
  @Override
  public void search(SearchEnterEvent arg0) {
	  Search search = (Search) arg0.getSource();
		if (StringUtils.equals(KEY_SEARCH, search.getKey())){
			String searchText = arg0.getText();
			this.doSearch(searchText);
		}
	  
    // 默认风格 输入搜索条件回车后 进入该方法
  }

  @Override
  public List<String> getSearchList(SearchEnterEvent arg0) {
    // 自动补全风格 输入搜索条件后 进入该方法组织数据返回前端显示
	  Search search = (Search) arg0.getSource();
		if (StringUtils.equals(KEY_SEARCH, search.getKey())) {
			String searchText = arg0.getText();
			if (StringUtils.isNotBlank(searchText))
				return this.doSearchList(searchText);
			else
				return null;
		}else
			return null;
  }
  
  
  public List<String>  doSearchList(String searchText) {
	    // 模糊搜索机构名

		// 模糊搜索名称
		// 构建取数条件
		ORM orm = ORM.create();
		
		QFilter f2 = new QFilter("customer", QCP.like, "%" + searchText + "%");
		QFilter filters = f2;

		// 到苍穹机构表中取数
		DynamicObjectCollection collection = orm.query("kded_insapply", new QFilter[]{filters});

		// 输出结构方式：{"cosmicsrv_org ???" = "客户名称：name"},
		HashMap<String, String> searchList = new HashMap<>();
		for(DynamicObject obj: collection){
			searchList.put("kded_insapply" + " " +obj.get("id").toString(),
					"客户名称：" + obj.get("customer"));
		}

		// 模糊搜索机构名称
		QFilter f = new QFilter("reqorg.name", QCP.like, "%" + searchText + "%");
		f2 = new QFilter("reqorg.number", QCP.like, "%" + searchText + "%");
		filters = f.or(f2);

		collection = orm.query("kded_insapply", new QFilter[]{filters});

		for(DynamicObject obj: collection){
			searchList.put("kded_insapply" + " " +obj.get("id").toString(),
					"机构名称：" + obj.get("reqorg.name") + obj.get("reqorg.number"));
		}

		// 把本次搜索结果放到页面缓存：后续search事件要用到
		this.getPageCache().put("searchList", SerializationUtils.toJsonString(searchList));
		return new ArrayList<>(searchList.values());
  }

  
   /**
	 * 实现搜索
	 * @param searchText 搜索文本
	 */
	private void doSearch(String searchText) {
		// 从模糊查询结果中，匹配搜索文本：找到后，打开对应的界面
		if(this.getPageCache().get("searchList") != null){
			HashMap<String, String> searchList = SerializationUtils.
					fromJsonString(this.getPageCache().get("searchList"), Map.class);
			searchList.forEach((key, value) ->{
				if(searchText.equals(value)){
					String[] arr = key.split(" ");
					BillShowParameter param = new BillShowParameter();
					param.setPkId(arr[1]);
					param.setFormId(arr[0]);
					param.getOpenStyle().setShowType(ShowType.Modal);
					param.setBillStatus(BillOperationStatus.EDIT);
					this.getView().showForm(param);
				}
			});
		}
	}
}