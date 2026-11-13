package com.drivingSchool.app.storage;

import com.drivingSchool.app.models.Student;
import java.util.List;

// Интерфейс наследует другой интерфейс
public interface StudentRepository extends AutoCloseable {
    void add(Student student);
    List<Student> getAll();
    int count();
    boolean isEmpty();

    default void close() {};
}