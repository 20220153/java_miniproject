package service;

import model.Receipt;
import util.ConsoleUtil; // 금액 포맷팅 위해 사용
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

// 보고서 생성 (간소화된 버전)
public class ReportManager implements IReportManager {
    private ReceiptManager receiptManager; // 영수증 관리 객체

    // 생성자
    public ReportManager(ReceiptManager receiptManager) {
        this.receiptManager = receiptManager; // 영수증 관리 객체 초기화
    }

    // 일간 보고서 생성
    public void generateDailyReport(LocalDate date) {
        List<Receipt> dailyReceipts = receiptManager.getAllReceipts().stream()
                .filter(receipt -> receipt.getDate().equals(date)) // 해당 날짜의 영수증 필터링
                .collect(Collectors.toList());

        if (dailyReceipts.isEmpty()) {
            System.out.println("해당 날짜에 판매 기록이 없습니다.");
            return;
        }

        // 일간 보고서 출력
        System.out.println("\n=== 일간 보고서 ===");
        System.out.println("날짜: " + date);
        System.out.println("총 판매 금액: " + ConsoleUtil.formatCurrency(dailyReceipts.stream()
                .mapToInt(Receipt::getTotalAmount).sum())); // 총 판매 금액 계산 및 포맷팅
    }

    // 주간 보고서 생성
    public void generateWeeklyReport(LocalDate startDate) {
        LocalDate endDate = startDate.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)); // 주간 종료일 계산
        List<Receipt> weeklyReceipts = receiptManager.getAllReceipts().stream()
                .filter(receipt -> !receipt.getDate().isBefore(startDate) && !receipt.getDate().isAfter(endDate)) // 해당 주의 영수증 필터링
                .collect(Collectors.toList());

        if (weeklyReceipts.isEmpty()) {
            System.out.println("해당 주에 판매 기록이 없습니다.");
            return;
        }

        // 주간 보고서 출력
        System.out.println("\n=== 주간 보고서 ===");
        System.out.println("주간 시작일: " + startDate + ", 종료일: " + endDate);
        System.out.println("총 판매 금액: " + ConsoleUtil.formatCurrency(weeklyReceipts.stream()
                .mapToInt(Receipt::getTotalAmount).sum())); // 총 판매 금액 계산 및 포맷팅
    }

}
