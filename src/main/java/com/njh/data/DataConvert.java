package com.njh.data;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public interface DataConvert {
    /**
     * ����������
     *
     * @param request
     * @param method
     * @return
     */
    Object[] simpleDataConvert(HttpServletRequest request, Method method) throws Exception;
}
