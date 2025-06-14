// IReportManager.java
package service;

import java.time.LocalDate;

public interface IReportManager {
    void generateDailyReport(LocalDate date);
    void generateWeeklyReport(LocalDate startDate);
}
