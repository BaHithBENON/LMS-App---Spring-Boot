package com.lms.library.controllers;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.library.models.ModelBook;
import com.lms.library.services.BookService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ImageController {

	@Autowired
	private BookService bookService;
	
	@GetMapping("/images/books/{id}")
	public byte[] getCover(@PathVariable Long id) throws FileNotFoundException {
	    ModelBook book = bookService.findById(id);
	    byte[] imageData = null;
	    if(book.getCover() != null) {
	    	System.out.print(book);
	    	imageData = book.getCover().getContent();
	    }
	    
	    return imageData;
	}


	@GetMapping("/admin/books/cover/{id}")
    public ResponseEntity<ByteArrayResource> getBookCover(@PathVariable Long id) {
		System.out.println(id);
        // Récupérez l'image de couverture de la base de données en utilisant l'ID du livre
        // Ceci est un exemple simplifié, vous devrez adapter cette partie à votre logique de récupération
		ModelBook book = bookService.findById(id);
		System.out.println(book.getCategory());
        byte[] imageData = book.getCover().getContent(); // Récupérez les données de l'image

        ByteArrayResource resource = new ByteArrayResource(imageData);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Assurez-vous d'utiliser le bon type MIME
                .body(resource);
    }
	
	@RequestMapping(value = "/admin/image/{image_id}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long id) throws IOException {
    	
    	ModelBook book = bookService.findById(id);
        byte[] imageContent = Base64.getDecoder().decode(book.getCover().getImageBase64()) ;//get image from DAO based on book id;
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<byte[]>(imageContent, headers, HttpStatus.OK);
    }
    
    @GetMapping("/admin/image/{id}/image")
    public void getImage(@PathVariable Long id, HttpServletResponse response) throws ServletException, IOException {
    	ModelBook book = bookService.findById(id);
        //var imageDecompressed = Base64.getDecoder().decode(book.getCover().getImageBase64());
        //var imageDecompressed2 = decompressBytes(book.getCover().getContent());
        response.setContentType("image/jpeg, image/jpg, image/png");
        //InputStream is = new ByteArrayInputStream(imageDecompressed);
        response.getOutputStream().write(book.getCover().getContent());
        response.getOutputStream().close();
    }
    
    public static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException ioe) {
        } catch (DataFormatException e) {
        }
        return outputStream.toByteArray();
    }
}