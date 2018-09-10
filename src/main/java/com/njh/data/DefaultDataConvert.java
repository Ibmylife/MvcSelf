package com.njh.data;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ����ת��
 */
public class DefaultDataConvert implements com.njh.data.DataConvert {
    @Override
    public Object[] simpleDataConvert(HttpServletRequest request, Method method) throws Exception {
        Object[] objects = new Object[method.getParameterCount()];
        Map parameterMap = request.getParameterMap();
        //����ȷ�������±�
        AtomicInteger i = new AtomicInteger();
        Class<?>[] parameterTypes = method.getParameterTypes();
        //�����������
        parameterMap.forEach((k, v) -> {
            //������������
            for (Parameter parameter : method.getParameters()) {
                //��ȡ�������뷽������ͬ�Ĳ���
                if (parameter.getName().equalsIgnoreCase(String.valueOf(k))) {
                    Object cast = null;
                    try {
                        //�������������ݲ���Ϊһ��������������ʱ(����String)
                        if (((Object[]) v).length == 1) {
                            //����ǿת
                            if (parameter.getType().isPrimitive()) {
                                cast = convertPrimitive(parameter.getType().getName(), ((Object[]) v)[0] + "");
                                objects[i.get()] = cast;
                                i.addAndGet(1);
                            } else if (parameter.getType().getName().equalsIgnoreCase("java.lang.String")) {
                                cast = Class.forName(parameter.getType().getName()).newInstance().getClass().cast(((Object[]) v)[0] + "");
                                objects[i.get()] = cast;
                                i.addAndGet(1);
                            }
                            //����������������(�����Զ���,��int�Ȱ�װ����)
                            else {

                            }
                        }
                        //�����Ǵ������ݲ���Ϊ����,��Ϊ������������
                        else if (v.getClass().isArray()) {
                            String paramName = "";
                            String arrayContentType = "";
                            //�����������Ͳ�����ΪΪ[*,��int[]����,��Ϊ[I
                            try {
                                paramName = parameter.getType().getName();
                                arrayContentType = paramName.substring(2, paramName.length() - 1);
                            } catch (Exception e) {
                                paramName = parameter.getType().getSimpleName();
                                arrayContentType = paramName.substring(0, paramName.length() - 2);
                            }
                            if (isPrimitive(arrayContentType)) {
                                Object newArray =GeneratePrimitiveArray(arrayContentType, ((Object[]) v).length);
//                                Object newArray = Array.newInstance(Class.forName(GetPrimitiveWrapperClassName(arrayContentType)), ((Object[]) v).length);
                                for (int j = 0; j < ((Object[]) v).length; j++) {
                                    Object paramTem = convertPrimitive(arrayContentType, ((Object[]) v)[j] + "");
                                    Array.set(newArray, j, paramTem);
                                }
                                objects[i.get()] = newArray;
                                i.addAndGet(1);
                            } else if (arrayContentType.equalsIgnoreCase("java.lang.String")) {
                                Object newArray = Array.newInstance(Class.forName(arrayContentType), ((Object[]) v).length);
                                for (int j = 0; j < ((Object[]) v).length; j++) {
                                    Object paramTem = Class.forName(arrayContentType).newInstance().getClass().cast(((Object[]) v)[j] + "");
                                    Array.set(newArray, j, paramTem);
                                }
                                objects[i.get()] = newArray;
                                i.addAndGet(1);
                            } else {

                            }

                        } else {

                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return objects;
    }

    /**
     * char���Ͳ���
     *
     * @param type
     * @param val
     * @return
     */
    private Object convertPrimitive(String type, String val) {
        Object obj = null;
        switch (type) {
            case "byte":
                obj = Byte.parseByte(val);
                break;
            case "char":
                obj = null;
                break;
            case "short":
                obj = Short.parseShort(val);
                break;
            case "long":
                obj = Long.parseLong(val);
                break;
            case "boolean":
                obj = Boolean.parseBoolean(val);
                break;
            case "int":
                obj = Integer.parseInt(val);
                break;
            case "double":
                obj = Double.parseDouble(val);
                break;
            case "float":
                obj = Float.parseFloat(val);
                break;
        }
        return obj;
    }
    private boolean isPrimitive(String val) {
        boolean flag = false;
        switch (val) {
            case "byte":
                flag = true;
                break;
            case "char":
                flag = true;
                break;
            case "short":
                flag = true;
                break;
            case "long":
                flag = true;
                break;
            case "boolean":
                flag = true;
                break;
            case "int":
                flag = true;
                break;
            case "double":
                flag = true;
                break;
            case "float":
                flag = true;
                break;
        }
        return flag;
    }
    private String GetPrimitiveWrapperClassName(String type) {
        String className = null;
        switch (type) {
            case "byte":
                className="java.lang.Btye";
                break;
            case "char":
                className = "java.lang.Character";
                break;
            case "short":
                className="java.lang.Short";
                break;
            case "long":
                className="java.lang.Long";
                break;
            case "boolean":
                className="java.lang.Boolean";
                break;
            case "int":
                className="java.lang.Integer";
                break;
            case "double":
                className="java.lang.Double";
                break;
            case "float":
                className="java.lang.Float";
                break;
        }
        return className;
    }

    private Object GeneratePrimitiveArray(String type,int length) {
        Object obj = null;
        switch (type) {
            case "byte":
                obj=new byte[length];
                break;
            case "char":
                obj = new char[length];
                break;
            case "short":
                obj=new short[length];
                break;
            case "long":
                obj=new long[length];
                break;
            case "boolean":
                obj=new boolean[length];
                break;
            case "int":
                obj=new int[length];
                break;
            case "double":
                obj=new double[length];
                break;
            case "float":
                obj=new float[length];
                break;
        }
        return obj;
    }
}
