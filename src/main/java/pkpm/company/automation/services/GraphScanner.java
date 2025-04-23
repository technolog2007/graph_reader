package pkpm.company.automation.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import pkpm.company.automation.models.BookSnapshot;
import pkpm.company.automation.models.GraphMessage;
import pkpm.company.automation.utils.MessageWriter;

@Slf4j
@Getter
@Setter
public class GraphScanner {

  private BookSnapshot oldSnapshot;
  private BookSnapshot newSnapshot;
  private static final String KEY_1 = "delSheets";
  private static final String KEY_2 = "addSheets";

  public void scanButtonPress(String graphName) {
    File file = new File(System.getenv("TRIGGER"));
    if (file.exists()) {
      try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
        String line = br.readLine();
        if (line != null && line.equals("ready")) {
          printChanges(graphName);
        }
      } catch (IOException e) {
        log.warn("Problem with file {}", graphName);
      } finally {
        deleteTrigger();
      }
    }
  }

  private void deleteTrigger() {
    try {
      Path path = Path.of(System.getenv("TRIGGER"));
      log.info(path.toString());
      Files.delete(path);
      log.info("Trigger file deleted successfully");
    } catch (IOException e) {
      log.warn("Unable to delete trigger file, because {}", e.getMessage());
    }
  }

  private void printChanges(String graphName) {
    try {
      newSnapshot = new MakeSnapshot(graphName).getBs();
    } catch (Exception e) {
      log.error("Snapshot creation failed: {}", e.getMessage());
      return;
    }
    definingBookChange(oldSnapshot, newSnapshot);
    oldSnapshot = newSnapshot;
    newSnapshot = null;
  }

  private void definingBookChange(BookSnapshot oldSnapshot, BookSnapshot newSnapshot) {
    if (oldSnapshot != null && newSnapshot != null) {
      DefiningBookChanges dbc = new DefiningBookChanges(oldSnapshot, newSnapshot);
      Map<String, List<String>> bookChanges = dbc.getBookChanges();
      if (!bookChanges.get(KEY_1).isEmpty()) {
        printDelBookChanges(bookChanges);
      }
      if (!bookChanges.get(KEY_2).isEmpty()) {
        writeAttachBookChanges(bookChanges.get(KEY_2));
      }
      writeSheetsChanges(dbc.getSheetsChanges(oldSnapshot, newSnapshot));
    }
  }

  private void printDelBookChanges(Map<String, List<String>> bookChanges) {
    if (bookChanges.get(KEY_1).size() == 1) {
      String message =
          GraphMessage.INFORM_DELETE_FOLDER.getMessage() + "\"" + bookChanges.get(KEY_1).get(0)
              + "\"";
      log.warn(message);
    } else {
      String message =
          GraphMessage.INFORM_DELETE_FOLDERS.getMessage() + createMessageBody(
              bookChanges.get(KEY_1));
      log.warn(message);
    }
  }

  private void writeAttachBookChanges(List<String> bookChanges) {
    if (bookChanges.size() == 1) {
      String message =
          GraphMessage.INFORM_ADD_FOLDER.getMessage() + "\"" + bookChanges.get(0) + "\"";
      logAndWrite(message);
    }
    if (bookChanges.size() > 1) {
      String message =
          GraphMessage.INFORM_ADD_FOLDERS.getMessage() + createMessageBody(bookChanges);
      logAndWrite(message);
    }
  }

  private void writeSheetsChanges(Map<String, List<Cell>> changes) {
    if (changes.isEmpty()) {
      log.info(GraphMessage.INFORM_NO_CHANGE.getMessage());
    } else if (changes.keySet().size() == 1) {
      for (String sheet : changes.keySet()) {
        String message = GraphMessage.INFORM_ADD_POSITION.getMessage() + "\"" + sheet + "\"";
        logAndWrite(message);
      }
    } else {
      List<String> sheets = new ArrayList<>(changes.keySet());
      String message = GraphMessage.INFORM_ADD_POSITIONS.getMessage() + createMessageBody(sheets);
      logAndWrite(message);
    }
  }

  /**
   * Створює, у потрібному форматі, тіло повідомлення зі списка змінених сторінок книги
   *
   * @param sheets - список змінених сторінок книги
   * @return - тіло повідомлення
   */
  private String createMessageBody(List<String> sheets) {
    return sheets.stream()
        .map(s -> "\"" + s + "\"")
        .collect(Collectors.joining(", "));
  }

  private void logAndWrite(String message) {
    log.warn(message);
    MessageWriter.writeLine(message);
  }
}