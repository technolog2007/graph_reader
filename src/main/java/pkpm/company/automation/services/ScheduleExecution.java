package pkpm.company.automation.services;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduleExecution {

  public void execute(String fileName, long pauseTime, LocalDateTime endTime) {
    LocalDateTime currentTime = LocalDateTime.now();
    while (currentTime.isBefore(endTime)) {
      MakeSnapshot ms = new MakeSnapshot(fileName);
      log.info(ms.getBs().toString());
      pause(pauseTime);
      currentTime = LocalDateTime.now();
    }
  }


  private void pause(long min) {
    try {
      TimeUnit.SECONDS.sleep(min);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}

