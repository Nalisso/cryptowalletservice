DROP TABLE IF EXISTS registeredUsers;
CREATE TABLE IF NOT EXISTS registeredUsers
(
 userId varchar(10) NOT NULL ,
 walletBackup varchar(65536) DEFAULT NULL,
 PRIMARY KEY (userId)
);