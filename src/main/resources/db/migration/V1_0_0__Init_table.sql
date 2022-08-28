CREATE TABLE IF NOT EXISTS courses
(
    id          SERIAL,
    course_name VARCHAR(50) NOT NULL UNIQUE,
    catalog_id  INTEGER     NOT NULL,
    CONSTRAINT pk_courses_id PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS courses_tags
(
    id        SERIAL,
    course_id INTEGER NOT NULL,
    tag_id    INTEGER NOT NULL,
    CONSTRAINT pk_courses_tags_id PRIMARY KEY (id),
    CONSTRAINT fk_course_id_id FOREIGN KEY (course_id) REFERENCES courses (id)
);

INSERT INTO courses (id, course_name, catalog_id)
VALUES (1, 'Java for Beginners', 1),
       (2, 'JavaScript for Beginners', 2)