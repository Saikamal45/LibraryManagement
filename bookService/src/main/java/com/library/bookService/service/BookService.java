package com.library.bookService.service;

import com.library.bookService.model.Book;


import java.util.List;

public interface BookService {

    public Book addBook(Book book);

    public List<Book> getAllBooks();

    public Book getBookById(Integer id);

    public String deleteBook(Integer id);

    public Book updateBook(Book book);


}
