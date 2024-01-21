package io.inbank.loan.domain.profile.mapper;

import io.inbank.loan.domain.profile.command.GetCreditThresholdByPersonalCodeCommand;
import io.inbank.loan.domain.profile.serivce.ProfileRegisterApi;
import org.springframework.stereotype.Component;

@Component
public class ProfileRegisterMapper {

    public GetCreditThresholdByPersonalCodeCommand.Result toModel(ProfileRegisterApi.CreditThresholdResponse response) {
        if (response == null) {
            return null;
        }
        return GetCreditThresholdByPersonalCodeCommand.Result.builder()
                .personalCode(response.getPersonalCode())
                .hasDebt(response.getHasDebt())
                .segmentQualifier(response.getSegmentQualifier())
                .build();
    }
}
