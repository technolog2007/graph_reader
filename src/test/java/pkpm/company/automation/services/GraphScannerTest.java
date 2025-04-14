package pkpm.company.automation.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class GraphScannerTest {

  static GraphScanner scanner;

  @BeforeAll
  static void create() {
    scanner = new GraphScanner();
  }

  @Test
  void scanning() {
//    scanner.scanning("Graph1.xlsx", 10, LocalDateTime.now().plusDays(1));
  }

  @Test
  void getSnapshotList() {
  }

  @Test
  void getDateList_returnEmptyList() {
    assertTrue(scanner.getDateList().isEmpty());
  }

  @Test
  void getDateList_returnList() {
    GraphScanner scanner = new GraphScanner();
    assertTrue(scanner.getDateList().isEmpty());
  }

  @Test
  void getCurrentTime_isBeforeCurrentTime() {
    assertTrue(scanner.getCurrentTime().isBefore(LocalDateTime.now()));
  }

  @Test
  void getKEY_1_returnActualValue() {
    assertEquals(scanner.getKEY_1(), "delSheets");
  }

  @Test
  void getKEY_2_returnActualValue() {
    assertEquals(scanner.getKEY_2(), "attachSheets");
  }

}