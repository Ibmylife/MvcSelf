package com.njh.data;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public interface DataConvert {
    /**
     * 解析简单数据
     *
     * @param request
     * @param method
     * @return
     */
    Object[] simpleDataConvert(HttpServletRequest request, Method method) throws Exception;
}
