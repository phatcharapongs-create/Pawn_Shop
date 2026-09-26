package com.kku.pawnshop.exception;

/** หาข้อมูลที่ร้องขอไม่พบ GlobalExceptionHandler จะแปลงเป็น HTTP 404 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super("ไม่พบ " + resource + " รหัส " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
