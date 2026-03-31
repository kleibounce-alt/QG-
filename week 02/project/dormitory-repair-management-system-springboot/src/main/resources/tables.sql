CREATE TABLE admin (
                       id BIGINT NOT NULL,
                       password CHAR(225) NOT NULL,
                       PRIMARY KEY (id)
);

CREATE TABLE report (
                        id INT NOT NULL AUTO_INCREMENT,
                        studentId BIGINT DEFAULT NULL,
                        deviceType VARCHAR(225) DEFAULT NULL,
                        description VARCHAR(225) DEFAULT NULL,
                        status ENUM('已完成', '未完成') NOT NULL,
                        time varchar(225) DEFAULT NULL,
                        PRIMARY KEY (id)
);

CREATE TABLE student (
                         id BIGINT NOT NULL,
                         password CHAR(225) NOT NULL,
                         dormitory VARCHAR(225) DEFAULT '',
                         roomId VARCHAR(225) DEFAULT '',
                         PRIMARY KEY (id)
);

CREATE TABLE report_image (
                                id bigint(20) NOT NULL AUTO_INCREMENT,
                                report_id bigint(20) NOT NULL,
                                image_path varchar(255) NOT NULL,
                                original_name varchar(255) DEFAULT NULL,
                                create_time bigint(20) DEFAULT NULL,
                                PRIMARY KEY (id)
);