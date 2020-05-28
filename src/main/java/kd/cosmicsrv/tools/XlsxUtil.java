package kd.cosmicsrv.tools;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;

public class XlsxUtil {
    public static void setRowStyle(XSSFRow row, XSSFCellStyle cellStyle) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            row.getCell(i).setCellStyle(cellStyle);
        }
    }

    public static void setBorderStyle (XSSFCellStyle cellStyle, BorderStyle borderStyle) {
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
    }

	public static void setRowStyle(HSSFRow row, HSSFCellStyle alignStyle) {
		// TODO Auto-generated method stub
		for (int i = 0; i < row.getLastCellNum(); i++) {
            row.getCell(i).setCellStyle(alignStyle);
        }
	}
}
