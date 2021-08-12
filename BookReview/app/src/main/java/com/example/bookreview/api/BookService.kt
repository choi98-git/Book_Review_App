package com.example.bookreview.api

import com.example.bookreview.model.BestSellerDto
import com.example.bookreview.model.SearchBookDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.nio.channels.spi.AbstractSelectionKey

interface BookService {
    @GET("/api/search.api?output=json")
    fun  getBooksByName(
        @Query("key") apiKey: String,
        @Query("query") keyword: String
    ):Call<SearchBookDto>

    @GET("/api/bestSeller.api?output=json&categoryId=100")
    fun getBestSellerBooks(
        @Query("key") apiKey: String
    ):Call<BestSellerDto>
}