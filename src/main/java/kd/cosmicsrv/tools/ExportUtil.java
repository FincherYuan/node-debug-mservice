package kd.cosmicsrv.tools;

import kd.bos.cache.CacheFactory;
import kd.bos.exception.BosErrorCode;
import kd.bos.exception.KDException;
import kd.bos.form.IFormView;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExportUtil {
    public static void exportFile(IFormView view, byte[] bytes, String fileName) {
        String url = CacheFactory.getCommonCacheFactory().getTempFileCache().saveAsUrl(fileName, bytes, 300000);
        view.download(url);
    }
    public static void exportExcel(IFormView view, XSSFWorkbook xssfWorkbook, String fileName) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            xssfWorkbook.write(os);
        } catch (IOException err) {
            throw new KDException(err, BosErrorCode.systemError, new Object[]{"将Excel转换为字节流失败."});
        }
        exportFile(view, os.toByteArray(), fileName.endsWith(".xlsx") ? fileName : fileName + ".xlsx");
    }
    
    public static void exportExcelForHSSF(IFormView view, HSSFWorkbook hssfWorkbook, String fileName) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
        	hssfWorkbook.write(os);
        } catch (IOException err) {
            throw new KDException(err, BosErrorCode.systemError, new Object[]{"将Excel转换为字节流失败."});
        }
        exportFile(view, os.toByteArray(), fileName.endsWith(".xls") ? fileName : fileName + ".xls");
    }
}
