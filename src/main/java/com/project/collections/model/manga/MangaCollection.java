package com.project.collections.model.manga;

import com.project.collections.model.Item;
import com.project.collections.model.ItemType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Concrete cultural item representing a manga collection (a whole series or box set).
 * Stored in the 'manga_collections' table, joined with parent 'items' on 'id'.
 * Individual volumes link to it through {@code Manga.collection}.
 */
@Entity
@Table(name = "manga_collections")
@DiscriminatorValue("MANGA_COLLECTION")
public class MangaCollection extends Item {

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String author;

    @Size(max = 255)
    @Column(length = 255)
    private String artist;

    protected MangaCollection() {
        super();
        setType(ItemType.MANGA_COLLECTION);
    }

    public MangaCollection(String title,
                           String originalTitle,
                           String description,
                           Integer year,
                           String coverUrl,
                           String author,
                           String artist) {
        super(title, originalTitle, description, year, coverUrl);
        this.setType(ItemType.MANGA_COLLECTION);
        this.author = author;
        this.artist = artist;
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

}
