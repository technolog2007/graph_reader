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
  private final static String FILE_REPORT_GENERAL = "report.txt";

  /**
   * Determines the presence of a trigger file, and if it exists, searches for and writes changes to
   * the FILE_NAME file, namely tabs that have been added or removed, tabs that have undergone
   * changes
   *
   * @param graphName - full name of the graphic file
   */
  public void scanButtonPress(String graphName) {
    File file = new File(System.getenv("TRIGGER"));
    if (file.exists()) {
      try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
        String line = br.readLine();
        if (line != null && line.equals("ready")) {
          findChanges(graphName);
        }
      } catch (IOException e) {
        log.warn("Problem with file {}", graphName);
      } finally {
        deleteTrigger();
      }
    }
  }

  /**
   * Deletes the trigger file
   */
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

  /**
   * Creates the last snapshot from the graph and compares it with the previous one, and outputs the
   * result to the console and prints it to a file
   *
   * @param graphName - graphic file name
   */
  private void findChanges(String graphName) {
    try {
      newSnapshot = new MakeSnapshot(graphName).getBs();
    } catch (Exception e) {
      log.error("Snapshot creation failed: {}", e.getMessage());
      return;
    }
    definingAndWriteBookChange(oldSnapshot, newSnapshot);
    oldSnapshot = newSnapshot;
    newSnapshot = null;
  }

  /**
   * Determines what changes were made to the graph and displays the corresponding message to the
   * console and prints to a file
   *
   * @param oldSnapshot - previous snapshot of the graph
   * @param newSnapshot - next graph snapshot
   */
  private void definingAndWriteBookChange(BookSnapshot oldSnapshot, BookSnapshot newSnapshot) {
    if (oldSnapshot != null && newSnapshot != null) {
      BookChangesSearcher dbc = new BookChangesSearcher(oldSnapshot, newSnapshot);
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

  /**
   * Outputs a message to the console about the deleted graph sheets
   *
   * @param bookChanges - list of graph changes
   */
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

  /**
   * Generates and writes to a file messages about added letters in the graph
   *
   * @param bookChanges - list of graph changes
   */
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

  /**
   * Generates and writes to the file messages regarding added positions on the corresponding chart
   * pages
   *
   * @param bookChanges - list of changes in the graph on the relevant sheets
   */
  private void writeSheetsChanges(Map<String, List<Cell>> bookChanges) {
    if (bookChanges.isEmpty()) {
      log.info(GraphMessage.INFORM_NO_CHANGE.getMessage());
    } else if (bookChanges.keySet().size() == 1) {
      for (String sheet : bookChanges.keySet()) {
        String message = GraphMessage.INFORM_ADD_POSITION.getMessage() + "\"" + sheet + "\"";
        logAndWrite(message);
      }
    } else {
      List<String> sheets = new ArrayList<>(bookChanges.keySet());
      String message = GraphMessage.INFORM_ADD_POSITIONS.getMessage() + createMessageBody(sheets);
      logAndWrite(message);
    }
  }

  /**
   * Creates, in the desired format, the message body from the list of changed pages of the graph
   *
   * @param sheets - list of changed pages in the graph
   * @return - message body
   */
  private String createMessageBody(List<String> sheets) {
    return sheets.stream()
        .map(s -> "\"" + s + "\"")
        .collect(Collectors.joining(", "));
  }

  /**
   * Receives and prints an informational message to the console, and also writes it to a file from
   * which the bot will read.
   *
   * @param message - a generated message that will be output to the console and written to a file
   */
  private void logAndWrite(String message) {
    log.warn(message);
    MessageWriter.writeLine(message);
  }
}