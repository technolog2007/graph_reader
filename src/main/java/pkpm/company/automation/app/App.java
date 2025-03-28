package pkpm.company.automation.app;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import pkpm.company.automation.services.DefiningBookChanges;
import pkpm.company.automation.services.MakeSnapshot;
import pkpm.company.automation.services.ScheduleExecution;

@Slf4j
public class App {

  private static final String FILE_NAME = System.getenv("file_name");
//  private static final String FILE_NAME2 = System.getenv("file_name2");
  private static LocalDateTime END_TIME = LocalDateTime.of(2025, 03, 28, 17,0);


  public static void main(String[] args) {

    log.info("Start program time is : " + new Date());

    ScheduleExecution se = new ScheduleExecution();
    se.execute(FILE_NAME, 10, END_TIME); // час інтервалу в сек

    log.info("End program time is : " + new Date());
  }
}
