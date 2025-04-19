package com.swc4253groupd.libraryapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.swc4253groupd.libraryapp.model.BookBorrow;

public interface BookBorrowRepository extends CrudRepository<BookBorrow, Integer> {
    List<BookBorrow> findByDatereturnBeforeAndIsreturnFalse(LocalDateTime now);
    List<BookBorrow> findByUserUserid(Integer userId);
}
