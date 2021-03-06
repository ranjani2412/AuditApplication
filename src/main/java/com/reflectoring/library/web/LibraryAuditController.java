package com.reflectoring.library.web;

import com.reflectoring.library.model.Response;
import com.reflectoring.library.model.Status;
import com.reflectoring.library.model.mapstruct.BookDto;
import com.reflectoring.library.service.LibraryAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("library/managed/books")
public class LibraryAuditController {

    private static final Logger log = LoggerFactory.getLogger(LibraryAuditController.class);

    private final LibraryAuditService libraryAuditService;

    public LibraryAuditController(LibraryAuditService libraryAuditService) {
        this.libraryAuditService = libraryAuditService;
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getBooks() {
        return ResponseEntity.ok().body(libraryAuditService.getAllBooks());
    }

    @PostMapping
    public ResponseEntity<Response> createBook(@RequestBody BookDto book) {
        return ResponseEntity.ok().body(libraryAuditService.createBook(book));
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Response> updateBook(@PathVariable Long id, @RequestBody BookDto book) {
        return ResponseEntity.ok().body(libraryAuditService.updateBook(id, book));
    }

    @DeleteMapping(value = "{id}", produces = "application/json")
    public ResponseEntity<Response> deleteBook(@PathVariable Long id) {
        Response response = libraryAuditService.deleteBook(id);
        log.info("Response received : {}", response);
        log.info("Status from response : {}", Status.fetchCode(response.getResponseCode()));
        return ResponseEntity.status(Status.fetchCode(response.getResponseCode())).body(response);
    }
}
