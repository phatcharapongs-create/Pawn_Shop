package com.kku.pawnshop.dto.pledged;

import com.kku.pawnshop.domain.enums.ItemType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor
public class PledgedItemRequest {
    @NotNull private ItemType itemType;
    @NotBlank @Size(max = 300) private String description;
    @Size(max = 100) private String serialNumber;
    @Positive private BigDecimal weightGram;
    @DecimalMin("0.01") @DecimalMax("100.00") private BigDecimal purityPercent;
    @Min(1900) private Integer manufactureYear;
    @DecimalMin("0.01") private BigDecimal referencePrice;
    @Min(1) @Max(5) private Integer conditionGrade;
    @Size(max = 30) private String storageSlot;
    @Size(max = 300) private String photoUrl;
    @Positive private Long appraiserId;
}
