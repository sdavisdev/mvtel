/*
DROP DATABASE IF EXISTS MVTel;
CREATE DATABASE mvtel;

GRANT ALL ON mvtel.* TO 'mvtel'@'localhost' IDENTIFIED BY 'mvtel';
GRANT SELECT, INSERT, UPDATE ON `mysql`.`proc` TO 'mvtel'@'localhost'; /*if cannot do this, use: noAccessToProcedureBodies=true*/

USE mvtel;

CREATE TABLE phones (
    p_id INT AUTO_INCREMENT,
    p_name VARCHAR(64) NOT NULL,
    p_category VARCHAR(24) NOT NULL,
    p_desc VARCHAR(4096) NOT NULL,
    p_dir VARCHAR(64) NOT NULL,
    p_imgCount INT,
    PRIMARY KEY(p_id)
);

CREATE TABLE sale_item (
    s_id INT AUTO_INCREMENT,
    s_name VARCHAR(64) NOT NULL,
    s_desc VARCHAR(4096) NOT NULL,
    s_price VARCHAR(24) NOT NULL,
    s_imgCount INT,
    PRIMARY KEY(s_id)
);

CREATE TABLE website_links (
    l_id INT AUTO_INCREMENT,
    l_name VARCHAR(128) NOT NULL,
    l_desc VARCHAR(4096) NOT NULL,
    l_url VARCHAR(128) NOT NULL,
    l_order INT NOT NULL,
    PRIMARY KEY(l_id)
);

CREATE TABLE articles (
    a_id INT AUTO_INCREMENT,
    a_name VARCHAR(128) NOT NULL,
    a_pubDate VARCHAR(64) NOT NULL,
    a_content VARCHAR(8192) NOT NULL,
    a_order INT NOT NULL,
    PRIMARY KEY(a_id)
);

CREATE TABLE registered_emails (
    e_address VARCHAR(128) NOT NULL,
    e_name VARCHAR(128) NOT NULL,
    PRIMARY KEY(e_address)
);

/* Begin GetPhonesByCategory() */
DELIMITER //
CREATE PROCEDURE GetPhonesByCategory(IN categoryName VARCHAR(24))
BEGIN
SELECT p_id, p_name, p_category, p_desc, p_dir, p_imgCount
FROM phones
WHERE p_category = categoryName
ORDER BY p_name;
END //
DELIMITER ;
/* End GetPhonesByCategory() */

/* Begin GetPhoneByName() */
DELIMITER //
CREATE PROCEDURE GetPhoneByName(IN phoneName VARCHAR(24))
BEGIN
SELECT p_id, p_name, p_category, p_desc, p_dir, p_imgCount
FROM phones
WHERE p_name = phoneName;
END //
DELIMITER ;
/* End GetPhoneByName() */

/* Begin SavePhone() */
DELIMITER //
CREATE PROCEDURE 
  SavePhone(
    IN pid INT,
    IN pname VARCHAR(64),
    IN pcategory VARCHAR(24),
    IN pdesc VARCHAR(4096),
    IN pdir VARCHAR(64),
    IN pimgCount INT)
BEGIN

DECLARE hasRecord INT;
SELECT COUNT(*) INTO hasRecord FROM phones WHERE p_id=pid;

IF hasRecord > 0 THEN
  UPDATE phones set p_name=pname, p_category=pcategory, p_desc=pdesc, p_dir=pdir, p_imgCount = pimgCount WHERE p_id=pid;
ELSE
  INSERT INTO phones (p_name, p_category, p_desc, p_dir, p_imgCount) VALUES (pname, pcategory, pdesc, pdir, pimgCount);
END IF;

END //
DELIMITER ;
/* End SavePhone() */

/* Begin DeletePhone */
DELIMITER //
CREATE PROCEDURE 
  DeletePhone(IN phoneId INT)
BEGIN
DELETE
FROM phones
WHERE p_id = phoneId;
END //
DELIMITER ;
/* End DeletePhone */

/* Begin GetPhoneById */
DELIMITER //
CREATE PROCEDURE 
  GetPhoneById(IN phoneId INT)
BEGIN
SELECT p_id, p_name, p_category, p_desc, p_dir, p_imgCount
FROM phones
WHERE p_id = phoneId;
END //
DELIMITER ;
/* End GetPhoneById */

/* Begin GetSaleItems */
DELIMITER //
CREATE PROCEDURE GetSaleItems()
BEGIN
SELECT s_id, s_name, s_desc, s_price, s_imgCount
FROM sale_item
ORDER BY s_name;
END //
DELIMITER ;
/* End GetSaleItems */

/* Begin GetSaleItem */
DELIMITER //
CREATE PROCEDURE GetSaleItem(IN saleItemId INT)
BEGIN
SELECT s_id, s_name, s_desc, s_price, s_imgCount
FROM sale_item
WHERE s_id = saleItemId;
END //
DELIMITER ;
/* End GetSaleItem */

/* Begin SaveSaleItem */
DELIMITER //
CREATE PROCEDURE 
  SaveSaleItem(
    IN sid INT,
    IN sname VARCHAR(64),
    IN sdesc VARCHAR(4096),
    IN sprice VARCHAR(24),
    IN simgCount INT,
    INOUT keyValue INT)
