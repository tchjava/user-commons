package com.gaby.beans;

import lombok.Data;

import java.util.List;
@Data
public class CustType {
    private String username;
    private int age;
    private List<Student> students;
    private List<Student> students1;
}
