package com.permithub.dto.hod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterPromotionDTO {
    private Long fromSemesterId;
    private Long toSemesterId;
    private Integer fromYear;
    private Integer toYear;
    private String fromSection;
    private String toSection;
    private List<Long> excludeStudentIds;
    private String remarks;
}