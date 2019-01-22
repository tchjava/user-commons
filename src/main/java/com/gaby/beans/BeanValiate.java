package com.gaby.beans;

import com.gaby.exception.BaseException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Iterator;


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
    /**
     *
     * @param o 指定对象
     * @param i 检查的层数  0是指如果出现List<T>或者Set<T>这样的成员属性，将不会判断内部是否存在空值。1指对List或Set中的如参数化类型还是非本地类型的话 就要再查一次里面的成员是否为空</></></>
     * @return
     * @throws Exception
     */
    public void isNull(Object o,Integer i) throws Exception {
        boolean flag = false;
        if(o==null){
            throw new BaseException(o.getClass().getSimpleName());
        }
        //得到所有字段
        Field[] fields = o.getClass().getDeclaredFields();
        Field.setAccessible(fields, true);
        for (Field field : fields) {
            if (field.getType().getClassLoader() == null) {

                if (field.getGenericType() instanceof ParameterizedType == false) {
                    //不是参数化类型的成员类型
                    if(field.get(o) == null){
                        flag=true;
                        throw new BaseException(field.getName());
                    }
                }else{
                    if(field.get(o)==null){
                        flag=true;
                        throw new BaseException(field.getName());
                    }
                    if(i==0){
                        continue;
                    }else{
                        //参数化类型
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            Collection collection = (Collection) field.get(o);
                            if(collection.size()==0){
                                throw new BaseException(field.getName());
                            }
                            Iterator iterator = collection.iterator();
                            //取出集合中得第一个对象
                            if (iterator.hasNext()) {
                                //todo 是否在这里做递归
                                //判断是不是自定义类
                                if(iterator.next().getClass().getClassLoader()!=null){
                                    if(i==0){
                                        //todo 什么都不做
                                    }else{
                                        try {
                                            int index=i;
                                            isNull(iterator.next(),--index);
                                        } catch (Exception e1) {
                                            throw new BaseException(e1.getMessage());
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
                //用户自定义类型
            }else{
                if(field.get(o)==null){
                    flag=true;
                    throw new BaseException(field.getName());
                }
            }
        }
    }
}
