package io.inbank.loan.domain.loan.api.external.model;

import io.inbank.loan.domain.loan.model.LoanDecision;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoanDecisionResponse {

    private String personalCode;

    private BigDecimal loanAmount;

    private Integer loanPeriod;

    @Schema(example = "POSITIVE", description = "POSITIVE = approved or approved with more suitable conditions. NEGATIVE = declined")
    private LoanDecision loanDecision;
}
