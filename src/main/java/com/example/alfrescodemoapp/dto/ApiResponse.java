package com.example.alfrescodemoapp.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<D, E> {


    private int status;

    private D data;

    private E error;

    public ApiResponse(int status, D data) {
        this.status = status;
        this.data = data;
    }




    public ApiResponse(int status) {
        this.status = status;
    }



}
