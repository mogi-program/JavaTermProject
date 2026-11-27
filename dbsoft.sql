-- 1. 강의 테이블 (COURSE)
CREATE TABLE COURSE (
    course_id   INTEGER      NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    CONSTRAINT pk_course PRIMARY KEY (course_id)
);

-- 2. 강의 시간표 테이블 (LECTURE_SCHEDULE)
CREATE TABLE LECTURE_SCHEDULE (
    schedule_id INTEGER    NOT NULL,
    course_id   INTEGER    NOT NULL,
    day_of_week INTEGER    NOT NULL,
    start_time  VARCHAR(5) NOT NULL,
    end_time    VARCHAR(5) NOT NULL,
    CONSTRAINT pk_lecture_schedule PRIMARY KEY (schedule_id),
    CONSTRAINT fk_schedule_course  FOREIGN KEY (course_id) REFERENCES COURSE(course_id),
    CONSTRAINT chk_day_of_week     CHECK (day_of_week BETWEEN 0 AND 7)
);

-- 3. 과제 테이블 (ASSIGNMENT)
CREATE TABLE ASSIGNMENT (
    assignment_id INTEGER      NOT NULL,
    course_id     INTEGER      NOT NULL,
    start_date    DATE         NOT NULL,
    due_date      DATE         NOT NULL,
    content       VARCHAR(500) NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_assignment        PRIMARY KEY (assignment_id),
    CONSTRAINT fk_assignment_course FOREIGN KEY (course_id) REFERENCES COURSE(course_id),
    CONSTRAINT chk_status           CHECK (status IN ('INCOMPLETE', 'COMPLETE'))

);