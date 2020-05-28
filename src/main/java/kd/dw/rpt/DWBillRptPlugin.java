package kd.dw.rpt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import kd.bos.algo.Algo;
import kd.bos.algo.DataSet;
import kd.bos.algo.DataType;
import kd.bos.algo.Row;
import kd.bos.algo.RowMeta;
import kd.bos.algo.RowMetaFactory;
import kd.bos.algo.input.CollectionInput;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.entity.report.AbstractReportColumn;
import kd.bos.entity.report.AbstractReportListDataPlugin;
import kd.bos.entity.report.FilterItemInfo;
import kd.bos.entity.report.ReportColumn;
import kd.bos.entity.report.ReportQueryParam;
import kd.bos.orm.impl.ORMUtil;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.util.StringUtils;

public class DWBillRptPlugin extends AbstractReportListDataPlugin {
	
	private String[] FIELDS = {"canal","billno","applier","date","advertisers","applyamount","alredyamount"};
	private String[] FIELDS_Meta = {"entryentity.canal","billno","applier","date","entryentity.advertisers","entryentity.applyamount","entryentity.alredyamount"};
	private static DataType [] DATATYPES = {DataType.LongType, DataType.LongType, DataType.StringType, DataType.LongType, 
			DataType.LongType, DataType.BigDecimalType, DataType.BigDecimalType};
	
	/*private String[] FIELDS_Dynamic = null;
	private String[] Caption_Dynamic = null;
	private String[] FieldType_Dynamic = null;*/
	
	private  List<String> FIELDS_Dynamic = null;
	private  List<Object> Caption_Dynamic = null;
	private  List<DataType> FieldType_Dynamic = null;
	private int alredyamount_indexof = 6;
	
	private List<String>  test= new ArrayList<String>();
	
	
	void init() {
		 FIELDS_Dynamic = new ArrayList<String>();
		 FIELDS_Dynamic.add("canal");
		 FIELDS_Dynamic.add("billno");
		 FIELDS_Dynamic.add("applier");
		 FIELDS_Dynamic.add("date");
		 
		 FieldType_Dynamic = new ArrayList<DataType>();
		 FieldType_Dynamic.add(DataType.LongType);
		 FieldType_Dynamic.add(DataType.StringType);
		 FieldType_Dynamic.add(DataType.LongType);
		 FieldType_Dynamic.add(DataType.DateType);
		 
		 Caption_Dynamic = new ArrayList<Object>();
		 Caption_Dynamic.add("渠道");
		 Caption_Dynamic.add("编码");
		 Caption_Dynamic.add("申请人");
		 Caption_Dynamic.add("申请日期");
	}
	/**
	 * @Title: query
	 * @Description: 查询数据
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月9日 下午3:53:10
	 * @param: @param arg0
	 * @param: @param arg1
	 * @param: @return
	 * @param: @throws Throwable
	 * @see kd.bos.entity.report.AbstractReportListDataPlugin#query(kd.bos.entity.report.ReportQueryParam, java.lang.Object)
	 */
	@Override
	public DataSet query(ReportQueryParam arg0, Object arg1) throws Throwable {
		init();
		String orderby = "billno";
		QFilter[] qFilters = getFilter(arg0);
		StringBuffer selectFields = new StringBuffer();
		selectFields.append(FIELDS_Meta[0]).append(",");
		selectFields.append(FIELDS_Meta[1]).append(",");
		selectFields.append(FIELDS_Meta[2]).append(",");
		selectFields.append(FIELDS_Meta[3]).append(",");
		selectFields.append(FIELDS_Meta[4]).append(",");
		selectFields.append(FIELDS_Meta[5]).append(",");
		selectFields.append(FIELDS_Meta[6]);
		// 根据查询条件查询数据
		DataSet queryDataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(), "dw_applybill", selectFields.toString(), qFilters, orderby);
		//ORMUtil.dumpDataSet(queryDataSet);// 打印数据
		//QFilter.and(f1, f2)
		DataSet queryDataSetCopy = queryDataSet.copy();
		// 处理数据
		// 动态加载需要添加的列
		for(Row row : queryDataSet.copy()) {
			int size = row.size();
			for(int i=0;i<size;i++) {
				if(i==4) {
					Object tempValue = row.get(i);
					if(!FIELDS_Dynamic.contains(String.valueOf(tempValue)) && !"0".equals(String.valueOf(tempValue))) {
						FIELDS_Dynamic.add(tempValue.toString());
						Caption_Dynamic.add("供应商ID:"+tempValue.toString());
						FieldType_Dynamic.add(DataType.BigDecimalType);
						/*if(tempValue instanceof Long) {
							FieldType_Dynamic.add(DataType.LongType);
						}else if(tempValue instanceof String) {
							FieldType_Dynamic.add(DataType.StringType);
						}else if(tempValue instanceof Date) {
							FieldType_Dynamic.add(DataType.DateType);
						}else if(tempValue instanceof BigDecimal) {
							FieldType_Dynamic.add(DataType.BigDecimalType);
						}*/
						
						
					}
				}
				
			}
			
		}
		
