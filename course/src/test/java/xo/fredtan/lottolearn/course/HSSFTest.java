package xo.fredtan.lottolearn.course;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xo.fredtan.lottolearn.course.dao.SignRecordRepository;
import xo.fredtan.lottolearn.domain.course.SignRecord;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@SpringBootTest
public class HSSFTest {
    @Autowired
    private SignRecordRepository signRecordRepository;

    @Test
    public void createExcelFile() throws IOException {
        // meta
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] headers = {"用户ID", "用户名", "签到时间", "签到状态"};

        // data
        List<SignRecord> list = signRecordRepository.findBySignIdOrderBySignTimeDesc(3L);
        String[][] content = new String[list.size()][headers.length];
        int i = 0;
        for (SignRecord signRecord : list) {
            content[i][0] = signRecord.getUserId().toString();
            content[i][1] = signRecord.getUserNickname();
            content[i][2] = dateFormat.format(signRecord.getSignTime());
            content[i][3] = signRecord.getSuccess() ? "成功" : "失败";
        }

        // workbook
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
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

        // output
        FileOutputStream fos = new FileOutputStream("/Users/fred/Desktop/HSSFTest.xls");
        workbook.write(fos);
        fos.close();
    }
}
