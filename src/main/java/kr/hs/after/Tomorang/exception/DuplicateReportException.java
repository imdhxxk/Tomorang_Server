package kr.hs.after.Tomorang.exception;

/** 같은 사용자가 같은 대상을 중복 신고할 때 (→ HTTP 409) */
public class DuplicateReportException extends RuntimeException {
    public DuplicateReportException(String message) {
        super(message);
    }
}
