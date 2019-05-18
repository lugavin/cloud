DELIMITER $$ -- 声明语句结束符, 将语句的结束符号从分号临时改为两个$$(可自定义)

DROP PROCEDURE IF EXISTS `prc_exchange_partition` $$

CREATE PROCEDURE prc_exchange_partition(in   prm_biz_table  varchar(32),  -- 业务表
                                        in   prm_his_table  varchar(32),  -- 历史表
                                        in   prm_tmp_table  varchar(32),  -- 临时表
                                        in   prm_rem_days   int,          -- 保留天数
                                        out  prm_ret_code   varchar(32),  -- 错误码
                                        out  prm_ret_msg    varchar(128)) -- 错误消息
BEGIN
  -- 声明变量
  DECLARE int_diff_days int default 0;
  DECLARE var_partition_name varchar(16);
  DECLARE var_partition_description int;
  DECLARE int_table_rows int;
  DECLARE int_cnt int;

  -- 遍历数据结束标志
  DECLARE done BOOLEAN default FALSE;
  -- 声明游标
  DEClARE cur_arg CURSOR FOR
           SELECT timestampdiff(day, from_days(partition_description), date(now())),
                  partition_name,
                  partition_description,
                  table_rows
             FROM information_schema.partitions
            WHERE table_schema = schema()
              AND UPPER(table_name) = UPPER(prm_biz_table)
		        ORDER BY partition_ordinal_position ASC;
  -- 将结束标志绑定到游标
  DECLARE continue handler for not found set done = TRUE;

  -- 异常处理
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
		GET DIAGNOSTICS CONDITION 1
		@ERR_NO = MYSQL_ERRNO, @ERR_MSG = MESSAGE_TEXT;
		SET prm_ret_code = '999999';
		SET prm_ret_msg  = CONCAT('调用存储过程 prc_exchange_partition 移动分区数据出错, 错误编号: ', @ERR_NO, ', 错误信息: ', @ERR_MSG);
  END;

  -- 初始化变量
  SET prm_ret_code = '000000';
  SET prm_ret_msg  = '';
  -- 使用之前没定义的变量需以@开头
  -- SET @sys_date = now();

  open cur_arg;
    while not done do
      fetch cur_arg into int_diff_days, var_partition_name, var_partition_description, int_table_rows;
      if int_diff_days > prm_rem_days then
          if int_table_rows > 0 then
            SELECT count(1)
              INTO int_cnt
              FROM information_schema.partitions
             WHERE table_schema = schema()
               AND UPPER(table_name) = UPPER(prm_his_table)
               AND partition_description = var_partition_description;
            -- (1)若历史表指定分区不存在则增加分区
            if int_cnt = 0 then
              SET @sql = CONCAT('ALTER TABLE ', prm_his_table, ' ADD PARTITION ( PARTITION ', var_partition_name, ' VALUES LESS THAN (', var_partition_description, ') )');
              SELECT @sql;            -- 打印SQL
              PREPARE stmt FROM @sql; -- 预处理执行的SQL(其中stmt是一变量)
              EXECUTE stmt;           -- 执行SQL语句
              DEALLOCATE PREPARE stmt;
            end if;
            -- (2)将业务表分区中的数据移动到临时表中
            SET @sql = CONCAT('ALTER TABLE ', prm_biz_table, ' EXCHANGE PARTITION ', var_partition_name, ' WITH TABLE ', prm_tmp_table);
            SELECT @sql;
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
            -- (3)将临时表中的数据移动到历史表中
            SET @sql = CONCAT('ALTER TABLE ', prm_his_table, ' EXCHANGE PARTITION ', var_partition_name, ' WITH TABLE ', prm_tmp_table);
            SELECT @sql;
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
          end if;
          -- (4)删除业务表分区
          SET @sql = CONCAT('ALTER TABLE ', prm_biz_table, ' DROP PARTITION ', var_partition_name);
          PREPARE stmt FROM @sql;
          EXECUTE stmt;
          DEALLOCATE PREPARE stmt;
      end if;
    end while;
  close cur_arg;

END $$