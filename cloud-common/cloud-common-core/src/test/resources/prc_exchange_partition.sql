DELIMITER $$ -- 声明语句结束符, 将语句的结束符号从分号临时改为两个$$(可自定义)

DROP PROCEDURE IF EXISTS `prc_exchange_partition` $$

CREATE PROCEDURE prc_exchange_partition(in   prm_biz_table  varchar(32),  -- 业务表
                                        in   prm_tmp_table  varchar(32),  -- 临时表
                                        in   prm_his_table  varchar(32),  -- 历史表
                                        in   prm_rem_days   int,          -- 保留天数
                                        out  prm_ret_code   varchar(32),  -- 错误码
                                        out  prm_ret_msg    varchar(128)) -- 错误消息
BEGIN
  -- 声明变量
  DECLARE int_diff_days int default 0;
  DECLARE var_partition_name varchar(16);
  DECLARE var_partition_description int;

  -- 遍历数据结束标志
  DECLARE done int default 0;
  -- 声明游标
  DEClARE cur_arg CURSOR FOR
           SELECT timestampdiff(day, from_days(partition_description), date_format(now(), '%Y-%m-%d')),
                  partition_name,
                  partition_description
             FROM information_schema.partitions
            WHERE table_schema = schema()
              AND UPPER(table_name) = UPPER(prm_biz_table)
		        ORDER BY partition_ordinal_position ASC;
  -- 将结束标志绑定到游标
  DECLARE continue handler for not found set done = 1;

  -- 初始化变量
  SET prm_ret_code = '000000';
  SET prm_ret_msg  = '';
  -- 使用之前没定义的变量需以@开头
  -- SET @sys_date = now();

  open cur_arg;
    while not done do
      fetch cur_arg into int_diff_days, var_partition_name, var_partition_description;
      if done = 1 then
        if int_diff_days > prm_rem_days then
          -- 增加分区
          -- ALTER TABLE prm_his_table ADD PARTITION ( PARTITION var_partition_name VALUES LESS THAN (var_partition_description) );
          SET @sql = CONCAT('ALTER TABLE ', prm_his_table, ' ADD PARTITION ( PARTITION ', var_partition_name, ' VALUES LESS THAN (', var_partition_description, ') )');
          SELECT @sql;
          PREPARE stmt FROM @sql;
          EXECUTE stmt;
          DEALLOCATE PREPARE stmt;
          -- 删除分区
          -- ALTER TABLE prm_bizTable DROP PARTITION p0;
        end if;
      end if;
    end while;
  close cur_arg;

END $$