package com.permithub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "hods")
@PrimaryKeyJoinColumn(name = "faculty_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HOD extends Faculty {

    @Column(name = "office_location")
    private String officeLocation;

    @Column(name = "appointment_date")
    private LocalDateTime appointmentDate;

    @Column(name = "tenure_end_date")
    private LocalDateTime tenureEndDate;

    @Column(name = "is_acting_hod")
    @lombok.Builder.Default
    private Boolean isActingHod = false;

    @Column(name = "signature_image")
    private String signatureImage;
}
