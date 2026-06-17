CREATE TABLE `sw_kits` (
  `userid` int(11) NOT NULL,
  `kit` varchar(20) NOT NULL,
  PRIMARY KEY (`userid`,`kit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;


CREATE TABLE `sw_stats` (
  `userid` int(11) NOT NULL,
  `trail` varchar(20) DEFAULT NULL,
  `kit` varchar(20) DEFAULT NULL,
  `wins` int(11) NOT NULL DEFAULT '0',
  `games` int(11) NOT NULL DEFAULT '0',
  `kills` int(11) NOT NULL DEFAULT '0',
  `deaths` int(11) NOT NULL DEFAULT '0',
  `arrowsFired` int(11) NOT NULL DEFAULT '0',
  `blocksBroken` int(11) NOT NULL DEFAULT '0',
  `blocksPlaced` int(11) NOT NULL DEFAULT '0',
  `currentWinStreak` int(11) NOT NULL DEFAULT '0',
  `winStreak` int(11) NOT NULL DEFAULT '0',
  `u_arrow` tinyint(3) NOT NULL DEFAULT '0',
  `u_blazeArrow` tinyint(3) NOT NULL DEFAULT '0',
  `u_juggernaut` tinyint(3) NOT NULL DEFAULT '0',
  `u_speedBoost` tinyint(3) NOT NULL DEFAULT '0',
  `u_resistance` tinyint(3) NOT NULL DEFAULT '0',
  `u_redstoneHeart` tinyint(3) NOT NULL DEFAULT '50',
  `u_enderman` tinyint(3) NOT NULL DEFAULT '0',
  `u_builder` tinyint(3) NOT NULL DEFAULT '0',
  `u_zombie` tinyint(3) NOT NULL DEFAULT '0',
  `u_enchanter` tinyint(3) NOT NULL DEFAULT '0',
  `u_goldenApple` tinyint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;
