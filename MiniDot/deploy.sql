CREATE TABLE `minidot_buys` (
  `userid` int(11) NOT NULL,
  `item` int(11) NOT NULL,
  PRIMARY KEY (`userid`,`item`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;


CREATE TABLE `minidot_config` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` int(11) NOT NULL DEFAULT '0',
  `slot` varchar(15) NOT NULL,
  `discount` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `minidot_dressed` (
  `userid` int(11) NOT NULL,
  `head` int(11) NOT NULL DEFAULT '-1',
  `body` int(11) NOT NULL DEFAULT '-1',
  `pet` int(11) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED;
