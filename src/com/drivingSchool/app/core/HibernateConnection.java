package com.drivingSchool.app.core;

import com.drivingSchool.app.models.Student;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Properties;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;


public class HibernateConnection{
    private final SessionFactory sessionFactory;

    public HibernateConnection() {
        // Конфигурация создается на основе classpath, в котором лежит hibernate.configuration
        Configuration configuration = new Configuration()
                .addAnnotatedClass(Student.class)
                .setProperty("hibernate.hbm2ddl.auto", "validate"); // Для контроля полей таблицы

        this.createDataBaseStudents(configuration);

        // Создаем фабрику для создания сессий (пока добавляю описание класса Student.class)
        this.sessionFactory = configuration.buildSessionFactory();
    }

    private void createDataBaseStudents(Configuration configuration) {
        Properties properties = configuration.getProperties();

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("jakarta.persistence.jdbc.url"),
                properties.getProperty("jakarta.persistence.jdbc.user"),
                properties.getProperty("jakarta.persistence.jdbc.password")))
        {
            String sqlSchema = Files.readString(Path.of("db/schema.sql"));
            Statement executorQuery = connection.createStatement();
            executorQuery.execute(sqlSchema);
        }
        catch (IOException error) {
            throw new RuntimeException("Файла создания БД не существует или неправильный указан путь", error);
        }
        catch (SQLException error) {
            throw new IllegalStateException("Не удалось подключиться к БД или выполнить скрипт создания таблиц", error);
        }
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
}
