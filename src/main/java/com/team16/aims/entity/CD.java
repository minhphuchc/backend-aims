package com.team16.aims.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CD")
public class CD {

    @Id
    @Column(name = "media_id")
    private Integer mediaId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "media_id")
    private Media media;

    private String artists;
    private String recordLabel;
    private String genre;
    private java.sql.Date releaseDate;
}
