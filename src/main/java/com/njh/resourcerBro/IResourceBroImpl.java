package com.njh.resourcerBro;

import com.njh.annation.RequestMapping;
import com.njh.annation.ResponseBody;
import com.njh.controllerUtils.controllerUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

public class IResourceBroImpl implements IResourceBro {
    private String reutrnType;
    {
        Properties pro = new Properties();
        try {
            pro.load(controllerUtils.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        reutrnType = pro.getProperty("reutrnType");
    }

    @Override
    public void responseToBro(HttpServletResponse response, Method method,Object object) throws IOException {
        ResponseBody responseBody = method.getAnnotation(ResponseBody.class);
        if(responseBody!=null){
            response.getWriter().write(object.toString());
        }else {

        }

    }
}
