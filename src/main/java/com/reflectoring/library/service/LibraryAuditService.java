package com.reflectoring.library.service;

import com.reflectoring.library.mapper.AuditMapper;
import com.reflectoring.library.mapper.LibraryMapper;
import com.reflectoring.library.model.Response;
import com.reflectoring.library.model.Status;
import com.reflectoring.library.model.mapstruct.AuditDto;
import com.reflectoring.library.model.mapstruct.BookDto;
import com.reflectoring.library.model.persistence.AuditLog;
import com.reflectoring.library.model.persistence.Book;
import com.reflectoring.library.repository.AuditRepository;
import com.reflectoring.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LibraryAuditService {

    private static final Logger log = LoggerFactory.getLogger(LibraryAuditService.class);

    private final BookRepository bookRepository;

    private final LibraryMapper libraryMapper;

    private final AuditMapper auditMapper;

    private final AuditRepository auditRepository;

    public LibraryAuditService(BookRepository bookRepository, LibraryMapper libraryMapper,
                               AuditMapper auditMapper, AuditRepository auditRepository) {
        this.bookRepository = bookRepository;
        this.libraryMapper = libraryMapper;
        this.auditMapper = auditMapper;
        this.auditRepository = auditRepository;
    }

    public List<BookDto> getAllBooks() {
        List<Book> allBooks = bookRepository.findAll();
        log.info("Get All Books : {}", allBooks);
        List<BookDto> respBooks = libraryMapper.bookToBookDto(allBooks);
        AuditDto audit = auditMapper.populateAuditLogForGet(respBooks);
        if (Objects.nonNull(audit)) {
            AuditLog savedObj = auditRepository.save(libraryMapper.auditDtoToAuditLog(audit));
            log.info("Saved into audit successfully: {}", savedObj);
        }
        return respBooks;
    }

    public Response createBook(BookDto bookDto) {
        log.info("Book DTO from POST request : {}", bookDto);
        Book book = libraryMapper.bookDtoToBook(bookDto);
        log.info("Mapping from BookDTO to Book entity is done. Book : {}", book);
        bookRepository.save(book);
        Response response = new Response(Status.SUCCESS.toString(), "Book created successfully");
        AuditDto audit = auditMapper.populateAuditLogForPostAndPut(bookDto, response, HttpMethod.POST);
        if (Objects.nonNull(audit)) {
            AuditLog savedObj = auditRepository.save(libraryMapper.auditDtoToAuditLog(audit));
            log.info("Saved into audit successfully: {}", savedObj);
        }
        return response;
    }

    public Response updateBook(Long id, BookDto bookdto) {
        Book book = libraryMapper.bookDtoToBook(bookdto);
        log.info("To be updated Book data: {}", book);
        Book fetchBook = bookRepository.getOne(id);
        log.info("Before update first fetch Book details from DB. Book: {}", fetchBook);
        if (Objects.nonNull(book.getAuthors()) && !book.getAuthors().isEmpty()) {
            fetchBook.getAuthors().clear();
            fetchBook.setAuthors(book.getAuthors());
        }
        if (Objects.nonNull(book.isCopyrightIssued())) {
            fetchBook.setCopyrightIssued(book.isCopyrightIssued());
        }
        if (Objects.nonNull(book.getPublisher())) {
            fetchBook.setPublisher(book.getPublisher());
        }
        if (Objects.nonNull(book.getPublicationYear())) {
            fetchBook.setPublicationYear(book.getPublicationYear());
        }
        log.info("Updated book object is {}", fetchBook);
        bookRepository.save(fetchBook);
        Response response = new Response(Status.SUCCESS.toString(), "Book updated successfully");
        AuditDto audit = auditMapper.populateAuditLogForPostAndPut(bookdto, response, HttpMethod.PUT);
        if (Objects.nonNull(audit)) {
            AuditLog savedObj = auditRepository.save(libraryMapper.auditDtoToAuditLog(audit));
            log.info("Saved into audit successfully: {}", savedObj);
        }
        return response;
    }

    public Response deleteBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        Response response = null;
        if (book.isPresent()) {
            bookRepository.delete(book.get());
            response = new Response(Status.SUCCESS.toString(), "Book deleted successfully");
        } else {
            response = new Response(Status.ERROR.toString(), "Could not delete book for id : " + id);
        }
        AuditDto audit = auditMapper.populateAuditLogForDelete(id, response);
        if (Objects.nonNull(audit)) {
            AuditLog savedObj = auditRepository.save(libraryMapper.auditDtoToAuditLog(audit));
            log.info("Saved into audit successfully : {}", savedObj);
        }
        return response;
    }
}
