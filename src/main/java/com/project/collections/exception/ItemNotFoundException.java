package com.project.collections.exception;

/**
 * Thrown when an item requested by id does not exist.
 */
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(Long id) {
        super("Item not found with id: " + id);
    }

}
