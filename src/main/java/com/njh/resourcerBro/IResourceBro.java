package com.njh.resourcerBro;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 用于数据向页面转发
 */
public interface IResourceBro {
    void responseToBro(HttpServletResponse response, Method method,Object object) throws IOException;
}
