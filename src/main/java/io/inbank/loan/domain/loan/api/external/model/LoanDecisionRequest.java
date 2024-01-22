package io.inbank.loan.domain.loan.api.external.model;

import io.inbank.loan.domain.loan.model.LoanLimitations;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@Builder
public class LoanDecisionRequest {

    @NotBlank
    @Schema(example = "20938465017", description = "personal code of client")
    private String personalCode;

    @NotNull
    @DecimalMin(value = LoanLimitations.MIN_ALLOWED_CREDIT_AMOUNT + ".0", inclusive = false)
    @DecimalMax(value = LoanLimitations.MAX_ALLOWED_CREDIT_AMOUNT + ".0")
    @Digits(integer = 5, fraction = 3)
    @Schema(example = "3500.123")
    private BigDecimal loanAmount;

    @NotNull
    @Min(LoanLimitations.MIN_LOAN_PERIOD_IN_MONTH)
    @Max(LoanLimitations.MAX_LOAN_PERIOD_IN_MONTH)
    @Schema(example = "12", description = "loan period in month")
    private int loanPeriod;
}
