package uk.ac.leedsbeckett.student;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.leedsbeckett.student.model.*;
import uk.ac.leedsbeckett.student.service.EnrolmentService;
import uk.ac.leedsbeckett.student.service.LoginService;


@Configuration
public class DataInsertionConfiguration {

    @Autowired
    private LoginService loginService;

    @Bean
    public CommandLineRunner dataLoader(StudentRepository studentRepository,
                                        CourseRepository courseRepository,
                                        LoginRepository loginRepository) {
        return new CommandLineRunner() {


            @Override
            public void run(String... args) throws Exception {



                /*
                // Insert 2 students and their login details
                Login login1 = new Login();
                login1.setEmail("test22@gmail.com");
                login1.setDefStudentID("c3922382");
                login1.setPassword("password");

                Login login2 = new Login();
                login2.setEmail("test2@gmail.com");
                login2.setStudentID("c3781247");
                login2.setPassword("password");

                loginRepository.saveAll(Arrays.asList(login1, login2));


                Student student1 = new Student();
                student1.setExternalStudentId("c3922382");
                student1.setForename("Adam");
                student1.setSurname("Smrcka");

                Student student2 = new Student();
                student2.setExternalStudentId("c3781247");
                student2.setForename("Micky");
                student2.setSurname("Mouse");

                studentRepository.saveAll(Arrays.asList(student1, student2));


                // Insert 3 courses
                Course course1 = new Course();
                course1.setTitle("Introduction to Programming");
                course1.setAbbreviation("CS101");
                course1.setFee(1000);

                Course course2 = new Course();
                course2.setTitle("Database Management");
                course2.setAbbreviation("DBMS202");
                course2.setFee(1200);

                Course course3 = new Course();
                course3.setTitle("Linear Algebra");
                course3.setAbbreviation("MATH301");
                course3.setFee(800);

                // Creating 10 additional courses with fees ranging between 150 and 5000
                Course course4 = new Course();
                course4.setTitle("Data Structures and Algorithms");
                course4.setAbbreviation("DSA303");
                course4.setFee(2000);

                Course course5 = new Course();
                course5.setTitle("Web Development");
                course5.setAbbreviation("WEBD405");
                course5.setFee(180);

                Course course6 = new Course();
                course6.setTitle("Computer Networks");
                course6.setAbbreviation("CN404");
                course6.setFee(2500);

                Course course7 = new Course();
                course7.setTitle("Software Engineering");
                course7.setAbbreviation("SE507");
                course7.setFee(3500);

                Course course8 = new Course();
                course8.setTitle("Artificial Intelligence");
                course8.setAbbreviation("AI601");
                course8.setFee(2800);

                Course course9 = new Course();
                course9.setTitle("Operating Systems");
                course9.setAbbreviation("OS704");
                course9.setFee(4000);

                Course course10 = new Course();
                course10.setTitle("Machine Learning");
                course10.setAbbreviation("ML802");
                course10.setFee(5000);

                Course course11 = new Course();
                course11.setTitle("Digital Image Processing");
                course11.setAbbreviation("DIP905");
                course11.setFee(3200);

                Course course12 = new Course();
                course12.setTitle("Computer Graphics");
                course12.setAbbreviation("CG1006");
                course12.setFee(4500);

                Course course13 = new Course();
                course13.setTitle("Mobile Application Development");
                course13.setAbbreviation("MAD1107");
                course13.setFee(1500);

                courseRepository.saveAll(Arrays.asList(course1, course2, course3));
                */

                // Insert 2 students and their login details
                List<Login> logins = Arrays.asList(
                        new Login("test22@gmail.com", "c3922382", "password"),
                        new Login("test2@gmail.com", "c3781247", "password"));
                loginRepository.saveAll(logins);

                List<Student> students = Arrays.asList(
                        new Student("c3922382", "Adam", "Smrcka"),
                        new Student("c3781247", "Micky", "Mouse"));
                studentRepository.saveAll(students);
                // Inserting all courses
                List<Course> courses = Arrays.asList(
                        new Course("Introduction to Programming", "CS101", 1000),
                        new Course("Database Management", "DBMS202", 1200),
                        new Course("Linear Algebra", "MATH301", 800),
                        new Course("Data Structures and Algorithms", "DSA303", 2000),
                        new Course("Web Development", "WEBD405", 180),
                        new Course("Computer Networks", "CN404", 2500),
                        new Course("Software Engineering", "SE507", 3500),
                        new Course("Artificial Intelligence", "AI601", 2800),
                        new Course("Operating Systems", "OS704", 4000),
                        new Course("Machine Learning", "ML802", 5000),
                        new Course("Digital Image Processing", "DIP905", 3200),
                        new Course("Computer Graphics", "CG1006", 4500),
                        new Course("Mobile Application Development", "MAD1107", 1500));
                courseRepository.saveAll(courses);

                students.get(0).enrolInCourse(courses.get(0));
                studentRepository.saveAll(students);
            }
        };
    }

}
