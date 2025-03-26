package pkpm.company.automation.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Slf4j
public class ExelReader {
  @Getter
  private static final List<Sheet> sheets = new ArrayList<>();
  public static void read(String fileName) {
    try (FileInputStream fis = new FileInputStream(new File(fileName));
        Workbook workbook = new XSSFWorkbook(fis)) {
      log.info("The file has been read successfully!!!");
      log.info("Numbers of sheets are: {}, ", workbook.getNumberOfSheets());
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        Sheet sheet = workbook.getSheetAt(i);
        sheets.add(sheet);
      }
    } catch (IOException e) {
      log.warn("There are problems with the file: " + fileName);
    }
  }
}
