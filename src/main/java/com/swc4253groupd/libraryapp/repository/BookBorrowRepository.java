package com.swc4253groupd.libraryapp.repository;

import org.springframework.data.repository.CrudRepository;

import com.swc4253groupd.libraryapp.model.BookBorrow;

public interface BookBorrowRepository extends CrudRepository<BookBorrow, Integer> {
}
