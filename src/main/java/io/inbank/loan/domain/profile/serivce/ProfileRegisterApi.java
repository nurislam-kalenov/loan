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
        CREDIT_THRESHOLD_BY_PERSONAL_CODE.put("49002010965",
                CreditThresholdResponse.builder()
                        .personalCode("49002010965")
                        .hasDebt(true)
                        .build());
        CREDIT_THRESHOLD_BY_PERSONAL_CODE.put("49002010976",
                CreditThresholdResponse.builder()
                        .personalCode("49002010976")
                        .hasDebt(false)
                        .segmentQualifier(SegmentQualifier.SEGMENT_1)
                        .build());
        CREDIT_THRESHOLD_BY_PERSONAL_CODE.put("49002010987",
                CreditThresholdResponse.builder()
                        .personalCode("49002010987")
                        .hasDebt(false)
                        .segmentQualifier(SegmentQualifier.SEGMENT_2)
                        .build());
        CREDIT_THRESHOLD_BY_PERSONAL_CODE.put("49002010998",
                CreditThresholdResponse.builder()
                        .personalCode("49002010998")
                        .hasDebt(false)
                        .segmentQualifier(SegmentQualifier.SEGMENT_3)
                        .build());
        CREDIT_THRESHOLD_BY_PERSONAL_CODE.put("49002010999",
                CreditThresholdResponse.builder()
                        .personalCode("49002010998")
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
