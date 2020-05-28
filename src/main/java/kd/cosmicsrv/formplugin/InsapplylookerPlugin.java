package kd.cosmicsrv.formplugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import kd.bos.algo.DataSet;
import kd.bos.algo.JoinType;
import kd.bos.algo.Row;
import kd.bos.context.RequestContext;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.entity.report.CellStyle;
import kd.bos.form.ClientProperties;
import kd.bos.form.IFormView;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.field.events.BeforeF7SelectEvent;
import kd.bos.form.field.events.BeforeF7SelectListener;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
//import kd.fi.bcm.fel.parser.FelParser.integerLiteral_return;
import kd.cosmicsrv.tools.CosmicsrvConfigUtil;

public class InsapplylookerPlugin extends AbstractFormPlugin implements BeforeF7SelectListener{

	@Override
	public void afterCreateNewData(EventObject e) {
		// TODO Auto-generated method stub
		super.afterCreateNewData(e);
		IDataModel model = this.getModel();
		Date now = (Date) model.getValue("datefilter");	//获取当前选中时间，绑定数据	
		calculateConsultantDashNew(null,now,true);
	}
	
	public void propertyChanged(PropertyChangedArgs e) {
		// TODO Auto-generated method stub
		String key = e.getProperty().getName();
		IDataModel model = this.getModel();
		if (key.equalsIgnoreCase("datefilter")) { //月份出现变更
			Date now = (Date) model.getValue("datefilter");
			calculateConsultantDashNew(null,now,false);
		}
	}
	
