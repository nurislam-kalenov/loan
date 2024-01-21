package io.inbank.loan.domain.loan;

import io.inbank.loan.common.exception.EntityNotFoundException;
import io.inbank.loan.common.exception.InvalidOperationException;
import io.inbank.loan.domain.loan.command.GetLoanDecisionCommand;
import io.inbank.loan.domain.loan.model.LoanDecision;
import io.inbank.loan.domain.profile.command.GetCreditThresholdByPersonalCodeCommand;
import io.inbank.loan.domain.profile.serivce.ProfileRegisterApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
public class GetLoanDecisionCommandUnitTests {
    @Mock
    private GetCreditThresholdByPersonalCodeCommand getCreditThresholdByPersonalCodeCommand;
    @InjectMocks
    private GetLoanDecisionCommand getLoanDecisionCommand;

    @Test
    void onIdealCreditScoreShouldReturnResult() {
        var givenPersonalCode = "0100001";
        var givenParams = GetLoanDecisionCommand.Parameter.builder()
                .personalCode(givenPersonalCode)
                .loanAmount(BigDecimal.valueOf(100))
                .loanPeriod(1)
                .build();

        when(getCreditThresholdByPersonalCodeCommand.execute(givenPersonalCode))
                .thenReturn(Optional.of(GetCreditThresholdByPersonalCodeCommand.Result.builder()
                        .hasDebt(false)
                        .personalCode(givenPersonalCode)
                        .segmentQualifier(ProfileRegisterApi.SegmentQualifier.SEGMENT_1)
                        .build()));

        var actualResult = getLoanDecisionCommand.execute(givenParams);

        assertAll(
                () -> assertThat(actualResult.personalCode()).isEqualTo(givenPersonalCode),
                () -> assertThat(actualResult.decision()).isEqualTo(LoanDecision.POSITIVE),
                () -> assertThat(actualResult.approvedAmount()).isEqualTo(givenParams.loanAmount()),
                () -> assertThat(actualResult.approvedPeriod()).isEqualTo(givenParams.loanPeriod())
        );
    }


    @Test
    void onLowCreditScoreShouldReturnSuitableLoanPeriod() {
        var givenPersonalCode = "0100001";
        var givenParams = GetLoanDecisionCommand.Parameter.builder()
                .personalCode(givenPersonalCode)
                .loanAmount(BigDecimal.valueOf(4000))
                .loanPeriod(13)
                .build();

        when(getCreditThresholdByPersonalCodeCommand.execute(givenPersonalCode))
                .thenReturn(Optional.of(GetCreditThresholdByPersonalCodeCommand.Result.builder()
                        .hasDebt(false)
                        .personalCode(givenPersonalCode)
                        .segmentQualifier(ProfileRegisterApi.SegmentQualifier.SEGMENT_1)
                        .build()));

        var actualResult = getLoanDecisionCommand.execute(givenParams);

        assertAll(
                () -> assertThat(actualResult.personalCode()).isEqualTo(givenPersonalCode),
                () -> assertThat(actualResult.decision()).isEqualTo(LoanDecision.POSITIVE),
                () -> assertThat(actualResult.approvedAmount()).isEqualTo(givenParams.loanAmount()),
                () -> assertThat(actualResult.approvedPeriod()).isEqualTo(40)
        );
    }

    @Test
    void onLowCreditScoreAndNoSuitableLoanPeriodShouldReturnNegative() {
        var givenPersonalCode = "0100001";
        var givenParams = GetLoanDecisionCommand.Parameter.builder()
                .personalCode(givenPersonalCode)
                .loanAmount(BigDecimal.valueOf(10000))
                .loanPeriod(13)
                .build();

        when(getCreditThresholdByPersonalCodeCommand.execute(givenPersonalCode))
                .thenReturn(Optional.of(GetCreditThresholdByPersonalCodeCommand.Result.builder()
                        .hasDebt(false)
                        .personalCode(givenPersonalCode)
                        .segmentQualifier(ProfileRegisterApi.SegmentQualifier.SEGMENT_1)
                        .build()));

        var actualResult = getLoanDecisionCommand.execute(givenParams);

        assertAll(
                () -> assertThat(actualResult.personalCode()).isEqualTo(givenPersonalCode),
                () -> assertThat(actualResult.decision()).isEqualTo(LoanDecision.NEGATIVE),
                () -> assertThat(actualResult.approvedAmount()).isNull(),
                () -> assertThat(actualResult.approvedPeriod()).isNull()
        );
    }

    @Test
    void onHighCreditScoreShouldReturnMaxPossibleAmount() {
        var givenPersonalCode = "0100001";
        var givenParams = GetLoanDecisionCommand.Parameter.builder()
                .personalCode(givenPersonalCode)
                .loanAmount(BigDecimal.valueOf(3000))
                .loanPeriod(13)
                .build();

        when(getCreditThresholdByPersonalCodeCommand.execute(givenPersonalCode))
                .thenReturn(Optional.of(GetCreditThresholdByPersonalCodeCommand.Result.builder()
                        .hasDebt(false)
                        .personalCode(givenPersonalCode)
                        .segmentQualifier(ProfileRegisterApi.SegmentQualifier.SEGMENT_2)
                        .build()));

        var actualResult = getLoanDecisionCommand.execute(givenParams);

        assertAll(
                () -> assertThat(actualResult.personalCode()).isEqualTo(givenPersonalCode),
                () -> assertThat(actualResult.decision()).isEqualTo(LoanDecision.POSITIVE),
                () -> assertThat(actualResult.approvedAmount()).isEqualTo(BigDecimal.valueOf(3900)),
                () -> assertThat(actualResult.approvedPeriod()).isEqualTo(13)
        );
    }

