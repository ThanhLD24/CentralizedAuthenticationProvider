CREATE DATABASE IF NOT EXISTS centralizedauthenticationprovider;
CREATE USER 'administrator'@'%' IDENTIFIED BY 'admin@###2o25';
GRANT ALL PRIVILEGES ON centralizedauthenticationprovider.* TO 'administrator'@'%';
FLUSH PRIVILEGES;
