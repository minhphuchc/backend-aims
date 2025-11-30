package com.team16.aims.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CD_Tracks")
public class CDTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer trackId;

    @ManyToOne
    @JoinColumn(name = "media_id")
    private CD cd;

    private String title;

    private java.sql.Time length;
}
