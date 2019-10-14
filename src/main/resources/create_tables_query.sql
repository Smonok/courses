CREATE   TABLE   groups 
(   
   group_id SERIAL PRIMARY KEY NOT NULL,
   group_name VARCHAR (20)
);

CREATE TABLE students
(
   student_id SERIAL PRIMARY KEY NOT NULL,
   group_id INTEGER,
   first_name VARCHAR (20),
   last_name VARCHAR (20)
);

CREATE TABLE courses
(
   course_id SERIAL PRIMARY KEY NOT NULL,
   course_name VARCHAR (20),
   course_description TEXT
);

CREATE TABLE students_courses
(
   student_id INTEGER NOT NULL,
   course_id INTEGER NOT NULL,
   PRIMARY KEY (student_id, course_id),
   FOREIGN KEY(student_id) REFERENCES students(student_id) ON DELETE CASCADE,
   FOREIGN KEY(course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);
