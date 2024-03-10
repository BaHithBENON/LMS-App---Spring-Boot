package com.lms.library.services;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.library.enums.UserRole;
import com.lms.library.models.ModelBook;
import com.lms.library.models.ModelProfile;
import com.lms.library.models.ModelUser;
import com.lms.library.repositories.BookRepository;

import jakarta.annotation.PostConstruct;

@Service
public class BookService {
	
	@Autowired
    private BookRepository bookRepository;
	
	@PostConstruct
   	public void postConstruct() {
	   	for(ModelBook book : ModelBook.getStaticBookList()) {
	   		save(book);
	   	}
   	}
	
	public byte[] getImageContentById(Long id) throws FileNotFoundException {
	    Optional<ModelBook> livreOptional = bookRepository.findById(id);

	    if (livreOptional.isPresent()) {
	    	ModelBook book = livreOptional.get();
	        return book.getCover().getContent(); 
	    } else {
	        // Gérer le cas où aucun livre avec l'ID spécifié n'est trouvé
	        throw new FileNotFoundException("Livre non trouvé avec l'ID : " + id);
	    }
	}

    public List<ModelBook> findAll() {
        return bookRepository.findAll();
    }

    public ModelBook save(ModelBook book) {
        return bookRepository.save(book);
    }

    public ModelBook findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }
    
    public ModelBook findByCode(String code) {
        return bookRepository.findByCode(code).orElse(null);
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
