ALTER TABLE `BRANCHES` ADD `TYPE` VARCHAR(255) NOT NULL;
ALTER TABLE `SECTIONS` ADD `TYPE` VARCHAR(255) NOT NULL;
ALTER TABLE `GROUPS` ADD `TYPE` VARCHAR(255) NOT NULL;
ALTER TABLE `GROUPS` ADD `BRANCH_ID` BIGINT(20) NULL DEFAULT NULL;
ALTER TABLE `GROUPS` ADD `POSITION` INT(11) NULL DEFAULT NULL;
ALTER TABLE `SECTIONS` ADD `POSITION` INT(11) NULL DEFAULT NULL;
ALTER TABLE `SECTIONS` ADD `JCOMMUNE_ID` BIGINT(20) NULL DEFAULT NULL;


CREATE  TABLE IF NOT EXISTS `BRANCH_USER_REF` (
  `BRANCH_ID` BIGINT(20) NOT NULL,
  `USER_ID` BIGINT(20) NOT NULL,
  CONSTRAINT `FK_BRANCH_USER_REF_USERS_USER_ID`
    FOREIGN KEY (`USER_ID`)
    REFERENCES `USERS` (`ID`),
  CONSTRAINT `FK_BRANCH`
    FOREIGN KEY (`BRANCH_ID`)
    REFERENCES `BRANCHES` (`BRANCH_ID`));

CREATE  TABLE IF NOT EXISTS `GROUP_USER_REF` (
  `GROUP_ID` BIGINT(20) NOT NULL,
  `USER_ID` BIGINT(20) NOT NULL,
  CONSTRAINT `FK_GROUP_USER_REF_GROUPS_GROUP_ID`
    FOREIGN KEY (`GROUP_ID`)
    REFERENCES `GROUPS` (`GROUP_ID`),
  CONSTRAINT `FK_GROUP_USER_REF_USERS_USER_ID`
    FOREIGN KEY (`USER_ID`)
    REFERENCES `USERS` (`ID`),
  CONSTRAINT `FK_GROUP_USER_REF_GROUPS_GROUP_ID`
    FOREIGN KEY (`GROUP_ID`)
    REFERENCES `GROUPS` (`GROUP_ID`));


CREATE  TABLE IF NOT EXISTS `TOPIC_TYPES`(
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `UUID` VARCHAR(255) NOT NULL ,
  `TITLE` VARCHAR(255) NOT NULL ,
  `DESCRIPTION` VARCHAR(255) NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) ,
  UNIQUE INDEX `UUID` (`UUID` ASC) ,
  UNIQUE INDEX `TITLE` (`TITLE` ASC));