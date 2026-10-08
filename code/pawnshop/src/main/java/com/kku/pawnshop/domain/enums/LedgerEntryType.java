package com.kku.pawnshop.domain.enums;

/** ประเภทรายการในสมุดบัญชีของตั๋ว ledger เป็น append-only ห้ามแก้ย้อนหลัง */
public enum LedgerEntryType {
    PAWN,
    INTEREST_PAYMENT,
    REDEMPTION
}
