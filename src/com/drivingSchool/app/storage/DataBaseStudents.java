package com.DrivingSchool.app.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.DrivingSchool.app.model.Student;

public class DataBaseStudents {
        private static final String URL = "jdbc:mysql://localhost:3306/DrivingSchool?serverTimezone=UTC";
        private static final String USER = "admin";
        private static final String PASSWORD = "driving-school";

        public void save(Student student) throws SQLException {
            String sqlRequest = "INSERT INTO Students (id, fullName, age, category, hasMedicalCert) VALUES (?, ?, ?, ?, ?)";
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement ps = connection.prepareStatement(sqlRequest))
            {

                ps.setInt(1, student.getStudentId());
                ps.setString(2, student.getFullName());
                ps.setInt(3, student.getAge());
                ps.setString(4, student.getCategory());
                ps.setBoolean(5, student.getHasMedicalCert());
                ps.executeUpdate();
            }
        }
}