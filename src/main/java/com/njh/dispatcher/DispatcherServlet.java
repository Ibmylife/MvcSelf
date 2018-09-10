package com.njh.dispatcher;

import com.njh.GlobalChain.InvokeHandlerChain;
import com.njh.controllerUtils.controllerUtils;
import com.njh.data.DataConvert;
import com.njh.data.DefaultDataConvert;
import com.njh.resourcerBro.IResourceBro;
import com.njh.resourcerBro.IResourceBroImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务分发
 */
public class DispatcherServlet extends HttpServlet {
    //全局过滤器链
    private InvokeHandlerChain invokeHandlerChain = new InvokeHandlerChain();
    //类解析工具,用于解析类注解信息
    private controllerUtils controllerUtils = new controllerUtils();
    //数据转化
    private DataConvert dataConvert = new DefaultDataConvert();
    //将执行结果根据参数,按照不同类型返回
    private IResourceBro resourceBro = new IResourceBroImpl();
    //保存,类名和类
    private Map<String, Object> classForController = new HashMap<>();
    //保存,类和类方法
    private Map<Object, String> objectForControllerMethod = new HashMap<>();
    //保存,映射地址和类
    private Map<String, Object> urlForControllerObject = new HashMap<>();
    //保存,映射地址和方法
    private Map<String, Method> urlForControllerMethod = new HashMap<>();
    //保证缓存只执行一次
    private boolean flag = true;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (flag) {
                controllerUtils.deal(classForController,
                        objectForControllerMethod,
                        urlForControllerObject,
                        urlForControllerMethod);
                flag = false;
            }
            String uri = req.getRequestURL().toString();
            int oneIndex = uri.lastIndexOf(":");
            uri = uri.substring(oneIndex);
            uri = uri.substring(uri.indexOf("/") + 1);
            //获取存储的类实例
            Object controller = urlForControllerObject.get(uri);
            Method methodName = urlForControllerMethod.get(uri);
            Parameter[] parameters = methodName.getParameters();
            Object[] actualPars = dataConvert.simpleDataConvert(req, methodName);
            Object invoke = methodName.invoke(controller, actualPars);
            resourceBro.responseToBro(resp, methodName, invoke);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
