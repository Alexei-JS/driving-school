package com.drivingSchool.app.storage;

// Иморты для работы с SQL
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

// Иморты со встроенными в язык модулями
import java.nio.file.Files;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

// Иморты текущих классов проекта
import com.drivingSchool.app.models.Student;


public class DataBaseStudents implements StudentRepository {
    private final SessionFactory sessionFactory;

    public DataBaseStudents(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void add(Student student) {
        try (Session session = this.sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(student);
                transaction.commit();
            }
            catch (RuntimeException error) {
                transaction.rollback();
                throw new IllegalStateException("Ошибка в записи студента в БД", error);
            }
        }
    }

    @Override
    public List<Student> getAll() {
        // Еще не реализован
    }

    @Override
    public int count() {
        // Еще не реализован
    }

    @Override
    public boolean isEmpty() {
        // Еще не реализован
    }

    @Override
    public void close() {
        this.sessionFactory.close();
    }

}