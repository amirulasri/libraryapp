package com.swc4253groupd.libraryapp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.swc4253groupd.libraryapp.model.BookBorrow;

public interface BookBorrowRepository extends CrudRepository<BookBorrow, Integer> {
    List<BookBorrow> findByDatereturnBeforeAndIsreturnFalse(LocalDate now);
    List<BookBorrow> findByUserUserid(Integer userId);
}
