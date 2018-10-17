package com.njh.data;

import javax.naming.OperationNotSupportedException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * 将MAP数据转化为java类型,现在只能对基本数据类型和字符串做处理,
 */
public class Map2Java {

    public static <T> T convert(Map<String, Object> srcSource, Class cls) {
        Objects.requireNonNull(srcSource);
        Objects.requireNonNull(cls);
        return convert(srcSource, cls, null);
    }

    public static <T> T convert(Map<String, Object> srcSource, Class cls, Function<Map<String, Object>, Map<String, Object>> functionForSrcSource) {
        Objects.requireNonNull(srcSource);
        Objects.requireNonNull(cls);
        return convert(srcSource, cls, null, functionForSrcSource, null, null);
    }

    public static <T> T convert(Map<String, Object> srcSource, Class cls, Function<Field, Integer> dateConvert, Function<Map<String, Object>, Map<String, Object>> functionForSrcSource) {
        Objects.requireNonNull(srcSource);
        Objects.requireNonNull(cls);
        return convert(srcSource, cls, null, functionForSrcSource, null, dateConvert);
    }

    public static <T> T convert(Map<String, Object> srcSource, Class cls, Supplier<Map<String, Object>> srcSourceSupplier, Function<Map<String, Object>, Map<String, Object>> functionForSrcSource, Supplier<T> srcSupplier, Function<Field, Integer> dateConvert) {
        Supplier supplier = null;
        Object instance = null;
        //类提供器
        if ((Objects.nonNull(srcSupplier) && Objects.nonNull(srcSupplier.get()))) {
            supplier = srcSupplier;
            instance = srcSupplier.get();
        } else {
            //实例化对象
            ObjectSupplier objectSupplier = new ObjectSupplier(cls);
            supplier = objectSupplier;
            instance = objectSupplier.get();
        }
        //元数据提供器
        Map<String, Object> temMap = null;
        if (Objects.nonNull(srcSource)) {
            temMap = srcSource;
        } else {
            temMap = srcSourceSupplier.get();
        }
        //针对名称,转化,名称转化器
        Map<String, Object> objectMap = null;
        if (Objects.nonNull(functionForSrcSource)) {
            objectMap = functionForSrcSource.apply(temMap);
        } else {
            objectMap = temMap;
        }

        //类型值注入器
        Function convert = null;
        if (Objects.nonNull(dateConvert)) {
            convert = dateConvert;
        } else {
            convert = new Convert(objectMap, instance);
        }
        //真正实现,
        Integer countSize = convert(supplier, convert);
        if (!countSize.equals(objectMap.size())) {
            System.out.println("类字段匹配不完全");
        }
        return (T) instance;
    }

    private static Integer convert(Supplier clsSupplier, Function<Field, Integer> convert) {
        //实例化对象
        Object instance = clsSupplier.get();

        Supplier<Field[]> supplierFields = () -> instance.getClass().getDeclaredFields();
        Field[] fields = supplierFields.get();
        Integer countSize = Stream.of(fields).map(convert::apply).reduce(Integer::sum).get();
        return countSize;
    }


