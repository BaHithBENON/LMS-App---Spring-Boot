package com.lms.library.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.library.models.ModelBook;
import com.lms.library.services.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	@Autowired
    private BookService bookService;

    @GetMapping
    public List<ModelBook> getAllBooks() {
        return bookService.findAll();
    }

    @PostMapping
    public ModelBook createBook(@RequestBody ModelBook book) {
        return bookService.save(book);
    }

    @GetMapping("/{id}")
    public ModelBook getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PutMapping("/{id}")
    public ModelBook updateBook(@PathVariable Long id, @RequestBody ModelBook bookDetails) {
        // Implémentez la logique de mise à jour  ici
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
