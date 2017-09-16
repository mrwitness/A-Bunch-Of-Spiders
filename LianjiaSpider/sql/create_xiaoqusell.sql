DROP TABLE IF EXISTS xiaoqu_sell;
CREATE TABLE xiaoqu_sell
(
  xiaoqu_id     BIGINT,
  url           VARCHAR(128),
  price         INT,
  priceMonth    VARCHAR(3),
  sellTao       INT,
  chengjiaoTime VARCHAR(4),
  chengjiaoTao  INT,
  rentTao       INT,
  PRIMARY KEY (xiaoqu_id)
)
  DEFAULT CHARSET = utf8,
  ENGINE = InnoDB;
CREATE INDEX idx_price
  ON xiaoqu_sell (price);
CREATE INDEX idx_sellTao
  ON xiaoqu_sell (sellTao);

