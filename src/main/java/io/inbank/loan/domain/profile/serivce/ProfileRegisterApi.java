package io.inbank.loan.domain.profile.serivce;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProfileRegisterApi {
    private final static Map<String, CreditThresholdResponse> CREDIT_THRESHOLD_BY_PERSONAL_CODE = new HashMap<>();

    @PostConstruct
    void init() {
        CREDIT_THRESHOLD_BY_PERSONAL_CODE.put("87103482516",
                CreditThresholdResponse.builder()
                        .personalCode("87103482516")
                        .hasDebt(true)
                        .build());
        CREDIT_THRESHOLD_BY_PERSONAL_CODE.put("59217648033",
                CreditThresholdResponse.builder()
                        .personalCode("59217648033")
                        .hasDebt(false)
                        .segmentQualifier(SegmentQualifier.SEGMENT_1)
                        .build());
        CREDIT_THRESHOLD_BY_PERSONAL_CODE.put("36490571280",
                CreditThresholdResponse.builder()
                        .personalCode("36490571280")
                        .hasDebt(false)
                        .segmentQualifier(SegmentQualifier.SEGMENT_2)
                        .build());
        CREDIT_THRESHOLD_BY_PERSONAL_CODE.put("20938465017",
                CreditThresholdResponse.builder()
                        .personalCode("20938465017")
                        .hasDebt(false)
                        .segmentQualifier(SegmentQualifier.SEGMENT_3)
                        .build());
        CREDIT_THRESHOLD_BY_PERSONAL_CODE.put("75321649008",
                CreditThresholdResponse.builder()
                        .personalCode("75321649008")
                        .hasDebt(false)
                        .segmentQualifier(null)
                        .build());


    }

    public CreditThresholdResponse getCreditThresholdByPersonalCode(String code) {
        return CREDIT_THRESHOLD_BY_PERSONAL_CODE.get(code);
    }

    @Builder
    @Getter
    public static class CreditThresholdResponse {

        @NotBlank
        private String personalCode;

        @NotNull
        private Boolean hasDebt;

        private SegmentQualifier segmentQualifier;

    }

    @Getter
    @RequiredArgsConstructor
    public enum SegmentQualifier {

        SEGMENT_1(BigDecimal.valueOf(100)),
        SEGMENT_2(BigDecimal.valueOf(300)),
        SEGMENT_3(BigDecimal.valueOf(1000));

        @NotNull
        public final BigDecimal creditModifier;
    }
}
