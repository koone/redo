
package com.lk.redo.commons.util.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/** 
 * @Description: 分页vo
 * @Author: chenao 
 * @Date: 2018/5/16 13:45
 */
@Data
@NoArgsConstructor
public class PageVo<T> {
    /**
     * 每页多少条记录
     */
    private Long pageSize;
    /**
     * 第几页
     */
    private Long pageNumber;
    /**
     * 总共多少页
     */
    private Long totalPage;
    /**
     * 总共多少条记录
     */
    private Long total;
    /**
     * 数据
     */
    private List<T> list;

    /**
     * 提示信息
     */
    private String info;

    @Builder(builderMethodName = "pageBuilder")
    public PageVo (Long pageSize, Long pageNumber, Long total, List<T> list) {
        if(Objects.equals(pageSize,0)){
             pageSize = 10L;
        }
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.total = total;
        this.list = list;
        int pageRemainder = total%pageSize > 0 ? 1 : 0;
        this.totalPage = total/pageSize + pageRemainder;
    }

}
