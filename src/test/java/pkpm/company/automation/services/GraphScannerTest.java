package pkpm.company.automation.services;

import static org.junit.jupiter.api.Assertions.*;

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
}