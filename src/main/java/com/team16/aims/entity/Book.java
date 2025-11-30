package com.team16.aims.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Book")
public class Book {

    @Id
    @Column(name = "media_id")
    private Integer mediaId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "media_id")
    private Media media;

    private String authors;
    private String coverType;
    private String publisher;
    private java.sql.Date publicationDate;
    private Integer pages;
    private String language;
    private String genre;
}
