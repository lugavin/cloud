DELIMITER //
#该表所在数据库名称
USE `test`//
CREATE PROCEDURE prc_exchange_partition(in   prm_bizTable  varchar(32),  -- 业务表
                                        in   prm_tmpTable  varchar(32),  -- 临时表
                                        in   prm_hisTable  varchar(32),  -- 历史表
                                        in   prm_remDays   int,          -- 保留天数
                                        out  prm_retCode   varchar(32),  -- 错误码
                                        out  prm_retMsg    varchar(128)) -- 错误消息
BEGIN
  -- 声明变量
  DECLARE sys_date TIMESTAMP;
  -- 初始化变量
  SET prm_retCode = '000000';
  SET prm_retMsg  = '';
  SET sys_date = NOW();

  /*
  -- 查看分区信息 
  SELECT partition_name
    FROM information_schema.partitions 
   WHERE table_schema = schema() 
     AND UPPER(table_name)=UPPER(prm_bizTable);
  -- 增加分区
  ALTER TABLE prm_hisTable ADD PARTITION ( PARTITION p20180629 VALUES LESS THAN (20180630) ENGINE=InnoDB );
  -- 删除分区
  ALTER TABLE prm_bizTable DROP PARTITION p0;
  */

END//
DELIMITER;