	private void calculateConsultantDashNew(Long orgId,Date now,boolean isFirstLoad) {
		Long currentUserId=Long.valueOf(RequestContext.get().getUserId());
		IFormView view = this.getView();
		IDataModel model = this.getModel();
			List<Long> userIds=null;
			if(orgId==null) {
				orgId=UserServiceHelper.getUserMainOrgId(currentUserId);
			}
			model.setValue("group", "金蝶中国苍穹客户化开发支持部");
			//项目数据
			DataSet insapplyDataSet = null;
			//用户数据
			DataSet userDataSet = null;
			//分组数据
			DataSet groupDataSet = null;
			//最终数据
			DataSet finalDataSet = null;
			try {
				
				//--------------------------------根据选择的月份，动态显示列日期（28-31号）--------------------------------------------
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendarNow = Calendar.getInstance();//获取实例
				calendarNow.setTime(now);//设为选择的月份时间

				int curMonthMaxDate = calendarNow.getActualMaximum(Calendar.DAY_OF_MONTH);
				int nowMonth = calendarNow.get(Calendar.MONTH) + 1;

				Calendar firstDayCalendar = Calendar.getInstance();
				firstDayCalendar.setTime(now);
				firstDayCalendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天

				Calendar lastDayCalendar = Calendar.getInstance();
				lastDayCalendar.setTime(now);
				lastDayCalendar.set(Calendar.DAY_OF_MONTH, curMonthMaxDate);// 设置为获取当前月最后一天
				lastDayCalendar.add(Calendar.DAY_OF_MONTH, 1);//加一天到下个月

				EntryGrid eg = view.getControl("entryentity");
				for(int j=1;j<=31;j++) {
					//锁定
					eg.setColumnProperty("day" + j, ClientProperties.Lock, true);
					// 变化只在29-31，前面28天一定会显示
					if (j <= curMonthMaxDate) {
						// 从29号到最大日显示
						eg.setColumnProperty("day" + j, ClientProperties.GridColHidden, true);
					} else {
						// 如果最大日不是31日，则最大日后的日子隐藏
						eg.setColumnProperty("day" + j, ClientProperties.GridColHidden, false);
					}
				}
				eg.setColumnProperty("daycount", ClientProperties.Lock, true);
				//----------------------------------------------------------------------------
				QFilter status = new QFilter("state",QFilter.not_equals,"E"); //状态不能为已关闭
				QFilter startDateQfilter = new QFilter("helplists.end", QFilter.large_equals,
						sdf.format(firstDayCalendar.getTime()));// 结束日期大于等于月初
				QFilter endDateQfilter = new QFilter("helplists.start", QFilter.less_than,
						sdf.format(lastDayCalendar.getTime()));// 开始日期小于下个月初
				startDateQfilter.and(endDateQfilter).and(status);
				QFilter[] dateQfilters = { startDateQfilter };
				
				insapplyDataSet = QueryServiceHelper.queryDataSet("insapply", "kded_insapply",
						"helplists.helps.name,helplists.start,helplists.end,helplists.helps.id,customer",
						dateQfilters, null);
				
				String arr[] = {"helplists.helps.id"};
				groupDataSet = QueryServiceHelper.queryDataSet("insapply", "kded_insapply",
						"helplists.helps.id",
						dateQfilters, null).groupBy(arr).count().finish();
				
				insapplyDataSet=insapplyDataSet.join(groupDataSet, JoinType.LEFT).on("helplists.helps.id", "helplists.helps.id")
						.select(new String[] { "helplists.helps.id", "helplists.helps.name",
								 "helplists.start", "helplists.end", "count","customer" })
						.finish();
						
				
				
				Long number=Long.valueOf(CosmicsrvConfigUtil.safeGetValue("cosmicsrvOrgId"));
				//long number = 858744959656791043L;//测试环境
				//金蝶中国产品支持部
				userIds=UserServiceHelper.getAllUsersOfOrg(number);
				
				QFilter usersQfilter = new QFilter("id", QFilter.in,userIds);
				//需要排除已离职的
				QFilter statusQfilter = new QFilter("enable", QFilter.equals,"1");
				usersQfilter.and(statusQfilter);
				QFilter[] usersQfilters = { usersQfilter };
				//
				userDataSet = QueryServiceHelper.queryDataSet("cosmicsrv_user", "bos_user",
						"id,number,name",	usersQfilters, null);
				
				//
				finalDataSet = userDataSet.join(insapplyDataSet, JoinType.LEFT).on("id", "helplists.helps.id")
						.select(new String[] { "id", "name","customer",
								 "helplists.start", "helplists.end", "number", "count" })
						.finish().orderBy(new String[] { "number" });
				

					model.deleteEntryData("entryentity");
					HashMap mapStyle=new HashMap();//样式集合
					ArrayList<HashMap> listModel=new ArrayList<HashMap>();//model集合
				for (Row row : finalDataSet) {
					ArrayList<Object> listStyle=new ArrayList<>();
					
					HashMap mapModel=new HashMap();
					mapModel.put("id", row.get("id"));
					mapModel.put("name", row.get("name"));
					mapModel.put("number", row.get("number"));
					mapModel.put("customer", row.get("customer"));
					mapModel.put("count", row.get("count"));
					listModel.add(mapModel);
					
					Date startDate = row.getDate("helplists.start");
					if (startDate != null) {
						int count=	(int) row.get("count");
						Calendar calendarStartDate = Calendar.getInstance();
						calendarStartDate.setTime(startDate);
						int startDateMonth = calendarStartDate.get(Calendar.MONTH) + 1;
						Date endDate = row.getDate("helplists.end");
						Calendar calendarEndDate = Calendar.getInstance();
						calendarEndDate.setTime(endDate);
						int endDateMonth = calendarEndDate.get(Calendar.MONTH) + 1;
						int startday;
						int endday;
						// 两种特殊情形：最小日期小于当月，最大日期大于当月，都在当月
						if (startDateMonth == nowMonth) {
							// 如果起始日期所在月等于当前日期所在月，就取开始日期里面的日，当然也可能为1
							startday = calendarStartDate.get(Calendar.DAY_OF_MONTH);
						} else {
							// 否则默认1号
							startday = 1;
						}
						if (endDateMonth == nowMonth) {
							// 如果结束日期所在月大于当前月份,则最大日为当月最大日
							endday = calendarEndDate.get(Calendar.DAY_OF_MONTH);
						} else {
							// 否则就取当月最大日期
							endday = curMonthMaxDate;
						}
						Object olist = mapStyle.get(row.get("id")+"");
						//开始日期1-31 结束日期1-31 项目数  客户
						Object [] arr1={startday,endday,count,row.get("customer")};
						if(olist==null) {
							listStyle.add(arr1);
							mapStyle.put(row.get("id")+"", listStyle);
						}else {
							ArrayList oList=(ArrayList) mapStyle.get(row.get("id")+"");
							oList.add(arr1);
							mapStyle.put(row.get("id")+"", oList);
						}

					}
					
				}
				//去重排序
				listModel=removeRepeatMapByKey(listModel, "number");
				
				int rowIndex = 0;
				//用户信息循环 start
				for(int x=0;x<listModel.size();x++) {
					HashMap<String,Integer[]> customers = new HashMap<>();
					model.batchCreateNewEntryRow("entryentity", 1);
					model.setValue("realname", listModel.get(x).get("name"), rowIndex);
					model.setValue("projectcount", "0" ,rowIndex);
					model.setValue("serialnumber", x+1 ,rowIndex);
					String id = listModel.get(x).get("id")+"";
					if (mapStyle.get(id) != null) {
						//获取一个用户下所有项目的支持记录
						List<Object[]> lists=(List<Object[]>) mapStyle.get(id);
						//项目数
						model.setValue("projectcount", (Integer)lists.get(0)[2], rowIndex);
						
						CellStyle cellStyle = new CellStyle();
						
						//用户循环 end
						for(int y=0;y<lists.size();y++) {
							
							String customer = (String)lists.get(y)[3];//客户名
							Integer start = (Integer)lists.get(y)[0];//开始日期
							Integer end = (Integer)lists.get(y)[1];//结束日期
							//创建客户行
							if(!customers.containsKey(customer)) {
								//去掉用户空行
								if(y != 0) {
									rowIndex++;
									model.batchCreateNewEntryRow("entryentity", 1);
								}
								customers.put(customer, new Integer[]{rowIndex});
								model.setValue("customer", customer, rowIndex);
								//TODO 用户间隔用不同颜色  客户索引MAP 当前客户 当前用户id顺序 页面对象  
								setDiffUserBackColor(customers,customer,curMonthMaxDate,x,eg);
								
							}
							//保存客户 行号-总支持天数  数据
							Integer[] tmp = new Integer[] {
									customers.get(customer)[0],customers.get(customer).length==2?customers.get(customer)[1]+(end -start+1):0+(end -start+1)
							};
							customers.put(customer, tmp);
							
							//设置占用天的格子样式
							cellStyle.setRow(customers.get(customer)[0]);
							cellStyle.setBackColor("#ffd4aa");
							List<CellStyle> cellStyles = new ArrayList<CellStyle>();
							cellStyles.add(cellStyle);
							for (int k = start; k <= end; k++) {
								cellStyle.setFieldKey("day" + k);
								eg.setCellStyle(cellStyles);
							}
							// 优化 显示  中间显示 （start - end） 天
							showDaycountInColor(start,end,model,customers.get(customer)[0]);
						}
						// 循环当月项目支持总天数
						for(Entry<String, Integer[]> entity : customers.entrySet()) {
							model.setValue("daycount", entity.getValue()[1], entity.getValue()[0]);
						}
						
						
					}
					
					rowIndex++;
					
				}
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				if (insapplyDataSet != null)
					insapplyDataSet.close();
				if (userDataSet != null)
					userDataSet.close();
				if (groupDataSet != null)
					groupDataSet.close();
				if (finalDataSet != null)
					finalDataSet.close();
			}
	}
	
