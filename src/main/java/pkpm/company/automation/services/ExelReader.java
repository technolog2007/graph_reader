package pkpm.company.automation.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Slf4j
public class ExelReader {

  public static void read(String fileName) {
    try (FileInputStream fis = new FileInputStream(new File(fileName));
        Workbook workbook = new XSSFWorkbook(fis)) {
      log.info("The file has been read successfully!!!");
      log.info("Numbers of sheets are: {}, ", workbook.getNumberOfSheets());

    } catch (IOException e) {
      log.warn("There are problems with the file: " + fileName);
    }
  }

}
