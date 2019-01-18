package com.gaby.beans;

import org.junit.Test;

public class BeanValiateTest {
    @Test
    public void testIsLocalBean(){
        BeanValiate beanValiate=new BeanValiate();
        System.out.println(beanValiate.isLocalBean(CustType.class));
        System.out.println(beanValiate.isLocalBean(Integer.class));
    }
}
