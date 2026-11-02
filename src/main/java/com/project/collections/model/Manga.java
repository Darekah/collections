package com.project.collections.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Concrete cultural item representing a manga volume or series entry.
 * Stored in the 'mangas' table, joined with parent 'items' on 'id'.
 */
@Entity
@Table(name = "mangas")
@DiscriminatorValue("MANGA")
public class Manga extends Item {

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String author;

    @Size(max = 255)
    @Column(length = 255)
    private String artist;

    @Size(max = 100)
    @Column(length = 100)
    private String publisher;

    @Column(name = "volume_number")
    private Integer volumeNumber;

    @Column(name = "total_volumes")
    private Integer totalVolumes;

    @Size(max = 20)
    @Column(length = 20)
    private String isbn;

    @Column(name = "page_count")
    private Integer pageCount;

    @Size(max = 50)
    @Column(length = 50)
    private String format;

    protected Manga() {
        super();
        setType(ItemType.MANGA);
    }

    public Manga(String title,
                 String originalTitle,
                 String description,
                 Integer year,
                 String coverUrl,
                 String author,
                 String artist,
                 String publisher,
                 Integer volumeNumber,
                 Integer totalVolumes,
                 String isbn,
                 Integer pageCount,
                 String format) {
        super(title, originalTitle, description, year, coverUrl);
        this.setType(ItemType.MANGA);
        this.author = author;
        this.artist = artist;
        this.publisher = publisher;
        this.volumeNumber = volumeNumber;
        this.totalVolumes = totalVolumes;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.format = format;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getVolumeNumber() {
        return volumeNumber;
    }

    public void setVolumeNumber(Integer volumeNumber) {
        this.volumeNumber = volumeNumber;
    }

    public Integer getTotalVolumes() {
        return totalVolumes;
    }

    public void setTotalVolumes(Integer totalVolumes) {
        this.totalVolumes = totalVolumes;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
