package com.ssafy.fiftyninesec.solution.dto;

import com.ssafy.fiftyninesec.solution.entity.Prize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrizeDto {
    private Long prizeId;
    private Long roomId;
    private String prizeType;
    private Integer winnerCount;
    private String prizeName;
    private Integer ranking;

    public PrizeDto PrizeDto(Prize prize) {
        return PrizeDto.builder()
                .prizeId(prize.getPrizeId())
                .prizeType(prize.getPrizeType())
                .winnerCount(prize.getWinnerCount() == null ? 0 : prize.getWinnerCount())
                .prizeName(prize.getPrizeName())
                .ranking(prize.getRanking())
                .build();
    }
}