package com.lms.library.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.library.models.ModelBook;
import com.lms.library.repositories.BookRepository;

@Service
public class BookService {
	
	@Autowired
    private BookRepository bookRepository;

    public List<ModelBook> findAll() {
        return bookRepository.findAll();
    }

    public ModelBook save(ModelBook book) {
        return bookRepository.save(book);
    }

    public ModelBook findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
