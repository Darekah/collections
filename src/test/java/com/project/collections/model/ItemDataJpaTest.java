package com.project.collections.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemDataJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void persistAndReadBack_validItem_returnsStoredValuesWithTimestamps() {
        Item item = new Item();
        item.setTitle("The Hobbit");
        item.setType(ItemType.BOOK);
        item.setYear(1937);

        Item found = entityManager.persistFlushFind(item);

        assertNotNull(found);
        assertNotNull(found.getId());
        assertEquals("The Hobbit", found.getTitle());
        assertEquals(ItemType.BOOK, found.getType());
        assertEquals(Integer.valueOf(1937), found.getYear());
        assertNotNull(found.getCreatedAt());
        assertNotNull(found.getUpdatedAt());
    }

}
