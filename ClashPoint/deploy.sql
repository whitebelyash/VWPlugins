CREATE TABLE `cp_stats` (
  `userid` int(11) NOT NULL,
  `trail` varchar(20) DEFAULT NULL,
  `kills` int(11) NOT NULL DEFAULT '0',
  `deaths` int(11) NOT NULL DEFAULT '0',
  `wins` int(11) NOT NULL DEFAULT '0',
  `games` int(11) NOT NULL DEFAULT '0',
  `resourcePointsBreaked` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
