package com.kevin.utils;

import org.assertj.core.api.Assert;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by kaiwen on 07/03/2017.
 */
public class BeanUtils {


    public static void main(String[] args) {
        List <String> strings = Arrays.asList("1", "2");
        strings.remove(1);
    }


    public static Object getPropertyVal(String propertyName,Object obj){

        try {

            if(obj instanceof  Map){
                Map data = (Map) obj;
                Object val = data.get(propertyName);
                return val;
            }

            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            Map <String, PropertyDescriptor> discriptorMap = new HashMap <>();

            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor prop = propertyDescriptors[i];

                if(prop.getName().compareToIgnoreCase("class") == 0) continue;

                discriptorMap.put(prop.getName(), prop);
            }

            PropertyDescriptor propertyDescriptor = discriptorMap.get(propertyName);
            if(propertyDescriptor == null) return null;
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if(writeMethod == null) return null;

            Object result = writeMethod.invoke(obj, null);

            return result;

        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * copy object properties to HashMap
     * @param source
     * @return
     */
    public static Map<String,Object> copyPropertiesToMap(Object source,String... propertyNames) {
        Map result = new HashMap <>();

        Field[] declaredFields = source.getClass().getDeclaredFields();

        if(declaredFields.length == 0)  {
            return result;
        }

        List <String> ignores = Arrays.asList(propertyNames);
        List <Field> filteredList = new ArrayList <>();
        for (int i = 0; i < declaredFields.length; i++) {
            Field f = declaredFields[i];
            if(!ignores.contains(f.getName())){
                filteredList.add(f);
            }
        }

        //TODO 不能像下面一样强制转换，会报错，可以去研究内部实现机制
//        Field [] copyFields = (Field [])filteredList.toArray();
        Field[] copyFields = filteredList.toArray(new Field[filteredList.size()]);

        copyFieldsToMap(source,copyFields, result);

        return result;
    }

    /**
     * copy object properties to HashMap
     * @param source
     * @return
     */
    public static Map<String,Object> copyPropertiesToMap(Object source) {
        Map result = new HashMap <>();
        Field[] declaredFields = source.getClass().getDeclaredFields();
        if(declaredFields.length == 0)  {
            return result;
        }

        copyFieldsToMap(source,declaredFields, result);

        return result;
    }


    private static Map<String, Object> copyFieldsToMap(Object source,Field[] fields, Map <String, Object> map) {
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            try {
                field.setAccessible(true);
                Object o = field.get(source);
                map.put(field.getName(), o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