		// 根据列动态组装数据
		Collection<Object[]> collection = new ArrayList<Object[]>();
		
		for(Row row : queryDataSet.copy()) {
			Object[] tempRowValue = new Object[FIELDS_Dynamic.size()];
			int size = row.size();
			for(int i=0;i<size;i++) {
				if(i==4) {
					Object tempValue = row.get(i);
					if(FIELDS_Dynamic.contains(String.valueOf(tempValue))) {
						int indexOf = FIELDS_Dynamic.indexOf(String.valueOf(row.get(i)));
						//tempRowValue[indexOf] = row.get(alredyamount_indexof);
						tempRowValue[indexOf] = row.getBigDecimal(alredyamount_indexof);
					}
				}else if(i <4) {
					tempRowValue[i] = row.get(i);
				}/*else if(i > 4 && i !=6) {
					
				}*/
				
			}
			collection.add(tempRowValue);
			
		}
		// 构建动态列里面的值
		RowMeta rowMeta = RowMetaFactory.createRowMeta(FIELDS_Dynamic.toArray(new String [FIELDS_Dynamic.size()]), FieldType_Dynamic.toArray(new DataType[FieldType_Dynamic.size()]));
		CollectionInput inputs = new CollectionInput(rowMeta, collection);
		DataSet newDataSet = Algo.create(this.getClass().getName()).createDataSet(inputs);
		
		
		return newDataSet;
	}
	
	/**
	 * @Title: getFilter
	 * @Description: 获取过滤条件
	 * @author Fincher JF.Yuan
	 * @date: 2020年5月9日 下午3:52:25
	 * @param: @param arg0
	 * @param: @return 参数说明
	 * @return: QFilter[] 返回类型
	 * @throws
	 */
	private QFilter[] getFilter(ReportQueryParam arg0) {
		List<FilterItemInfo> filterItems = arg0.getFilter().getFilterItems();
		QFilter[] qFilters = new QFilter[filterItems.size()];
		for(FilterItemInfo filterItem : filterItems) {
			QFilter qFilter = null;
			String propName = filterItem.getPropName();
			Object value = filterItem.getValue();
			if("search_billno".equals(propName) && !StringUtils.isEmpty(value.toString())){
				qFilter = QFilter.of("billno = ?", value);
				qFilters[0] = qFilter;
				continue;
			}else if("search_date".equals(propName) && value!=null){
				qFilter = QFilter.of("date = ?", value);
				qFilters[1] = qFilter;
				continue;
			}
		}
		return qFilters;
	}

	/**
	 * @Title: getColumns
	 * @Description: 动态新增列
	 * @author: Fincher JF.Yuan
	 * @date: 2020年5月9日 下午3:52:39
	 * @param: @param columns
	 * @param: @return
	 * @param: @throws Throwable
	 * @see kd.bos.entity.report.AbstractReportListDataPlugin#getColumns(java.util.List)
	 */
	@Override
	public List<AbstractReportColumn> getColumns(List<AbstractReportColumn> columns) throws Throwable {
		super.getColumns(columns);
		columns.clear();
		for(int i=0 ; i< FIELDS_Dynamic.size();i++) {
			ReportColumn endRpt = new ReportColumn();
			endRpt.setCaption(new LocaleString(Caption_Dynamic.get(i).toString()));
			endRpt.setWidth(new LocaleString("200"));
			endRpt.setFieldKey(FIELDS_Dynamic.get(i).toString());
			endRpt.setFieldType(FieldType_Dynamic.get(i).toString());
			endRpt.setScale(100);
			columns.add(endRpt);
		}
		
		return columns;
	}
	
	
	

}
