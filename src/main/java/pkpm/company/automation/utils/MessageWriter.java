package pkpm.company.automation.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MessageWriter {

  private static final String FILE_NAME = "output.txt";

  public static void writeList(List<String> messages) {
    try (FileWriter fw = new FileWriter(FILE_NAME, true)) {

      for (String mes : messages) {
        fw.write(mes + "\n");
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void writeLine(String message) {
    try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
      fw.write(message + "\n");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
