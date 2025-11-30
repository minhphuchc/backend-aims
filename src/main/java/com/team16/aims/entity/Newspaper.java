package com.team16.aims.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Newspaper")
public class Newspaper {

    @Id
    @Column(name = "media_id")
    private Integer mediaId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "media_id")
    private Media media;

    private String editorInChief;
    private String publisher;
    private java.sql.Date publicationDate;
    private String issueNumber;
    private String publicationFrequency;
    private String issn;
    private String sections;
}
