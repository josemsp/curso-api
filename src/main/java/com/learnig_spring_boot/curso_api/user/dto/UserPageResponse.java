package com.learnig_spring_boot.curso_api.user.dto;

import java.util.List;

/**
 * Represents a paginated response containing users and pagination metadata.
 * */
public class UserPageResponse {
    private final List<UserResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;

    public UserPageResponse(
            List<UserResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ){
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public List<UserResponse> getContent(){
        return this.content;
    }

    public int getPage(){
        return this.page;
    }

    public int getSize(){
        return this.size;
    }

    public long getTotalElements(){
        return this.totalElements;
    }

    public int getTotalPages(){
        return this.totalPages;
    }

    public boolean getFirst(){
        return this.first;
    }

    public boolean getLast(){
        return this.last;
    }
}