package com.library.bookService.service;

import com.library.bookService.exception.BookNotFoundException;
import com.library.bookService.model.Book;
import com.library.bookService.repository.BookRepo;
import com.library.bookService.serviceImplementation.BookServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class BookServiceImplementationTest {

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
       autoCloseable= MockitoAnnotations.openMocks(this);
        book=new Book(1,"Kama","sai",1);
    }

    @Test
    void testAddBook() {
        Mockito.when(bookRepo.save(any(Book.class))).thenReturn(book);

        Book savedBook = bookService.addBook(book);

        assertNotNull(savedBook);
        assertEquals(book.getId(), savedBook.getId());
        assertEquals(book.getBName(), savedBook.getBName());

        verify(bookRepo, times(1)).save(book);

    }

    @Test
    void testGetAllBooks(){
        Book book1=new Book(1,"Book one","Author One",356);
        Book book2=new Book(1,"Book two","Author Two",358);
        List<Book> list = Arrays.asList(book1, book2);

        when(bookRepo.findAll()).thenReturn(list);
        List<Book> result = bookService.getAllBooks();

        assertNotNull(result);
        assertEquals(2,result.size());
        assertEquals(book1.getId(),result.get(0).getId());
        assertEquals(book2.getId(),result.get(1).getId());

        verify(bookRepo,times(1)).findAll();
    }

    @Test
    void testGetAllBooks_ReturnsEmptyList(){

        when(bookRepo.findAll()).thenReturn(Collections.emptyList());
        List<Book> result = bookService.getAllBooks();
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bookRepo,times(1)).findAll();
    }

    @Test
    void testDeleteBook(){
        Book book1=new Book(1,"Bname","Aname",3);
        when(bookRepo.findById(book1.getId())).thenReturn(Optional.of(book1));
        String deleteBook = bookService.deleteBook(book1.getId());
        assertEquals("Book with id 1 has been deleted successfully", deleteBook);

        verify(bookRepo,times(1)).findById(book1.getId());
        verify(bookRepo,times(1)).delete(book1);
    }

    @Test
   void testDeleteBook_NotFound(){

        Integer bookId=1;

        when(bookRepo.findById(bookId)).thenReturn(Optional.empty());

        BookNotFoundException bookNotFoundException = assertThrows(BookNotFoundException.class, () -> {
            bookService.deleteBook(bookId);
        });

        assertEquals("Book with id 1 not found",bookNotFoundException.getMessage());
        verify(bookRepo,times(1)).findById(bookId);
        verify(bookRepo,never()).delete(any(Book.class));
    }

    @Test
    void testUpdateBook(){
        Book book1=new Book(1,"Bname","Aname",3);
        when(bookRepo.existsById(book1.getId())).thenReturn(true);

        book1.setQuantity(5);

        when(bookRepo.save(book1)).thenReturn(book1);
        Book updateBook = bookService.updateBook(book1);

        assertNotNull(updateBook);
        assertEquals(book1.getId(),updateBook.getId());
        assertEquals(5,updateBook.getQuantity());
        assertEquals("Bname",updateBook.getBName());
        assertEquals("Aname",updateBook.getAuthor());

        verify(bookRepo,times(1)).existsById(book1.getId());
        verify(bookRepo,times(1)).save(book1);
    }

    @Test
 void testUpdateBook_Failure(){
        Book book1=new Book(1,"Bname","Aname",3);
        when(bookRepo.existsById(book1.getId())).thenReturn(false);

        assertThrows(BookNotFoundException.class,()->{
            bookService.updateBook(book1);
        });

        verify(bookRepo,times(1)).existsById(book1.getId());
        verify(bookRepo,never()).save(any(Book.class));
 }

    @AfterEach
    void tearDown() throws Exception{
        autoCloseable.close();
    }
}