    @Test
    void onHighCreditScoreAndPossibleAmountMoreThenMaxLimitShouldReturnMaxLimit() {
        var givenPersonalCode = "0100001";
        var givenParams = GetLoanDecisionCommand.Parameter.builder()
                .personalCode(givenPersonalCode)
                .loanAmount(BigDecimal.valueOf(2000))
                .loanPeriod(13)
                .build();

        when(getCreditThresholdByPersonalCodeCommand.execute(givenPersonalCode))
                .thenReturn(Optional.of(GetCreditThresholdByPersonalCodeCommand.Result.builder()
                        .hasDebt(false)
                        .personalCode(givenPersonalCode)
                        .segmentQualifier(ProfileRegisterApi.SegmentQualifier.SEGMENT_3)
                        .build()));

        var actualResult = getLoanDecisionCommand.execute(givenParams);

        assertAll(
                () -> assertThat(actualResult.personalCode()).isEqualTo(givenPersonalCode),
                () -> assertThat(actualResult.decision()).isEqualTo(LoanDecision.POSITIVE),
                () -> assertThat(actualResult.approvedAmount()).isEqualTo(BigDecimal.valueOf(10000)),
                () -> assertThat(actualResult.approvedPeriod()).isEqualTo(13)
        );
    }


    @Test
    void onHasDebtTrueShouldReturnNegativeResponse() {
        var givenPersonalCode = "0100001";
        var givenParams = GetLoanDecisionCommand.Parameter.builder()
                .personalCode(givenPersonalCode)
                .loanAmount(BigDecimal.valueOf(100))
                .loanPeriod(1)
                .build();

        when(getCreditThresholdByPersonalCodeCommand.execute(givenPersonalCode))
                .thenReturn(Optional.of(GetCreditThresholdByPersonalCodeCommand.Result.builder()
                        .hasDebt(true)
                        .personalCode(givenPersonalCode)
                        .segmentQualifier(ProfileRegisterApi.SegmentQualifier.SEGMENT_1)
                        .build()));

        var actualResult = getLoanDecisionCommand.execute(givenParams);

        assertAll(
                () -> assertThat(actualResult.personalCode()).isEqualTo(givenPersonalCode),
                () -> assertThat(actualResult.decision()).isEqualTo(LoanDecision.NEGATIVE),
                () -> assertThat(actualResult.approvedAmount()).isNull(),
                () -> assertThat(actualResult.approvedPeriod()).isNull()
        );
    }

    @Test
    void onProfileNotFoundShouldThrowException() {
        var givenPersonalCode = "0100001";
        var givenParams = GetLoanDecisionCommand.Parameter.builder()
                .personalCode(givenPersonalCode)
                .loanAmount(BigDecimal.valueOf(2000))
                .loanPeriod(13)
                .build();

        when(getCreditThresholdByPersonalCodeCommand.execute(givenPersonalCode)).thenReturn(Optional.empty());

        var actualException = assertThrows(EntityNotFoundException.class, () -> getLoanDecisionCommand.execute(givenParams));

        assertAll(
                () -> assertThat(actualException.getEntity()).isEqualTo(GetLoanDecisionCommand.Parameter.class.getSimpleName()),
                () -> assertThat(actualException.getField()).isEqualTo("personalCode"),
                () -> assertThat(actualException.getValue()).isEqualTo(givenPersonalCode),
                () -> assertThat(actualException.getMessage()).isEqualTo("Profile not found")
        );
    }

    @Test
    void onSegmentNotFoundShouldThrowException() {
        var givenPersonalCode = "0100001";
        var givenParams = GetLoanDecisionCommand.Parameter.builder()
                .personalCode(givenPersonalCode)
                .loanAmount(BigDecimal.valueOf(2000))
                .loanPeriod(13)
                .build();

        when(getCreditThresholdByPersonalCodeCommand.execute(givenPersonalCode)).thenReturn(
                Optional.of(GetCreditThresholdByPersonalCodeCommand.Result.builder()
                        .hasDebt(false)
                        .personalCode(givenPersonalCode)
                        .build()));

        var actualException = assertThrows(InvalidOperationException.class, () -> getLoanDecisionCommand.execute(givenParams));

        assertAll(
                () -> assertThat(actualException.getEntity()).isEqualTo(ProfileRegisterApi.class.getSimpleName()),
                () -> assertThat(actualException.getField()).isEqualTo("personalCode"),
                () -> assertThat(actualException.getValue()).isEqualTo(givenPersonalCode),
                () -> assertThat(actualException.getMessage()).isEqualTo("Segment qualifier not found")
        );
    }

}
