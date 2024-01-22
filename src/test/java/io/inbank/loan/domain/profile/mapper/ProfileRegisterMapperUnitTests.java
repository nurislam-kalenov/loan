package io.inbank.loan.domain.profile.mapper;

import io.inbank.loan.domain.profile.serivce.ProfileRegisterApi;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProfileRegisterMapperUnitTests {

    private final ProfileRegisterMapper profileRegisterMapper;

    ProfileRegisterMapperUnitTests() {
        this.profileRegisterMapper = new ProfileRegisterMapper();
    }

    @Test
    void onNullProfileRegisterResponseToGetLoanDecisionCommandParams() {
        assertThat(profileRegisterMapper.toModel(null)).isNull();
    }

    @Test
    void onFullProfileRegisterResponseToGetLoanDecisionCommandParams() {
        var givenResponse = ProfileRegisterApi.CreditThresholdResponse.builder()
                .hasDebt(true)
                .personalCode("010001")
                .segmentQualifier(ProfileRegisterApi.SegmentQualifier.SEGMENT_1)
                .build();
        var actualResult = profileRegisterMapper.toModel(givenResponse);

        assertAll(
                () -> assertThat(actualResult.hasDebt()).isEqualTo(givenResponse.getHasDebt()),
                () -> assertThat(actualResult.personalCode()).isEqualTo(givenResponse.getPersonalCode()),
                () -> assertThat(actualResult.segmentQualifier()).isEqualTo(givenResponse.getSegmentQualifier()));
    }
}
