package com.project.libraryproducer.domain;

public record Book(
        Integer bookId,
        String bookName,
        String bookAuthor
) {
}
