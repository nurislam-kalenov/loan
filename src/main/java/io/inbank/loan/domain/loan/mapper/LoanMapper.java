package io.inbank.loan.domain.loan.mapper;

import io.inbank.loan.domain.loan.api.external.model.LoanDecisionRequest;
import io.inbank.loan.domain.loan.api.external.model.LoanDecisionResponse;
import io.inbank.loan.domain.loan.command.GetLoanDecisionCommand;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public GetLoanDecisionCommand.Parameter toParams(LoanDecisionRequest request) {
        return GetLoanDecisionCommand.Parameter.builder()
                .personalCode(request.getPersonalCode())
                .loanAmount(request.getLoanAmount())
                .loanPeriod(request.getLoanPeriod()).build();
    }

    public LoanDecisionResponse toResponse(GetLoanDecisionCommand.Result result) {
        return LoanDecisionResponse.builder()
                .personalCode(result.personalCode())
                .loanAmount(result.approvedAmount())
                .loanPeriod(result.approvedPeriod())
                .loanDecision(result.decision())
                .build();
    }
}
