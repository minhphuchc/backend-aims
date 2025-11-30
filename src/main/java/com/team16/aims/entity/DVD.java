package com.team16.aims.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DVD")
public class DVD {

    @Id
    @Column(name = "media_id")
    private Integer mediaId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "media_id")
    private Media media;

    private String discType;
    private String director;
    private Integer runtime;
    private String studio;
    private String language;
    private String subtitles;
    private java.sql.Date releaseDate;
    private String genre;
}
