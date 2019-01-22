package com.gaby.beans;

import lombok.Data;

import java.util.List;

@Data
public class Student {
    private String id;
    private String name;
    private List<String> subjects;
}
