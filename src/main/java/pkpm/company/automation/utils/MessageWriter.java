package pkpm.company.automation.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MessageWriter {

  private static final String FILE_NAME = "output.txt";

  public static void write(List<String> messages) {
    try (FileWriter fw = new FileWriter(new File(FILE_NAME))) {

      for (String mes : messages) {
        fw.write(mes + "\n");
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
