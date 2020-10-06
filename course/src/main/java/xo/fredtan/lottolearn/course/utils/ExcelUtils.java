package xo.fredtan.lottolearn.course.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.util.StringUtils;

public class ExcelUtils {
    /**
     * 创建Excel文件
     * @param sheetName 表格名
     * @param headers 表头
     * @param content 表体
     * @return HSSFWorkbook
     */
    public static HSSFWorkbook generateBasicExcelFile(String sheetName, String[] headers, String[][] content) {
        // workbook
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet;
        if (StringUtils.hasText(sheetName)) {
            sheet = workbook.createSheet(sheetName);
        } else {
            sheet = workbook.createSheet();
        }
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // headers
        for (int j = 0; j < headers.length; j++) {
            HSSFCell cell = row.createCell(j);
            cell.setCellValue(headers[j]);
            cell.setCellStyle(cellStyle);
        }
        // data
        for (int k = 0; k < content.length; k++) {
            HSSFRow sheetRow = sheet.createRow(k + 1);
            for (int l = 0; l < content[k].length; l++) {
                sheetRow.createCell(l).setCellValue(content[k][l]);
            }
        }
        return workbook;
    }
}
