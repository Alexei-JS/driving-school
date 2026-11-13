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
import org.hibernate.query.Query;

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
        try (Session session = this.sessionFactory.openSession()) {
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
        try (Session session = this.sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();

            try {
                String hqlQuery = "FROM Students";
                Query<Student> query = session.createQuery(hqlQuery, Student.class);
                List<Student> result = query.getResultList();

                transaction.commit();
                return result;
            }
            catch (RuntimeException error) {
                transaction.rollback();
                throw new IllegalStateException("Ошибка при запросе списка всех студентов", error);
            }
        }
    }

    @Override
    public int count() {
        try (Session session = this.sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                String hqlQuery = "SELECT CAST(COUNT(*) AS INTEGER) FROM Students";
                Query<Integer> query = session.createQuery(hqlQuery, Integer.class);
                Integer result = query.getSingleResult();

                transaction.commit();
                return (result != null) ? (result) : (0);
            }
            catch (RuntimeException error) {
                transaction.rollback();
                throw new IllegalStateException("Ошибка при подсчете количества студентов", error);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return this.count() == 0;
    }

    @Override
    public void close() {
        this.sessionFactory.close();
    }

}