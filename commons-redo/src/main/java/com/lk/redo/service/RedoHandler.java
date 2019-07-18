package com.lk.redo.service;


import com.lk.redo.model.SysRedo;

/**
 * 重做接口
 */
public interface RedoHandler {
    /**
     * 重做，各业务模块根据失败发生时记录的args信息，进行失败重做
     * @param redoItem
     */
    void redo(SysRedo redoItem);
}