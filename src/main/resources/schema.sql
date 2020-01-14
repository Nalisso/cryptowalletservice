SELECT 'CREATE DATABASE wallet'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'wallet');

CREATE TABLE IF NOT EXISTS wallet
(
 refId varchar(10) NOT NULL ,
 keyValue varchar(65536) DEFAULT NULL,
 PRIMARY KEY (refId)
);