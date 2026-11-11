package com.project.collections.model.manga;

import com.project.collections.model.Item;
import com.project.collections.model.ItemType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Concrete cultural item representing a manga volume or edition.
 * Stored in the 'mangas' table, joined with parent 'items' on 'id'.
 * A volume may belong to a {@link MangaCollection} through {@link #collection}.
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

    @Column(name = "volume_number")
    private Integer volumeNumber;

    @Size(max = 20)
    @Column(name = "isbn", length = 20, unique = true)
    private String isbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private MangaCollection collection;

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
                 Integer volumeNumber,
                 String isbn) {
        super(title, originalTitle, description, year, coverUrl);
        this.setType(ItemType.MANGA);
        this.author = author;
        this.artist = artist;
        this.volumeNumber = volumeNumber;
        this.isbn = normalizeIsbn(isbn);
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

    public Integer getVolumeNumber() {
        return volumeNumber;
    }

    public void setVolumeNumber(Integer volumeNumber) {
        this.volumeNumber = volumeNumber;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = normalizeIsbn(isbn);
    }

    public static String normalizeIsbn(String isbn) {
        return isbn == null ? null : isbn.replaceAll("[\\s-]", "");
    }

    public MangaCollection getCollection() {
        return collection;
    }

    public void setCollection(MangaCollection collection) {
        this.collection = collection;
    }

    public Long getCollectionId() {
        return collection == null ? null : collection.getId();
    }

}
