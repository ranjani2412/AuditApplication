package com.reflectoring.library.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reflectoring.library.model.Response;
import com.reflectoring.library.model.mapstruct.AuditDto;
import com.reflectoring.library.model.mapstruct.BookDto;
import com.reflectoring.library.model.persistence.AuditLog;
import com.reflectoring.library.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuditMapper {

    private static final Logger LOG = LoggerFactory.getLogger(AuditMapper.class);

    public AuditDto populateAuditLogForGet(List<BookDto> books) {
        AuditDto log = null;
        try {
            log = new AuditDto();
            log.setMethodType(HttpMethod.GET);
            log.setRequest(null);
            log.setResponse(new ObjectMapper().writeValueAsString(books));
            log.setSuccess(Constants.SUCCESS);
            log.setTimestamp(LocalDateTime.now());
        } catch (JsonProcessingException ex) {
            LOG.error("Error processing Object to json", ex);
        }
        return log;

    }

    public AuditDto populateAuditLogForPostAndPut(BookDto book, Response response, HttpMethod method) {
        AuditDto log = null;
        try {
            log = new AuditDto();
            log.setMethodType(method);
            log.setRequest(new ObjectMapper().writeValueAsString(book));
            log.setResponse(new ObjectMapper().writeValueAsString(response));
            log.setSuccess(Constants.SUCCESS);
            log.setTimestamp(LocalDateTime.now());
        } catch (JsonProcessingException ex) {
            LOG.error("Error processing Object to json", ex);
        }
        return log;

    }

    public AuditDto populateAuditLogForDelete(Long id, Response response) {
        AuditDto log = null;
        try {
            log = new AuditDto();
            log.setMethodType(HttpMethod.DELETE);
            log.setRequest(String.valueOf(id));
            log.setResponse(new ObjectMapper().writeValueAsString(response));
            log.setSuccess(Constants.SUCCESS);
            log.setTimestamp(LocalDateTime.now());
        } catch (JsonProcessingException ex) {
            LOG.error("Error processing Object to json", ex);
        }
        return log;

    }


}
