package org.example.library.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageBookResponse {

    private List<BookResponse> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
