package pkpm.company.automation.utils;

import java.io.IOException;
import java.nio.file.*;

public class FileWriter {
  private static final String FILE_NAME = System.getenv("FILE_NAME");

  public static void main(String[] args) throws IOException {
    Path path = Paths.get(FILE_NAME); // Файл, який скануємо
    WatchService watchService = FileSystems.getDefault().newWatchService();
    path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

    while (true) {
      WatchKey key;
      try {
        key = watchService.take(); // Очікуємо змін
      } catch (InterruptedException e) {
        return;
      }

      for (WatchEvent<?> event : key.pollEvents()) {
        if (event.context().toString().equals(path.getFileName().toString())) {
          writeToFile("Файл змінено: " + System.currentTimeMillis());
        }
      }
      key.reset();
    }
  }

  private static void writeToFile(String message) {
    try (java.io.FileWriter writer = new java.io.FileWriter(FILE_NAME, true)) {
      writer.write(message + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
