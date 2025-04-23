package pkpm.company.automation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

@Slf4j
public class ExelReader {

  @Getter
  private static Workbook workbook;
  @Getter
  private static long fileDate;

  private static final String PATH = System.getenv("TEMPORARY_PATH");

  public static void read(String fileName) {
    File file = new File(fileName);
    if (!file.canRead()) {
      log.warn("File is not readable: " + fileName);
      return;
    }
    try (FileInputStream fis = new FileInputStream(file)) {
      workbook = WorkbookFactory.create(fis);
      fileDate = file.lastModified();
      log.info("The file has been read successfully!!! Sheets: {}", workbook.getNumberOfSheets());
    } catch (IOException e) {
      log.warn("There are problems with the file: " + fileName);
    }
  }

  public static void closeWorkbook() {
    if (workbook != null) {
      try {
        workbook.close();
        log.info("Workbook closed successfully");
      } catch (IOException e) {
        log.warn("Failed to close workbook: {}", e.getMessage());
      }
    }
  }

  public static List<Sheet> getSheets() {
    List<Sheet> bookSheets = new ArrayList<>();
    if (workbook == null) {
      throw new IllegalStateException("Workbook has not been initialized. Call read() first.");
    }
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      bookSheets.add(workbook.getSheetAt(i));
    }
    return bookSheets;
  }

  public static Path createTempCopy(Path originalPath) throws IOException {
    Path tempFile = Files.createTempFile(Path.of(PATH), "copy_", ".xlsx");
    log.info("Temporary copy created successfully: {}", tempFile);
    if (Files.isReadable(originalPath)) {
      Files.copy(originalPath, tempFile, StandardCopyOption.REPLACE_EXISTING);
    } else {
      throw new IOException("File is not readable: " + originalPath);
    }
    return tempFile;
  }

  public static void deleteCopy(Path path) {
    try {
      Files.delete(path);
      log.info("Temporary file deleted successfully: {}", path);
    } catch (IOException e) {
      log.warn("Unable to delete temporary file: {}", path);
    }
  }
}
