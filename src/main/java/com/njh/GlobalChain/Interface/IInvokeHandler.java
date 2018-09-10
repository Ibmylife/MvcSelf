package com.njh.GlobalChain.Interface;

import com.njh.GlobalChain.InvokeHandlerChain;
import com.njh.GlobalChain.config.IInvokeHandlerConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Ö´ÐÐÆ÷½Ó¿Ú
 */
public interface IInvokeHandler {
    void init(IInvokeHandlerConfig config);

    void invoke(HttpServletRequest request, HttpServletResponse response, InvokeHandlerChain chain);

    void destory();
}
