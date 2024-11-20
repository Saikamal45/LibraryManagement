package com.library.bookService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bookService.model.Book;
import com.library.bookService.service.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;
    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book(1, "Book Title", "Author Name", 10);

    }


    @Test
    void testAddBook() throws Exception{
         when(bookService.addBook(any(Book.class))).thenReturn(book);

         mockMvc.perform(post("/books/addBook")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(book)))
                 .andExpect(status().isCreated())
                 .andExpect(jsonPath("$.id").value(1))
                 .andExpect(jsonPath("$.bname").value("Book Title"));
    }

    @Test
    void testGetAllBooks() throws Exception{
        List<Book> books = Arrays.asList(book, new Book(2, "Another Book", "Another Author", 5));
        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/books/getAllBooks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].bname").value("Another Book"));
    }

    @Test
    void testGetBookById() throws Exception {
        when(bookService.getBookById(1)).thenReturn(book);

        mockMvc.perform(get("/books/getBookById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.bname").value("Book Title"));
    }

    @Test
    void testDeleteBook() throws Exception{
        when(bookService.deleteBook(1)).thenReturn("Book with id 1 has been deleted successfully");

        mockMvc.perform(delete("/books/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book with id 1 has been deleted successfully"));
    }

    @Test
    void testUpdateBook() throws Exception{
        Book updateBook=new Book(1,"UpdatedTitle","AuthorName",5);
        when(bookService.updateBook(any(Book.class))).thenReturn(updateBook);

        mockMvc.perform(put("/books/updateBook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bname").value("UpdatedTitle"));
    }
}