BEGIN

DECLARE hasRecord INT;
SELECT COUNT(*) INTO hasRecord FROM sale_item WHERE s_id=sid;

IF hasRecord > 0 THEN
  UPDATE sale_item set s_name=sname, s_desc=sdesc, s_price=sprice, s_imgCount=simgCount WHERE s_id=sid;
  SET keyValue = sid;
ELSE
  INSERT INTO sale_item (s_name, s_desc, s_price, s_imgCount) VALUES (sname, sdesc, sprice, simgCount);
  SELECT LAST_INSERT_ID() INTO keyValue;
END IF;

END //
DELIMITER ;
/* End SaveSaleItem */

/* Begin DeleteSaleItem */
DELIMITER //
CREATE PROCEDURE 
  DeleteSaleItem(IN saleId INT)
BEGIN
DELETE
FROM sale_item
WHERE s_id = saleId;
END //
DELIMITER ;
/* End DeleteSaleItem */

/* Begin GetLinks */
DELIMITER //
CREATE PROCEDURE GetLinks()
BEGIN
SELECT l_id, l_name, l_desc, l_url, l_order
FROM website_links
ORDER BY l_order ASC;
END //
DELIMITER ;
/* End GetLinks */

/* Begin DeleteLink */
DELIMITER //
CREATE PROCEDURE 
  DeleteLink(IN linkId INT)
BEGIN
DELETE
FROM website_links
WHERE l_id = linkId;
END //
DELIMITER ;
/* End DeleteLink */

/* Begin SaveLink */
DELIMITER //
CREATE PROCEDURE 
  SaveLink(
    IN lid INT,
    IN lname VARCHAR(128),
    IN ldesc VARCHAR(4096),
    IN lurl VARCHAR(128),
    IN lorder INT,
    INOUT keyValue INT)
BEGIN

DECLARE hasRecord INT;
SELECT COUNT(*) INTO hasRecord FROM website_links WHERE l_id=lid;

IF hasRecord > 0 THEN
  UPDATE website_links set l_name=lname, l_desc=ldesc, l_url=lurl, l_order=lorder WHERE l_id=lid;
  SET keyValue = lid;
ELSE
  INSERT INTO website_links (l_name, l_desc, l_url, l_order) VALUES (lname, ldesc, lurl, lorder);
  SELECT LAST_INSERT_ID() INTO keyValue;
END IF;

END //
DELIMITER ;
/* End SaveLink */

/* Begin GetArticle */
DELIMITER //
CREATE PROCEDURE GetArticle(IN artId INT)
BEGIN
SELECT a_id, a_name, a_pubDate, a_content, a_order
FROM articles
WHERE a_id=artId;
END //
DELIMITER ;
/* End GetArticle */

/* Begin GetArticles */
DELIMITER //
CREATE PROCEDURE GetArticles()
BEGIN
SELECT a_id, a_name, a_pubDate, a_content, a_order
FROM articles
ORDER BY a_order ASC, a_id DESC;
END //
DELIMITER ;
/* End GetArticles */

/* Begin DeleteArticle */
DELIMITER //
CREATE PROCEDURE 
  DeleteArticle(IN artId INT)
BEGIN
DELETE
FROM articles
WHERE a_id = artId;
END //
DELIMITER ;
/* End DeleteArticle */

/* Begin SaveArticle */
DELIMITER //
CREATE PROCEDURE 
  SaveArticle(
    IN aid INT,
    IN aname VARCHAR(128),
    IN apubDate VARCHAR(64),
    IN acontent VARCHAR(8192),
    IN aorder INT,
    INOUT keyValue INT)
BEGIN

DECLARE hasRecord INT;
SELECT COUNT(*) INTO hasRecord FROM articles WHERE a_id=aid;

IF hasRecord > 0 THEN
  UPDATE articles set a_name=aname, a_pubDate=apubDate, a_content=acontent, a_order=aorder WHERE a_id=aid;
  SET keyValue = aid;
ELSE
  INSERT INTO articles (a_name, a_pubDate, a_content, a_order) VALUES (aname, apubDate, acontent, aorder);
  SELECT LAST_INSERT_ID() INTO keyValue;
END IF;

END //
DELIMITER ;
/* End SaveArticle */

/* Begin GetEmails */
DELIMITER //
CREATE PROCEDURE GetEmails()
BEGIN
SELECT e_address, e_name
FROM registered_emails
ORDER BY e_address ASC;
END //
DELIMITER ;
/* End GetEmails */

/* Begin DeleteEmail */
DELIMITER //
CREATE PROCEDURE 
  DeleteEmail(IN eAddress VARCHAR(128))
BEGIN
DELETE
FROM registered_emails
WHERE e_address = eAddress;
END //
DELIMITER ;
/* End DeleteEmail */

/* Begin SaveEmail */
DELIMITER //
CREATE PROCEDURE 
  SaveEmail(
    IN eaddress VARCHAR(128),
    IN ename VARCHAR(128))
BEGIN
  INSERT INTO registered_emails (e_address, e_name) VALUES (eaddress, ename);
END //
DELIMITER ;
/* End SaveEmail */