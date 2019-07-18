package com.lk.redo.commons.util.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
public class PageQueryDto {

    private Long pageNumber = 1L;//当前页的位置

    private Long pageSize = 10L;//页面大小

    public Long getStartIndex(){
        return (pageNumber-1)*pageSize;
    }

    public Long getTotalSelect(){
        return pageSize;
    }
}
