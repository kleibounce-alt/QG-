CREATE TABLE admin (
            id BIGINT NOT NULL,
            password CHAR(10) NOT NULL,
            PRIMARY KEY (id)
);

CREATE TABLE report (
            id INT NOT NULL AUTO_INCREMENT,
            studentId BIGINT DEFAULT NULL,
            deviceType VARCHAR(20) DEFAULT NULL,
            description VARCHAR(40) DEFAULT NULL,
            status ENUM('已完成', '未完成') NOT NULL,
            time varchar(30) DEFAULT NULL,
            PRIMARY KEY (id)
);

CREATE TABLE student (
            id BIGINT NOT NULL,
            password CHAR(10) NOT NULL,
            dormitory VARCHAR(20) DEFAULT '',
            roomId VARCHAR(20) DEFAULT '',
            PRIMARY KEY (id)
);