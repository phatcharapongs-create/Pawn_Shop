package com.kku.pawnshop.domain.vo;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Money {
    
    private BigDecimal amount;

    protected Money() {}

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    // เขียน GETTER เอง
    public BigDecimal getAmount() {
        return amount;
    }
}