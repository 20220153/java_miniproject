package util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Scanner;

// 콘솔 입력 및 출력 관련 유틸리티
public class ConsoleUtil {

    private static Scanner scanner = new Scanner(System.in);

    // 문자열 입력 받기
    public static String readLine(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine();
    }

    // 정수 입력 받기 (유효성 검사 포함)
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            try {
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("오류: 숫자를 입력해주세요.");
            }
        }
    }

    // 실수 입력 받기 (유효성 검사 포함)
    public static double readDouble(String prompt) {
         while (true) {
            System.out.print(prompt + ": ");
            try {
                double value = Double.parseDouble(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("오류: 숫자를 입력해주세요.");
            }
        }
    }

    // 날짜 입력 받기 (형식: YYYY-MM-DD)
public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String input = scanner.nextLine();
            try {
                return LocalDate.parse(input); // 입력된 문자열을 LocalDate로 변환
            } catch (Exception e) {
                System.out.println("오류: 날짜 형식이 올바르지 않습니다.");
            }
        }
    }

    // 금액 포맷팅 (원화)
    public static String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###원");
        return formatter.format(amount);
    }

    public static String formatCurrency(long amount) {
        DecimalFormat formatter = new DecimalFormat("#,###원");
        return formatter.format(amount);
    }

    // Scanner 닫기 (애플리케이션 종료 시 호출 필요)
    public static void closeScanner() {
        scanner.close();
    }
}