	// 优化 显示  中间显示 （start - end） 天
	private void showDaycountInColor(int start, int end, IDataModel model,int indexRow) {
		// 优化 显示  中间显示 （start - end） 天
		int length = end - start + 1;
		int mid = length / 2;
		mid = length % 2 == 0 ? mid - 1 : mid; //消除偶数天显示位置偏后问题
		model.setValue("day" + (start + mid), length+"天", indexRow);
	}
	
	private void setDiffUserBackColor(HashMap<String,Integer[]> customers,String customer,int curMonthMaxDate,int x,EntryGrid eg) {
		CellStyle cellStyle = new CellStyle();
		cellStyle.setRow(customers.get(customer)[0]);
		String color = x % 2 == 0 ? "#edf3f559" : "#edf3f559";
		cellStyle.setBackColor(color);
		List<CellStyle> cellStyles = new ArrayList<CellStyle>();
		cellStyles.add(cellStyle);
		//设置日期行
		for (int k = 1; k <= curMonthMaxDate; k++) {
			cellStyle.setFieldKey("day" + k);
			eg.setCellStyle(cellStyles);
		}
		cellStyle.setFieldKey("realname");
		eg.setCellStyle(cellStyles);
		cellStyle.setFieldKey("projectcount");
		eg.setCellStyle(cellStyles);
		cellStyle.setFieldKey("customer");
		eg.setCellStyle(cellStyles);
		cellStyle.setFieldKey("daycount");
		eg.setCellStyle(cellStyles);
		cellStyle.setFieldKey("serialnumber");
		eg.setCellStyle(cellStyles);
	}
	
	@Override
	public void beforeF7Select(BeforeF7SelectEvent paramBeforeF7SelectEvent) {
		// TODO Auto-generated method stub
	}
	  /**
     * 根据map中的某个key 去除List中重复的map并排序
     * @author  zhaihao
     * @param list2
     * @param mapKey
     * @return
     */
    public  ArrayList<HashMap> removeRepeatMapByKey(ArrayList<HashMap> 
           list2, String mapKey){
        //把list中的数据转换成msp,去掉同一id值多余数据，保留查找到第一个id值对应的数据
    	ArrayList<HashMap> listMap = new ArrayList<>();
    	HashMap<String, Map> msp = new HashMap<>();
        for(int i = list2.size()-1 ; i>=0; i--){
        	HashMap map = list2.get(i);
            String id = map.get(mapKey)+"";
            map.remove(mapKey);
            msp.put(id, map);
            
        }
        //把msp再转换成list,就会得到根据某一字段去掉重复的数据的List<Map>
        Set<String> mspKey = msp.keySet();
        for(String key: mspKey){
        	HashMap newMap = (HashMap) msp.get(key);
            newMap.put(mapKey, key);
            listMap.add(newMap);
        }
        /*Collections.sort(listMap, new Comparator<HashMap>() {
            public int compare(HashMap o1, HashMap o2) {
            	Long name1 = Long.valueOf(o1.get(mapKey).toString()) ;//name1是从你list里面拿出来的一个 
            	Long name2 = Long.valueOf(o2.get(mapKey).toString()) ; //name1是从你list里面拿出来的第二个name
                return name1.compareTo(name2);
            }
        });*/
        
        return listMap;
    }


}
