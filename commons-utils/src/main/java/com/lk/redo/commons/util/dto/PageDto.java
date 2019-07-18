package com.lk.redo.commons.util.dto;

import lombok.Data;

@Data
public class PageDto {
    private Long totalNumber;//当前表中总条目数量
    private Long pageNumber = 1L;//当前页的位置
    private Long totalPage;//总页数
    private Long pageSize = 10L;//页面大小
    private Long startIndex;//检索的起始位置
    private Long totalSelect;//检索的总数目
    private Long begin;



    /**
     * 设置分页参数信息
     */
    public void setPage(){
        this.begin = (this.pageNumber -1)*this.pageSize;
    }

    public Long getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Long totalNumber) {
        this.totalNumber = totalNumber;
        this.count();
    }

    public PageDto() {
        super();
    }

    public PageDto(Long pageNumber, Long pageSize) {
        super();
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public PageDto(Long totalNumber, Long pageNumber, Long totalPage, Long pageSize, Long startIndex, Long totalSelect) {
        super();
        this.totalNumber = totalNumber;
        this.pageNumber = pageNumber;
        this.totalPage = totalPage;
        this.pageSize = pageSize;
        this.startIndex = startIndex;
        this.totalSelect = totalSelect;
    }
    public void count(){
        Long totalPageTemp = this.totalNumber/this.pageSize;
        int plus = (this.totalNumber%this.pageSize)==0?0:1;
        totalPageTemp = totalPageTemp+plus;
        if(totalPageTemp<=0){
            totalPageTemp=1L;
        }
        this.totalPage = totalPageTemp;//总页数

        if(this.totalPage<this.pageNumber){
            this.pageNumber = this.totalPage;
        }
        if(this.pageNumber<1){
            this.pageNumber=1L;
        }
        this.startIndex = (this.pageNumber-1)*this.pageSize;//起始位置等于之前所有页面输乘以页面大小
        this.totalSelect = this.pageSize;//检索数量等于页面大小
    }
}
