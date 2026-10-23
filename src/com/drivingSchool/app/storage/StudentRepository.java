package com.drivingSchool.app.storage;

import com.drivingSchool.app.models.Student;
import java.util.List;

public interface StudentRepository {
    void add(Student student);
    List<Student> getAll();
    int count();
    boolean isEmpty();
}