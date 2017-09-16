
DROP TABLE IF EXISTS xiaoqu;
CREATE TABLE xiaoqu
(
  xiaoqu_id BIGINT,
  name VARCHAR(16),
  location VARCHAR(16),
  follower INT ,
  buildTime INT,
  buildingType VARCHAR(8),
  wuyefee VARCHAR(8),
  wuyeCompany VARCHAR(24),
  kaifashang VARCHAR(24),
  buildingNum INT,
  houseNum INT,
  longitude VARCHAR(10),
  lantitude VARCHAR(10),
  PRIMARY KEY (xiaoqu_id)
)
DEFAULT CHARSET = utf8,
ENGINE = InnoDB;

