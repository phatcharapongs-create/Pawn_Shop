package com.kku.pawnshop.exception;

/** ผิดกฎทางธุรกิจ เช่น ขอวงเงินเกินที่ประเมินได้ GlobalExceptionHandler แปลงเป็น HTTP 400 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
