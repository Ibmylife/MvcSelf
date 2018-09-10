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
 * ����ַ�
 */
public class DispatcherServlet extends HttpServlet {
    //ȫ�ֹ�������
    private InvokeHandlerChain invokeHandlerChain = new InvokeHandlerChain();
    //���������,���ڽ�����ע����Ϣ
    private controllerUtils controllerUtils = new controllerUtils();
    //����ת��
    private DataConvert dataConvert = new DefaultDataConvert();
    //��ִ�н�����ݲ���,���ղ�ͬ���ͷ���
    private IResourceBro resourceBro = new IResourceBroImpl();
    //����,��������
    private Map<String, Object> classForController = new HashMap<>();
    //����,����෽��
    private Map<Object, String> objectForControllerMethod = new HashMap<>();
    //����,ӳ���ַ����
    private Map<String, Object> urlForControllerObject = new HashMap<>();
    //����,ӳ���ַ�ͷ���
    private Map<String, Method> urlForControllerMethod = new HashMap<>();
    //��֤����ִֻ��һ��
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
            //��ȡ�洢����ʵ��
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
