package com.swc4253groupd.libraryapp.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.swc4253groupd.libraryapp.model.Book;

public interface BookRepository extends CrudRepository<Book, Integer> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByCategoryContainingIgnoreCase(String category);

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
        String title, String author, String category
    );
}
