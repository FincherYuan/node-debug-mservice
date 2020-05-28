package kd.dw.form.train;

import java.util.ArrayList;
import java.util.List;

import kd.bos.context.RequestContext;
import kd.bos.form.events.FilterContainerInitArgs;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.impl.ORMUtil;
import kd.bos.orm.query.QFilter;

public class ApplyListPlugin extends AbstractListPlugin {
	
	
	

	/**
	 * @Title: setFilter
	 * @Description: 添加过滤条件
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月22日 下午2:23:52
	 * @param: @param e
	 * @see kd.bos.list.plugin.IListPlugin#setFilter(kd.bos.form.events.SetFilterEvent)
	 */
	@Override
	public void setFilter(SetFilterEvent e) {
		RequestContext ctx = RequestContext.get();
        Long userId = Long.valueOf(ctx.getUserId());
		List<QFilter> filters = new ArrayList<QFilter>();
		QFilter qfilter = new QFilter("applier.id","=",userId);
		qfilter.or(new QFilter("mulapprover.fbasedataid.id","=",userId));
		filters.add(qfilter);
		e.setCustomQFilters(filters);
		super.setFilter(e);
		
	}
}
