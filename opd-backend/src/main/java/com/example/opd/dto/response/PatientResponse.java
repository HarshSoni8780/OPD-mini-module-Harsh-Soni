package com.example.opd.dto.response;

import com.example.opd.entity.Gender;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {

    private Long id;
    private String name;
    private Gender gender;
    private Integer age;
    private String phone;
    private LocalDateTime createdAt;
}
