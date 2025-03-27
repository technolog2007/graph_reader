package pkpm.company.automation.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Slf4j
public class ExelReader {

  @Getter
  private static final List<Sheet> book = new ArrayList<>();
  @Getter
  private static Date bookDate;

  public static void read(String fileName) {
    File file = new File(fileName);
    try (FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis)) {
      log.info("The file has been read successfully!!!");
      log.info("Numbers of sheets are: {}, ", workbook.getNumberOfSheets());
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        Sheet sheet = workbook.getSheetAt(i);
        book.add(sheet);
      }
      bookDate = new Date(file.lastModified());
    } catch (IOException e) {
      log.warn("There are problems with the file: " + fileName);
    }
  }

  public static void clear() {
    book.clear();
    bookDate = null;
  }
}
