package io.inbank.loan.domain.profile.command;

import io.inbank.loan.domain.profile.mapper.ProfileRegisterMapper;
import io.inbank.loan.domain.profile.serivce.ProfileRegisterApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCreditThresholdByPersonalCodeCommandUnitTests {
    @Mock
    private ProfileRegisterApi profileRegisterApi;
    @Spy
    private ProfileRegisterMapper mapper;
    @InjectMocks
    private GetCreditThresholdByPersonalCodeCommand getCreditThresholdByPersonalCodeCommand;

    @Test
    void onFullAndCorrectInputShouldReturnResult() {
        var givenPersonalCode = "01000101";
        var givenResponse = ProfileRegisterApi.CreditThresholdResponse.builder()
                .personalCode(givenPersonalCode)
                .segmentQualifier(ProfileRegisterApi.SegmentQualifier.SEGMENT_1)
                .hasDebt(true)
                .build();

        when(profileRegisterApi.getCreditThresholdByPersonalCode(givenPersonalCode)).thenReturn(givenResponse);

        var actualResult = getCreditThresholdByPersonalCodeCommand.execute(givenPersonalCode).get();
        assertAll(
                () -> assertThat(actualResult.hasDebt()).isEqualTo(givenResponse.getHasDebt()),
                () -> assertThat(actualResult.personalCode()).isEqualTo(givenResponse.getPersonalCode()),
                () -> assertThat(actualResult.segmentQualifier()).isEqualTo(givenResponse.getSegmentQualifier()));
    }

    @Test
    void onFullAndCorrectInputButNullResponseShouldReturnNullResult() {
        var givenPersonalCode = "01000101";
        when(profileRegisterApi.getCreditThresholdByPersonalCode(givenPersonalCode)).thenReturn(null);
        var actualResult = getCreditThresholdByPersonalCodeCommand.execute(givenPersonalCode);
        assertThat(actualResult.isEmpty()).isTrue();
    }

}
