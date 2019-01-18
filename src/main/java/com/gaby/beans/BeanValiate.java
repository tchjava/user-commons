package com.gaby.beans;

import java.lang.reflect.Field;

/**
* @Description:    关于类型验证的一个类
* @Author:         wengzhongjie
* @CreateDate:     2019-01-18 15:02
*/
public class BeanValiate {
    /**
     * 判断类是用户自定义类的还是本地类
     * @param clazz
     * @return
     */
    public boolean isLocalBean(Class<?> clazz){
        return clazz!=null && clazz.getClassLoader()==null;
    }

    /**
     * 判断类中的成员变量是否是空或者null
     */
    public boolean isEmpty(Object o) {
        //得到字段数组
        Field[] fields=o.getClass().getDeclaredFields();
        //暴力破解
        Field.setAccessible(fields, true);
        //循环遍历判断类中的成员变量是否是本地类型
        for(Field field:fields){
            this.isLocalBean(field.getType());
        }
        return false;
    }
}
