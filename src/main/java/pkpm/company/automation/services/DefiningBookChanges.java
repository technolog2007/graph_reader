package pkpm.company.automation.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefiningBookChanges { // Визначення змін в книзі

  //  MakeSnapshot makeSnapshot = new MakeSnapshot(System.getenv("file_name"));
  MakeSnapshot ms1;
  MakeSnapshot ms2;

  public DefiningBookChanges(MakeSnapshot ms1, MakeSnapshot ms2) {
    this.ms1 = ms1;
    this.ms2 = ms2;
  }

  /**
   * Метод визначає, чи були додані нові вкладки!
   * @return
   */
  public List<String> getBookChanges() {
    Set<String> bookSheetsNames1 = ms1.getBs().getSheetsNames();
    Set<String> bookSheetsNames2 = ms2.getBs().getSheetsNames();
    if(bookSheetsNames1.size() < bookSheetsNames2.size()){
      return getListOfDifferentSheets(bookSheetsNames1, bookSheetsNames2);
    }
    log.warn("Відбулося очищення графіка, потрібне ручне перезавантаження!");
    return null;
  }

  private int determiningMatches() { //визначення збігів
    return 0;
  }

  /**
   * Метод порівнює два переліка сторінок графіка і повертає список імен що відрізняються
   * @param bookSheetsNames1
   * @param bookSheetsNames2
   * @return
   */
  private List<String> getListOfDifferentSheets(Set<String> bookSheetsNames1,
      Set<String> bookSheetsNames2) {
    return Stream.concat(
        bookSheetsNames1.stream().filter(e -> !bookSheetsNames2.contains(e)),
        bookSheetsNames2.stream().filter(e -> !bookSheetsNames1.contains(e))
    ).collect(Collectors.toList());
  }
}
