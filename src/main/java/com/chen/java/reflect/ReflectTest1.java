package com.chen.java.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * @desc
 * @Author Chentian
 * @date 2021/1/18
 */
public class ReflectTest1 {

    public static void main(String[] args) throws Exception {

        Class<?> stuClass = Class.forName("com.chen.java.reflect.Student");

        //Constructor 提供关于类的单个构造方法的信息以及对它的访问权限。
        Constructor<?>[] constructors = stuClass.getConstructors();
        Object obj1 = constructors[0].newInstance();
        System.out.println(obj1);

        Object obj2 = constructors[1].newInstance("chen",1);
        System.out.println(obj2);

        //Field 提供有关类或接口的单个字段的信息，以及对它的动态访问权限。
        Field[] fields = stuClass.getFields();
        System.out.print("fields length="+fields.length+" field:");
        for (Field field : fields){
            System.out.print(field.getName()+",");
        }
        System.out.println();

        Field[] declaredFields = stuClass.getDeclaredFields();
        System.out.print("declaredFields length="+declaredFields.length+" field:");
        for (Field field : declaredFields){
            System.out.print(field.getName()+"-"+field.getModifiers()+",");
        }
        System.out.println();

        //Method 提供关于类或接口上单独某个方法（以及如何访问该方法）的信息
        Method[] methods = stuClass.getMethods();
        System.out.print("methods length="+methods.length+" method:");
        for (Method method : methods){
            System.out.print(method.getName()+"-"+method.getModifiers()+",");
        }
        System.out.println();

        Method[] declaredMethods = stuClass.getDeclaredMethods();
        System.out.print("declaredMethods length="+declaredMethods.length+" declaredMethod:");
        for (Method method : declaredMethods){
            System.out.print(method.getName()+"-"+method.getModifiers()+",");

        }
        System.out.println();

        int modifiers = stuClass.getModifiers();
        System.out.println(modifiers);
    }


}
