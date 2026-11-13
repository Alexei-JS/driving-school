package com.drivingSchool.app.core;

import com.drivingSchool.app.models.Student;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateConnection{
    private final SessionFactory sessionFactory;

    public HibernateConnection() {
        // Конфигурация создается на основе classpath, в котором лежит hibernate.configuration
        Configuration configuration = new Configuration().addAnnotatedClass(Student.class);

        // Создаем фабрику для создания сессий (пока добавляю описание класса Student.class)
        this.sessionFactory = configuration.buildSessionFactory();
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
}
