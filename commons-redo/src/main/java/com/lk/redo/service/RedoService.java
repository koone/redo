package com.lk.redo.service;

import com.lk.redo.annotation.Redo;
import com.lk.redo.commons.util.enums.VoCodeEnum;
import com.lk.redo.commons.util.exception.BusinessException;
import com.lk.redo.commons.util.utils.JsonUtil;
import com.lk.redo.dao.SysRedoMapper;
import com.lk.redo.model.RedoException;
import com.lk.redo.model.SysRedo;
import com.lk.redo.util.RedoCheckUtils;
import com.lk.redo.util.RedoConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * redo service
 */
@Slf4j
@Service
public class RedoService extends ApplicationObjectSupport {

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    // mork model，用来标记是否redo模式，告诉切面过滤处理
    private static final ThreadLocal<Boolean> WORK_MODEL = new ThreadLocal<Boolean>();

    @Resource
    private SysRedoMapper mapper;


    /**
     * 异步记录redo日志
     * @param redo
     * @param t
     * @param invokeClass
     * @param invokeMethod
     * @param args
     */
    public void addRedoItem(Redo redo, Throwable t, Class<?> invokeClass, MethodSignature invokeMethod, Object[] args) {
        final SysRedo redoItem = new SysRedo();
        redoItem.setAutoRedoAble(redo.autoRedoAble());
        redoItem.setAutoRedoLimit(redo.autoRedoLimit());
        redoItem.setRedoHandler(redo.handlerName());
        redoItem.setBizType(redo.type());
        if (invokeClass != null) {
            redoItem.setBizInvokeClazz(invokeClass.getName());
            redoItem.setBizInvokeMethod(invokeMethod.getName());
            redoItem.setBizInvokeMethodArgtype(JsonUtil.toJson(invokeMethod.getParameterTypes()));
        }
        redoItem.setBizInvokeArgs(serialize(args));
        redoItem.setCreateTime(new Date());
        redoItem.setFailMessage(getFailMessage(t));
        // 异步保存
        taskExecutor.submit((Runnable) () -> {
            try {
               mapper.insertSelective(redoItem);
            } catch (Exception e) {
                log.error("save redo item {} error", redoItem,e);
            }
        });
    }


    /**
     * 执行自动重做的操作
     */
    public void autoRedo() {
        // 查询可以自动执行的任务: 可自动自行 & 状态为 待处理
        List<SysRedo> redoList = mapper.findAutoRedoItem();
        // 处理任务
        redoList.stream().forEach( item -> {
            redo(item, "auto");
        });
    }

    /**
     * 执行手动重做
     */
    public void redoById(Long id, String operator) {
        SysRedo redoItem = mapper.selectByPrimaryKey(id);
        if (redoItem != null) {
            boolean result = redo(redoItem, operator);
            if (!result){
                throw new BusinessException(VoCodeEnum.FAIL);
            }
        }
    }


    /**
     * 执行redo
     * @param redoItem
     */
    public boolean redo(SysRedo redoItem, String executor) {
        SysRedo modify = new SysRedo();
        try {
            modify.setId(redoItem.getId());
            modify.setUpdateTime(new Date());
            modify.setUpdator(executor);
            if (RedoConstants.STATUS_NEW.equals(redoItem.getStatus())
                    || RedoConstants.STATUS_HANDLE_FAILED.equals(redoItem.getStatus())) {
                RedoHandler redoHandler = getUredoHandler(redoItem.getRedoHandler());
                RedoCheckUtils.checkNotNull(redoHandler);
                WORK_MODEL.set(true);
                redoHandler.redo(redoItem);
            }
            // 执行成功，设置状态
            modify.setStatus(RedoConstants.STATUS_HANDLE_SUCCESS);
            mapper.updateByPrimaryKeySelective(modify);
            return true;
        } catch (Exception e) {
            log.error("execute redo item {} error", redoItem, e);
            // 执行失败，重试次数加一，如果重试次数大于等于重试限制，状态设置为失败
            modify.setAutoRedoCount(redoItem.getAutoRedoCount() + 1);
            if (modify.getAutoRedoCount() >= redoItem.getAutoRedoLimit()) {
                modify.setStatus(RedoConstants.STATUS_HANDLE_FAILED);
            }
            mapper.updateByPrimaryKeySelective(modify);
            return false;
        } finally {
            WORK_MODEL.remove();
        }
    }

    /**
     * 取消重做
     * @param id
     * @param operator
     */
    public void cancelById(Long id, String operator) {
        SysRedo redoItem = mapper.selectByPrimaryKey(id);
        if (redoItem != null){
            if (RedoConstants.STATUS_NEW.equals(redoItem.getStatus())
                    || RedoConstants.STATUS_HANDLE_FAILED.equals(redoItem.getStatus())) {
                redoItem.setStatus(RedoConstants.STATUS_NOT_NEED_HANDLE);
                redoItem.setUpdator(operator);
                redoItem.setUpdateTime(new Date());
                mapper.updateByPrimaryKeySelective(redoItem);
            }
        }


    }

    /**
     * 还有未处理的任务报警(近10分钟内)
     */
    public void warning() {
        // 获取执行失败 & 需要手动处理的任务
        int needRedoCount = mapper.countNeedRedoItem();
        if (needRedoCount > 0) {
            log.error("【保险】【重试】有需要手动处理的任务【"+ needRedoCount + "】条，请及时查看处理");
        }
    }


    /**
     * 将bizInvokeArgs数组中的每个arg按照格式排列: <br>
     * arg0ClazzName, arg0ValueJsonStr, arg1ClazzName, arg1ValueJsonStr, arg2ClazzName, arg2ValueJsonStr<br>
     * 重排之后的队列序列化成json array string，存入到持久化层
     *
     * @return json array string
     */
    public String serialize(Object[] args) {
        if (args == null) {
            return "";
        }
        List<Object> argNameAndValueList = new ArrayList<Object>();
        for (Object arg : args) {
            if (arg != null) {
                argNameAndValueList.add(arg.getClass().getName());
                argNameAndValueList.add(JsonUtil.toJson(arg));
            }else{
                argNameAndValueList.add(null);
                argNameAndValueList.add(null);
            }

        }
        return JsonUtil.toJson(argNameAndValueList);
    }


    private String getFailMessage(Throwable t) {
        if (t == null) {
            return "";
        }
        String message = t.getMessage();
        String cause = t.toString();
        String failMessage = "message: " + message + "; cause: " + cause;
        return failMessage.substring(0, failMessage.length() < 2000 ? failMessage.length() : 2000);
    }

    private RedoHandler getUredoHandler(String handlerName) {
        RedoCheckUtils.checkNotBlank(handlerName);
        byte[] charArray = handlerName.getBytes();
        if (charArray[0] >= 'A' && charArray[0] < 'Z') {
            charArray[0] = (byte) ((char) charArray[0] + 'a' - 'A');
        }
        String handlerBeanName = new String(charArray);
        ApplicationContext applicationContext = getApplicationContext();
        try {
            Object bean = applicationContext.getBean(handlerBeanName);
            RedoCheckUtils.check(bean instanceof RedoHandler);
            return (RedoHandler) bean;
        } catch (NoSuchBeanDefinitionException e) {
            throw new RedoException("no redoHandler instance named " + handlerBeanName + " is defined", e);
        }
    }

    public static boolean isRedoWorkModel() {
        return WORK_MODEL.get() == null ? false : WORK_MODEL.get();
    }


}