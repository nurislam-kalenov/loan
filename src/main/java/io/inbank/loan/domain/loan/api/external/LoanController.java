package io.inbank.loan.domain.loan.api.external;

import io.inbank.loan.domain.loan.api.external.model.LoanDecisionRequest;
import io.inbank.loan.domain.loan.api.external.model.LoanDecisionResponse;
import io.inbank.loan.domain.loan.command.GetLoanDecisionCommand;
import io.inbank.loan.domain.loan.mapper.LoanMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController("LoanController")
@RequestMapping(path = "/v1/loan")
@RequiredArgsConstructor
public class LoanController {

    private final GetLoanDecisionCommand getLoanDecisionCommand;
    private final LoanMapper loanMapper;

    @PostMapping("/decision")
    public ResponseEntity<LoanDecisionResponse> decision(@Valid @RequestBody LoanDecisionRequest request) {
        var decisionResult = getLoanDecisionCommand.execute(loanMapper.toParams(request));
        return new ResponseEntity<>(loanMapper.toResponse(decisionResult), HttpStatus.OK);
    }
}
