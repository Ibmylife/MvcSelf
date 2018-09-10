package com.njh.controllerUtils;

import jdk.nashorn.internal.runtime.logging.Logger;
import com.njh.GlobalChain.InvokeHandlerChain;
import com.njh.annation.Controller;
import com.njh.annation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class controllerUtils {
    private static String scanPackage;
    private static List<String> className = new ArrayList<>();
    //保存,类名和类
    private static Map<String, Object> classForController = new HashMap<>();
    //保存,类和类方法
    private static Map<Object, String> objectForControllerMethod = new HashMap<>();
    //保存,映射地址和类
    private static Map<String, Object> urlForControllerObject = new HashMap<>();
    //保存,映射地址和方法
    private static Map<String, Method> urlForControllerMethod = new HashMap<>();

    static {
        try {
            Properties pro = new Properties();
            pro.load(controllerUtils.class.getClassLoader().getResourceAsStream("config.properties"));
            scanPackage = pro.getProperty("scanPackages");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deal(Map<String, Object> classForControlle,
                     Map<Object, String> objectForControllerMethod,
                     Map<String, Object> urlForControllerObject,
                     Map<String, Method> urlForControllerMethod)
            throws Exception {
        controllerUtils.classForController = classForController;
        controllerUtils.objectForControllerMethod = objectForControllerMethod;
        controllerUtils.urlForControllerObject = urlForControllerObject;
        controllerUtils.urlForControllerMethod = urlForControllerMethod;
        scanClassPath();
        dealScanClass();
    }

    private void scanClassPath() throws Exception {

        String scanPackagePath = scanPackage.replace(".", "/");
        Path path = new File(controllerUtils.class.getClassLoader().getResource("").getPath() + scanPackagePath).toPath();
        Files.walkFileTree(path, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String name = file.toFile().getName();
                className.add(scanPackage + "." + name.substring(0,name.lastIndexOf(".")));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void dealScanClass() throws Exception {
        Controller controller = null;
        RequestMapping methodAnn = null;
        for (String name : className) {
            Class<?> scanClass = Class.forName(name);
            if (scanClass.isAnnotationPresent(Controller.class)) {
                Object instance = Class.forName(name).newInstance();
                //保存,类名和类
                put(classForController, name, instance);
                controller = scanClass.getAnnotation(Controller.class);
                for (Method method : scanClass.getMethods()) {
                    methodAnn = method.getAnnotation(RequestMapping.class);
                    if (methodAnn == null) {
                        continue;
                    }
                    //保存,类和类方法名
                    put(objectForControllerMethod, instance, method.getName());
                    //保存,映射地址和方法
                    put(urlForControllerMethod, controller.name() + methodAnn.name(), method);
                    //保存,映射地址和类
                    put(urlForControllerObject, controller.name() + methodAnn.name(), instance);
                }
            }
        }
    }

    private boolean put(Map map, Object obj, Object val) {
        if (map.get(obj) == null) {
            map.put(obj, val);
            return true;
        } else {
            System.out.println("在本" + map.getClass().getSimpleName() + "中已经缓存了");
            return false;
        }
    }
}
