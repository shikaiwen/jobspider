package com.kevin.utils;

import java.lang.reflect.Field;
import java.util.*;

import static org.apache.commons.lang3.ArrayUtils.toArray;

/**
 * Created by kaiwen on 07/03/2017.
 */
public class BeanUtils {


    public static void main(String[] args) {
        List <String> strings = Arrays.asList("1", "2");
        strings.remove(1);
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
