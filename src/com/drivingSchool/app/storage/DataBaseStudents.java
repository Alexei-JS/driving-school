package com.drivingSchool.app.storage;

// Иморты для работы с SQL
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

// Иморты со встроенными в язык модулями
import java.nio.file.Files;
import java.io.IOException;
import java.util.List;

// Импорты для исключений
import java.lang.RuntimeException;

// Иморты текущих классов проекта
import com.drivingSchool.app.models.Student;


public class DataBaseStudents implements StudentRepository {
        private static final String URL = "jdbc:mysql://localhost:3306/DrivingSchool?serverTimezone=UTC";
        private static final String USER = "admin";
        private static final String PASSWORD = "driving-school";

        private Connection connection;

        public void DataBaseStudents() {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                this.connection = connection;
            }
            catch (SQLException error) {
                throw new RuntimeException("Ошибка подключения к БД");
            }

            // Создаем базу данных, через подключение connection.
            try {
                String sqlSchema = Files.readString(Path.of("db/schema.sql"));
                Statement sqlExecutor = this.connection.createStatement();
                sqlExecutor.execute(sqlSchema);
            }
            catch (IOException error) {
                throw new RuntimeException("Файла создания БД не существует или неправильный указан путь");
            }
            catch (SQLException error) {
                throw new RuntimeException("При подключении отправлении запроса на создания БД произошла ошибка");
            }
        }


        @Override
        public void add(Student student) {
            String sqlRequest = "INSERT INTO Students (id, fullName, age, category, hasMedicalCert) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement studentInsertQuery = this.connection.prepareStatement(sqlRequest)) {

                studentInsertQuery.setInt(1, student.getStudentId());
                studentInsertQuery.setString(2, student.getFullName());
                studentInsertQuery.setInt(3, student.getAge());
                studentInsertQuery.setString(4, student.getCategory());
                studentInsertQuery.setBoolean(5, student.hasMedicalCert());
                studentInsertQuery.executeUpdate(); // Выполнение запроса на вставку данных
            }
            catch (SQLException error) {
                throw new IllegalStateException("Ошибка в записи студента в БД");
            }
        }

        @Override
        public List<Student> getAll() {
            throw new UnsupportedOperationException("Метод getAll еще не реализован.");
        }

        @Override
        public int count() {
            throw new UnsupportedOperationException("Метод count еще не реализован.");
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException("Метод isEmpty еще не реализован.");
        }

}