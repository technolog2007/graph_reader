package pkpm.company.automation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Slf4j
public class ExelReader {

  @Getter
  private static Date bookDate;
  @Getter
  private static Workbook workbook;

  public static void read(String fileName) {
    File file = new File(fileName);
    try (FileInputStream fis = new FileInputStream(file);
        Workbook wb = new XSSFWorkbook(fis)) {
      log.info("The file has been read successfully!!! " + wb.getNumberOfSheets());
      workbook = wb;
      bookDate = new Date(file.lastModified());
    } catch (IOException e) {
      log.warn("There are problems with the file: " + fileName);
    }
  }

  public static List<Sheet> getSheets() {
    List<Sheet> bookSheets = new ArrayList<>();
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      bookSheets.add(workbook.getSheetAt(i));
    }
    return bookSheets;
  }
}
