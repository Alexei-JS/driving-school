package com.drivingSchool.storage;

import com.drivingSchool.model.Student;
import java.util.List;

public interface StudentRepository {
    void add(Student student);
    List<Student> getAll();
    int count();
    boolean isEmpty();
}