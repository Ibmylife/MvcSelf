package com.njh.GlobalChain.Interface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AbstractIInvokeHandlerChain {
    void doFilter(HttpServletRequest request, HttpServletResponse response);
}
