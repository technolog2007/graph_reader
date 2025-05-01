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

  /**
   * Reads and saves a Workbook from a file
   *
   * @param fileName - book file name
   */
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

  /**
   * Close the book
   */
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

  /**
   * Returns all sheets of the book
   *
   * @return - list og book sheets
   */
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

  /**
   * Creates a copy from the original file
   *
   * @param originalPath - path to the original graph file
   * @return - path to the graph copy file
   * @throws IOException - exception input / output
   */
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

  /**
   * Deletes a temporary copy of the file
   *
   * @param path - path to the graph copy file
   */
  public static void deleteCopy(Path path) {
    try {
      Files.delete(path);
      log.info("Temporary file deleted successfully: {}", path);
    } catch (IOException e) {
      log.warn("Unable to delete temporary file: {}", path);
    }
  }
}
