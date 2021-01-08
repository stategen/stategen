package org.stategen.framework.spring.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.stategen.framework.lite.BaseResponse;
import org.stategen.framework.lite.IResponseStatus;
import org.stategen.framework.response.ResponseStatusTypeHandler;
import org.stategen.framework.response.ResponseUtil;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/***
 * https://blog.csdn.net/u011684553/article/details/108789968
 * 
 * @author niaoge
 * @version $Id: SentinelBlockHandler.java, v 0.1 2021年1月4日 下午2:28:58 XiaZhengsheng Exp $
 */
@Setter
@Slf4j
public class SentinelBlockHandler extends ResponseStatusTypeHandler implements BlockExceptionHandler, InitializingBean {
    
    
    private String defaultBlockMsg = "请过会再试";
    
    private String msgFlowException = "该阶段不支持该操作(限流)，请稍后再试";
    
    private String msgDegradeException = "该阶段不支持该操作(降级)，请稍后再试";
    
    private String msgParamFlowException = "该阶段不支持该操作(热点参数)，请稍后再试";
    
    private String msgSystemBlockException = "系统规则（负载/...不满足要求）";
    
    private String msgAuthorityException = "授权规则不通过";
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse httpServletResponse, BlockException ex) throws Exception {
        String msg = ResponseUtil.BLOCK_MAP.get(ex.getClass());
        if (org.stategen.framework.util.StringUtil.isEmpty(msg)) {
            msg = defaultBlockMsg;
        }
        
        log.error(new StringBuilder("SentinelBlockHandler 截获一个异常:").append(ex.getMessage()).append(" \n")
                .toString(), ex);
        
        BaseResponse<?> errorResponse = ResponseUtil.buildResponse(null, getResponseStatus());
        errorResponse.setExeptionClass(ex.getClass().getSimpleName());
        errorResponse.setMessage(msg);
        httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        ResponseUtil.writhResponse(true, errorResponse);
        
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        ResponseUtil.BLOCK_MAP.putIfAbsent(FlowException.class.getSimpleName(), msgFlowException);
        ResponseUtil.BLOCK_MAP.putIfAbsent(DegradeException.class.getSimpleName(), msgDegradeException);
        ResponseUtil.BLOCK_MAP.putIfAbsent(ParamFlowException.class.getSimpleName(), msgParamFlowException);
        ResponseUtil.BLOCK_MAP.putIfAbsent(SystemBlockException.class.getSimpleName(), msgSystemBlockException);
        ResponseUtil.BLOCK_MAP.putIfAbsent(AuthorityException.class.getSimpleName(), msgAuthorityException);
        super.afterPropertiesSet();
    }
    
    public void setBlockResponseStatus(IResponseStatus responseStatus) {
        super.setResponseStatus(responseStatus);
    }
}
