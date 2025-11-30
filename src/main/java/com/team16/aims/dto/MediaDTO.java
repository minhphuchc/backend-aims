package com.team16.aims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaDTO {
    private Integer mediaId;
    private String title;
    private String category;
    private Double value;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private String description;
    private Float weight;
    private String dimensions;
    private String barcode;
    private String mediaType; // Book, CD, DVD, Newspaper

    // Book fields
    private String authors;
    private String coverType;
    private String publisher;
    private Date publicationDate;
    private Integer pages;
    private String language;
    private String genre;

    // CD fields
    private String artists;
    private String recordLabel;
    // genre is shared with Book and DVD
    private Date releaseDate;

    // DVD fields
    private String discType;
    private String director;
    private Integer runtime;
    private String studio;
    // language is shared with Book
    private String subtitles;
    // releaseDate is shared with CD

    // Newspaper fields
    private String editorInChief;
    // publisher is shared with Book
    // publicationDate is shared with Book
    private String issueNumber;
    private String publicationFrequency;
    private String issn;
    private String sections;
}
