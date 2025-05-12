package pkpm.company.automation.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MessageWriter {

  private static final String FILE_NAME = getFileName();

  public static void writeList(String fileName, List<String> messages) {
    try (FileWriter fw = new FileWriter(fileName, false)) {
      for (String mes : messages) {
        fw.write(mes + "\n");
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Приймає і записує повідомлення в файл. Якщо файл відсутній, його буде створено, якщо ні -
   * буде просто дописаний рядок
   *
   * @param message - повідомлення, що записується в файл
   */
  public static void writeLine(String message) {
    try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
      fw.write(message + "\n");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getFileName() {
    String value = System.getenv("FILE_NAME");
    if (value != null) {
      return value;
    } else {
      throw new RuntimeException(
          "Please, check the configuration file, it does not contain the key \"FILE_NAME\"");
    }
  }

}
