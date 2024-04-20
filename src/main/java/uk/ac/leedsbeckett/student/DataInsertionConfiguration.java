package uk.ac.leedsbeckett.student;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.leedsbeckett.student.model.*;
import uk.ac.leedsbeckett.student.service.LoginService;

/**
 * Configuration class responsible for inserting initial data into the database using CommandLineRunner
 */
@Configuration
public class DataInsertionConfiguration {

    /**
     * Creates a CommandLineRunner bean to execute data insertion operations on application startup
     * @param studentRepository The repository for student data access
     * @param courseRepository  The repository for course data access
     * @param loginRepository   The repository for login data access
     * @return CommandLineRunner instance to insert initial data into the database
     */
    @Bean
    public CommandLineRunner dataLoader(StudentRepository studentRepository,
                                        CourseRepository courseRepository,
                                        LoginRepository loginRepository) {
        return new CommandLineRunner() {


            @Override
            public void run(String... args) throws Exception {

                List<Login> logins = Arrays.asList(
                        new Login("test22@gmail.com", "c3922382", "password", Login.UserType.USER),
                        new Login("test2@gmail.com", "c3781247", "password", Login.UserType.ADMIN));
                loginRepository.saveAll(logins);

                List<Student> students = Arrays.asList(
                        new Student("c3922382", "Adam", "Smrcka"),
                        new Student("c3781247", "Micky", "Mouse"));
                studentRepository.saveAll(students);

                List<Course> courses = Arrays.asList(
                        new Course("Introduction to Programming", "CS101", "Learn basic programming concepts and logic.", 1000),
                        new Course("Database Management", "DBMS202", "Study database design and SQL.", 1200),
                        new Course("Linear Algebra", "MATH301", "Explore vectors, matrices, and linear transformations.", 800),
                        new Course("Data Structures and Algorithms", "DSA303", "Learn essential data structures and algorithms.", 2000),
                        new Course("Web Development", "WEBD405", "Build dynamic websites using HTML, CSS, and JavaScript.", 180),
                        new Course("Computer Networks", "CN404", "Understand network protocols and architectures.", 2500),
                        new Course("Software Engineering", "SE507", "Explore software development methodologies.", 3500),
                        new Course("Artificial Intelligence", "AI601", "Study AI algorithms and applications.", 2800),
                        new Course("Operating Systems", "OS704", "Learn about OS structure and functionality.", 4000),
                        new Course("Machine Learning", "ML802", "Explore statistical models and machine learning algorithms.", 5000),
                        new Course("Digital Image Processing", "DIP905", "Learn techniques for digital image analysis and manipulation.", 3200),
                        new Course("Computer Graphics", "CG1006", "Explore rendering techniques and graphics programming.", 4500),
                        new Course("Mobile Application Development", "MAD1107", "Build mobile apps for iOS and Android platforms.", 1500));
                courseRepository.saveAll(courses);

                students.get(0).enrolInCourse(courses.get(0));
                studentRepository.saveAll(students);
            }
        };
    }

}
