package com.drivingSchool.app.storage;

import com.drivingSchool.app.models.Student;
import java.util.ArrayList;
import java.util.List;

public class InMemoryStudentRepository implements StudentRepository {

    private final List<Student> students = new ArrayList<>();

    @Override
    public void add(Student student) {
        students.add(student);
    }

    @Override
    public List<Student> getAll() {
        return new ArrayList<>(students);
    }

    @Override
    public int count() {
        return students.size();
    }

    @Override
    public boolean isEmpty() {
        return students.isEmpty();
    }
}