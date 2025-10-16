CREATE DATABASE IF NOT EXISTS njtodo;

use njtodo;

/* General rule for MySQL correct modeling:
    - Many-to-One relations like: project_owned_by_user => project entity gets identified by the user pk as well as its own pk
    - One-to-Many relations like: project_holds_task => task entity gets identified by its own pk + the projects' pk
    - Many-to-Many relations like: User_participate_project => Join Table modeling the relation having a combination of both the tables pks
*/

CREATE TABLE IF NOT EXISTS `user`(
  `id` INT AUTO_INCREMENT NOT NULL,
  `email` VARCHAR(255) UNIQUE NOT NULL,
  `username` VARCHAR(255) UNIQUE NOT NULL,
  `premium` BOOLEAN NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `salt` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `project`(
  `id` INT AUTO_INCREMENT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `owner` VARCHAR(255) NOT NULL REFERENCES user(`username`) ON DELETE CASCADE,
  /* Avoid composite keys, better use Unique key constraints instead */
  UNIQUE KEY `no_duplicate_project_names` (`name`, `owner`),
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `task`(
  `id` INT AUTO_INCREMENT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `pjid` INT NOT NULL REFERENCES project(`id`) ON DELETE CASCADE,
  `state` ENUM('todo', 'ongoing', 'done') NOT NULL,
  `description` TEXT,
  /* Avoid composite keys, better use Unique key constraints instead */
  UNIQUE KEY `no_duplicate_task_names` (`name`,`pjid`),
  PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `user_participate_project`(
  `uid` INT NOT NULL REFERENCES user(`id`) ON DELETE CASCADE,
  `pjid` INT NOT NULL REFERENCES project(`id`) ON DELETE CASCADE,
  `role` VARCHAR(255) NOT NULL,
  /* please, always respect name conventions, Frameworks Will! */
  `added_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (`uid`, `pjid`)
);

CREATE TABLE IF NOT EXISTS `user_execute_task`(
  `uid` INT NOT NULL REFERENCES user(`id`) ON DELETE CASCADE,
  `tid` INT NOT NULL REFERENCES task(`id`) ON DELETE CASCADE,
  /* please, always respect name conventions, Frameworks Will! */
  `started_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (`uid`, `tid`)
);