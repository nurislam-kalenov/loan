package io.inbank.loan.domain.loan.command;

import io.inbank.loan.common.Command;
import io.inbank.loan.domain.loan.model.LoanDecision;
import io.inbank.loan.domain.loan.model.LoanLimitations;
import io.inbank.loan.domain.profile.command.GetCreditThresholdByPersonalCodeCommand;
import io.inbank.loan.domain.profile.serivce.ProfileRegisterApi;
import io.inbank.loan.common.exception.EntityNotFoundException;
import io.inbank.loan.common.exception.InvalidOperationException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetLoanDecisionCommand
        implements Command<GetLoanDecisionCommand.Parameter, GetLoanDecisionCommand.Result> {

    private final GetCreditThresholdByPersonalCodeCommand getCreditThresholdByPersonalCodeCommand;
    private static final BigDecimal IDEAL_CREDIT_SCORE = BigDecimal.valueOf(LoanLimitations.IDEAL_CREDIT_SCORE);
    private static final BigDecimal MIN_ALLOWED_CREDIT_AMOUNT = BigDecimal.valueOf(LoanLimitations.MIN_ALLOWED_CREDIT_AMOUNT);
    private static final BigDecimal MAX_ALLOWED_CREDIT_AMOUNT = BigDecimal.valueOf(LoanLimitations.MAX_ALLOWED_CREDIT_AMOUNT);
    private static final int MAX_LOAN_PERIOD_IN_MONTH = LoanLimitations.MAX_LOAN_PERIOD_IN_MONTH;

    @Override
    public Result execute(Parameter parameters) {
        var personalCode = parameters.personalCode();

        var creditThreshold = getCreditThresholdByPersonalCodeCommand.execute(personalCode)
                .orElseThrow(() -> getProfileNotFoundException(personalCode));

        if (creditThreshold.hasDebt() == null || creditThreshold.hasDebt()) {
            return Result.builder()
                    .personalCode(personalCode)
                    .decision(LoanDecision.NEGATIVE)
                    .build();
        }

        var segmentQualifier = Optional.ofNullable(creditThreshold.segmentQualifier())
                .map(ProfileRegisterApi.SegmentQualifier::getCreditModifier)
                .orElseThrow(() -> getInvalidOperationException(personalCode));

        var creditScore = calculateCreditScore(segmentQualifier, parameters.loanAmount(), parameters.loanPeriod());

        if (creditScore.compareTo(IDEAL_CREDIT_SCORE) != 0) {
            return findSuitableAmountOrPeriod(personalCode,
                    parameters.loanPeriod(),
                    parameters.loanAmount(),
                    segmentQualifier);
        }

        return Result.builder()
                .personalCode(personalCode)
                .decision(LoanDecision.POSITIVE)
                .approvedAmount(parameters.loanAmount())
                .approvedPeriod(parameters.loanPeriod())
                .build();
    }

    private Result findSuitableAmountOrPeriod(String personalCode,
                                              int loanPeriod,
                                              BigDecimal loanAmount,
                                              BigDecimal segmentQualifier) {
        var approvedCreditAmount = calculateClosestSuitableLoanAmount(loanPeriod, segmentQualifier);

        if (approvedCreditAmount.compareTo(MIN_ALLOWED_CREDIT_AMOUNT) < 0) {
            var suitablePeriod = calculatesLoanSuitablePeriod(loanAmount, segmentQualifier);
            if (suitablePeriod > MAX_LOAN_PERIOD_IN_MONTH) {
                return Result.builder()
                        .personalCode(personalCode)
                        .decision(LoanDecision.NEGATIVE)
                        .build();
            }

            return Result.builder()
                    .personalCode(personalCode)
                    .approvedAmount(loanAmount)
                    .approvedPeriod(suitablePeriod)
                    .decision(LoanDecision.POSITIVE)
                    .build();

        } else if (approvedCreditAmount.compareTo(MAX_ALLOWED_CREDIT_AMOUNT) > 0) {
            return Result.builder()
                    .personalCode(personalCode)
                    .decision(LoanDecision.POSITIVE)
                    .approvedAmount(MAX_ALLOWED_CREDIT_AMOUNT)
                    .approvedPeriod(loanPeriod)
                    .build();
        }

        return Result.builder()
                .personalCode(personalCode)
                .decision(LoanDecision.POSITIVE)
                .approvedAmount(approvedCreditAmount)
                .approvedPeriod(loanPeriod)
                .build();
    }

    private BigDecimal calculateCreditScore(BigDecimal segmentQualifier, BigDecimal loanAmount, int loanPeriod) {
        return segmentQualifier.divide(loanAmount, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(loanPeriod));
    }

    private BigDecimal calculateClosestSuitableLoanAmount(int loanPeriod, BigDecimal creditModifier) {
        return creditModifier.multiply(BigDecimal.valueOf(loanPeriod));
    }

    private int calculatesLoanSuitablePeriod(BigDecimal loanAmount, BigDecimal creditModifier) {
        return loanAmount.multiply(IDEAL_CREDIT_SCORE)
                .divide(creditModifier, 2, RoundingMode.HALF_UP).intValue();
    }

    private EntityNotFoundException getProfileNotFoundException(String personalCode) {
        return new EntityNotFoundException(
                Parameter.class.getSimpleName(),
                "personalCode",
                personalCode,
                "Profile not found"
        );
    }

    private InvalidOperationException getInvalidOperationException(String personalCode) {
        return new InvalidOperationException(
                ProfileRegisterApi.class.getSimpleName(),
                "personalCode",
                personalCode,
                "Segment qualifier not found"
        );
    }

    @Builder
    public record Parameter(String personalCode,
                            BigDecimal loanAmount,
                            int loanPeriod) {
    }

    @Builder
    public record Result(String personalCode,
                         BigDecimal approvedAmount,
                         Integer approvedPeriod,
                         LoanDecision decision) {
    }
}
