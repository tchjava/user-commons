package com.gaby.beans;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BeanValiateTest {
    @Test
    public void testIsLocalBean(){
        BeanValiate beanValiate=new BeanValiate();
        System.out.println(beanValiate.isLocalBean(CustType.class));
        System.out.println(beanValiate.isLocalBean(Integer.class));
    }

    @Test
    public void testIsNullorEmpty() {
        CustType custType=new CustType();
        custType.setUsername("zhangsan");

        List<String> subjects = new ArrayList<String>();
        subjects.add("语文");
        subjects.add("数学");
        //创建学生
        Student student1 = new Student();
        student1.setId("1");
        student1.setName("lisi");
        student1.setSubjects(subjects);

        Student student2 = new Student();
        student2.setId("1");
        student2.setName("lisi");
        student2.setSubjects(subjects);
        Student student3 = new Student();
        student3.setId("1");

        student3.setName("lisi");
        student3.setSubjects(subjects);
        Student student4 = new Student();
        student4.setId("1");
        student4.setName("lisi");
        student4.setSubjects(new ArrayList<String>());

        List<Student> list1 = new ArrayList<Student>();
        List<Student> list2 = new ArrayList<Student>();
        list1.add(student1);
        list1.add(student2);
        custType.setStudents(list1);
        list2.add(student3);
        list2.add(student4);
        custType.setStudents1(list2);
        BeanValiate beanValiate = new BeanValiate();


        try {
            beanValiate.isNull(custType,2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
