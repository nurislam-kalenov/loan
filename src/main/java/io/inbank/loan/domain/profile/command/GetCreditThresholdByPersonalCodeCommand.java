package io.inbank.loan.domain.profile.command;

import io.inbank.loan.common.Command;
import io.inbank.loan.domain.profile.mapper.ProfileRegisterMapper;
import io.inbank.loan.domain.profile.serivce.ProfileRegisterApi;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCreditThresholdByPersonalCodeCommand
        implements Command<String, Optional<GetCreditThresholdByPersonalCodeCommand.Result>> {

    private final ProfileRegisterApi profileRegisterApi;

    private final ProfileRegisterMapper mapper;

    @Override
    public Optional<Result> execute(String code) {
        return Optional.ofNullable(
                mapper.toModel(
                        profileRegisterApi.getCreditThresholdByPersonalCode(code)));
    }

    @Builder
    public record Result(String personalCode,
                         Boolean hasDebt,
                         ProfileRegisterApi.SegmentQualifier segmentQualifier) {
    }
}
