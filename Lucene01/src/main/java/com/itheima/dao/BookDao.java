package com.itheima.dao;

import com.itheima.entity.Book;

import java.util.List;

public interface BookDao {

    List<Book> findAllBook();
}
