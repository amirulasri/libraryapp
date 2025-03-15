package com.swc4253groupd.libraryapp.repository;

import org.springframework.data.repository.CrudRepository;

import com.swc4253groupd.libraryapp.model.Book;

public interface BookRepository extends CrudRepository<Book, Integer> {
}