    /**
     * 判断数据是否为基本数据类型的包装类型
     * (数据在当成对象交换时，基本数据变量会自动变成包装类型)
     *
     * @param clz 对象类型
     * @return 判断是否为基本数据类型
     */
    public static boolean isPrimitivePredicate(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    static class PrimitivePredicate implements Predicate<Class> {
        @Override
        public boolean test(Class aClass) {
            try {
                return ((Class) aClass.getField("TYPE").get(null)).isPrimitive();
            } catch (Exception e) {
                return false;
            }
        }

        public static boolean predicate(Class aClass) {
            try {
                return ((Class) aClass.getField("TYPE").get(null)).isPrimitive();
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 基本数据类型转化,还是不太好搞,
     *
     * @param obj
     * @param dataType
     * @return
     */
    public static Object convetPrimitive(Object obj, String dataType) {
        Object result = null;
        switch (dataType) {
            case "java.lang.String":
                result = obj.toString();
                break;
            case "string":
                result = obj.toString();
                break;
            case "byte":
                result = Byte.parseByte(obj.toString());
                break;
            case "java.lang.Byte":
                result = Byte.valueOf(obj.toString());
                break;
            case "char":
                result = obj.toString().toCharArray()[0];
                break;
            case "java.lang.Character":
                result = Character.valueOf(obj.toString().toCharArray()[0]);
                break;
            case "short":
                result = Short.parseShort(obj.toString());
                break;
            case "java.lang.Short":
                result = Short.valueOf(obj.toString());
                break;
            case "int":
                result = Integer.parseInt(obj.toString());
                break;
            case "java.lang.Integer":
                result = Integer.valueOf(obj.toString());
                break;
            case "long":
                result = Long.parseLong(obj.toString());
                break;
            case "java.lang.Long":
                result = Long.valueOf(obj.toString());
                break;
            case "float":
                result = Float.parseFloat(obj.toString());
                break;
            case "java.lang.Float":
                result = Float.valueOf(obj.toString());
                break;
            case "double":
                result = Double.parseDouble(obj.toString());
                break;
            case "java.lang.Double":
                result = Double.valueOf(obj.toString());
                break;
            case "boolean":
                result = Boolean.parseBoolean(obj.toString());
                break;
            case "java.lang.Boolean":
                result = Boolean.valueOf(obj.toString());
                break;
            default:
                result = obj;
        }
        return result;
    }

    static class Convert implements Function<Field, Integer> {
        private Map<String, Object> objectMap;
        private Object finalInstance;

        public Convert(Map<String, Object> objectMap, Object finalInstance) {
            this.objectMap = objectMap;
            this.finalInstance = finalInstance;
        }

        @Override
        public Integer apply(Field k) {
            String name = k.getName();
            Object srcField = objectMap.get(name);
            k.setAccessible(true);
            if (Objects.nonNull(srcField)) {
                try {
                    PrimitivePredicate predicate = new PrimitivePredicate();
                    boolean flag = srcField.getClass().isAssignableFrom(k.getType());
                    boolean isPrimitive = k.getType().isPrimitive();
                    boolean isPrimitiveWarpper = predicate.test(k.getType());
                    //基本数据类型，并且是相同数据类型
                    if (isPrimitive && flag) {
                        //传入数据与对象所要求的类型不一致
                        String methodName = "set" + k.getType().getName().substring(0, 1).toUpperCase() + k.getType().getName().substring(1, k.getType().getName().length());
                        k.getClass().getMethod(methodName, Object.class, k.getType()).invoke(k, finalInstance, srcField);
                    }
                    //基本数据类型，但不是相同数据类型
                    else if ((isPrimitiveWarpper || isPrimitive) && !flag) {
                        Object convetPrimitive = convetPrimitive(srcField, k.getType().getName());
                        k.set(finalInstance, convetPrimitive);
                    } else {
                        if (k.getType().isAssignableFrom(String.class)) {
                            k.set(finalInstance, srcField.toString());
                        } else {
                            throw new OperationNotSupportedException("暂不支持出字符串的复杂类型数据类型");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 1;
            }
            return 0;
        }
    }

    static class ObjectSupplier implements Supplier<Object> {
        private Class cls;

        public ObjectSupplier(Class cls) {
            this.cls = cls;
        }

        @Override
        public Object get() {
            Objects.requireNonNull(cls, "对象不可为空");
            Object instance = null;
            try {
                instance = cls.newInstance();
            } catch (Exception e) {
                throw new ClassCastException("对象无法创建,请提供无参构造函数");
            }
            return instance;
        }
    }
}

class A {
    private byte b;
    private Byte bb;
    private char c;
    private short s;
    private Short ss;
    private int i;
    private Integer ii;
    private long l;
    private Long ll;
    private float f;
    private Float ff;
    private double d;
    private Double dd;
    private boolean bbb;
    private Boolean bbbb;

    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }

    public Byte getBb() {
        return bb;
    }

    public void setBb(Byte bb) {
        this.bb = bb;
    }

    public boolean isBbb() {
        return bbb;
    }

    public void setBbb(boolean bbb) {
        this.bbb = bbb;
    }

    public Boolean getBbbb() {
        return bbbb;
    }

    public void setBbbb(Boolean bbbb) {
        this.bbbb = bbbb;
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public short getS() {
        return s;
    }

    public void setS(short s) {
        this.s = s;
    }

    public Short getSs() {
        return ss;
    }

    public void setSs(Short ss) {
        this.ss = ss;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public Integer getIi() {
        return ii;
    }

    public void setIi(Integer ii) {
        this.ii = ii;
    }

    public long getL() {
        return l;
    }

    public void setL(long l) {
        this.l = l;
    }

    public Long getLl() {
        return ll;
    }

    public void setLl(Long ll) {
        this.ll = ll;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public Float getFf() {
        return ff;
    }

    public void setFf(Float ff) {
        this.ff = ff;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public Double getDd() {
        return dd;
    }

    public void setDd(Double dd) {
        this.dd = dd;
    }

    @Override
    public String toString() {
        return "A{" +
                "b=" + b +
                ", bb=" + bb +
                ", c=" + c +
                ", s=" + s +
                ", ss=" + ss +
                ", i=" + i +
                ", ii=" + ii +
                ", l=" + l +
                ", ll=" + ll +
                ", f=" + f +
                ", ff=" + ff +
                ", d=" + d +
                ", dd=" + dd +
                ", bbb=" + bbb +
                ", bbbb=" + bbbb +
                '}';
    }
}
