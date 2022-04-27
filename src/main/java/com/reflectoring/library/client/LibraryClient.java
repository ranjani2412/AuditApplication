package com.reflectoring.library.client;

import com.reflectoring.library.model.mapstruct.BookDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LibraryClient {

    @GET("/library/managed/books")
    public Call<BookDto> getAllBooks(@Query("type") String type);




}
