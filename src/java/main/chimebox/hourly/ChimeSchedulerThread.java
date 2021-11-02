package chimebox.hourly;

import chimebox.logical.ClochesStop;
import chimebox.logical.HourlyChimeSwitch;
import chimebox.logical.Notes;
import chimebox.logical.Power;
import chimebox.logical.Volume;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ChimeSchedulerThread extends Thread {
  private final Logger logger = Logger.getLogger(ChimeSchedulerThread.class.getName());

  private final ScheduledExecutorService scheduler =
      Executors.newScheduledThreadPool(1);

  private final HourlyChimeSwitch hourlyChimeSwitch;
  private final Volume volume;
  private final Power power;
  private final Notes notes;
  private final ClochesStop clochesStop;

  public ChimeSchedulerThread(HourlyChimeSwitch hourlyChimeSwitch,
      Volume volume, Power power, Notes notes, ClochesStop clochesStop) {
    this.hourlyChimeSwitch = hourlyChimeSwitch;
    this.volume = volume;
    this.power = power;
    this.notes = notes;
    this.clochesStop = clochesStop;
  }

  @Override
  public void run() {
    PeriodicChimeRunnable runnable = new PeriodicChimeRunnable(hourlyChimeSwitch,
        power, volume, notes, clochesStop);
    LocalDateTime today = LocalDateTime.now();
    long initialDelayMillis = getMillisUntilNextChime(today);
    ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
        runnable,
        initialDelayMillis,
        TimeUnit.MINUTES.toMillis(15),
        TimeUnit.MILLISECONDS);
    logger.finer("Initial delay millis = " + initialDelayMillis);
    while (true) {
      try {
        Thread.sleep(Long.MAX_VALUE);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private long getMillisUntilNextChime(LocalDateTime today) {
    if (today.getMinute() == 0 && today.getSecond() == 0) {
      return 0;
    }
    LocalDateTime then;
    if (today.getMinute() < 15) {
      then = today.withMinute(15).withSecond(0).withNano(0);
    } else if (today.getMinute() < 30) {
      then = today.withMinute(30).withSecond(0).withNano(0);
    } else if (today.getMinute() < 45) {
      then = today.withMinute(45).withSecond(0).withNano(0);
    } else {
      then = today.plusHours(1).withMinute(0).withSecond(0).withNano(0);
    }
    return today.until(then, ChronoUnit.MILLIS);
  }
}
