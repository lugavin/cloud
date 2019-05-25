DELIMITER $$ -- 声明语句结束符, 将语句的结束符号从分号临时改为两个$$(可自定义)

DROP PROCEDURE IF EXISTS `prc_exchange_partition` $$

CREATE PROCEDURE prc_exchange_partition(IN   prm_biz_table  VARCHAR(32),  -- 业务表
                                        IN   prm_his_table  VARCHAR(32),  -- 历史表
                                        IN   prm_tmp_table  VARCHAR(32),  -- 临时表
                                        IN   prm_rem_days   INT,          -- 保留天数
                                        OUT  prm_ret_code   VARCHAR(32),  -- 错误码
                                        OUT  prm_ret_msg    VARCHAR(128)) -- 错误消息
BEGIN
  -- 声明变量
  DECLARE GN_DEF_OK  VARCHAR(6) DEFAULT '000000';
  DECLARE GN_DEF_ERR VARCHAR(6) DEFAULT '999999';

  DECLARE int_diff_days INT DEFAULT 0;
  DECLARE var_partition_name VARCHAR(16);
  DECLARE var_partition_description INT;
  DECLARE int_table_rows INT;
  DECLARE int_cnt INT;

  -- 声明游标
  DEClARE cur_arg CURSOR FOR
           SELECT timestampdiff(DAY, from_days(partition_description), DATE(now())),
                  partition_name,
                  partition_description,
                  table_rows
             FROM information_schema.partitions
            WHERE table_schema = SCHEMA()
              AND UPPER(table_name) = UPPER(prm_biz_table)
		        ORDER BY partition_ordinal_position ASC;
  -- 将结束标志绑定到游标
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET @done = TRUE;

  -- 异常处理
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
		GET DIAGNOSTICS CONDITION 1
		@err_no = MYSQL_ERRNO, @err_msg = MESSAGE_TEXT; -- 使用之前没定义的变量需以@开头
		SET prm_ret_code = GN_DEF_ERR;
		SET prm_ret_msg  = CONCAT('调用存储过程 prc_exchange_partition 移动分区数据出错, 错误编号: ', @err_no, ', 错误信息: ', @err_msg);
  END;

  -- 初始化变量
  SET prm_ret_code = GN_DEF_OK;
  SET prm_ret_msg  = '';

  SET @done = FALSE; -- 遍历数据结束标志

  SET @sql = CONCAT('SELECT COUNT(1) INTO @int_cnt FROM ', prm_tmp_table);
  SELECT @sql;            -- 打印SQL
  PREPARE stmt FROM @sql; -- 预处理执行的SQL(其中stmt是一变量)
  EXECUTE stmt;           -- 执行SQL语句
  DEALLOCATE PREPARE stmt;
  -- IF @int_cnt > 0 THEN
  --   SET prm_ret_code = GN_DEF_ERR;
  --   SET prm_ret_msg  = CONCAT('历史表 ', prm_tmp_table, ' 中存在数据, 无法进行分区数据交换, 请先处理');
  --   RETURN;
  -- END IF;

  OPEN cur_arg;
    WHILE not @done do
      FETCH cur_arg INTO int_diff_days, var_partition_name, var_partition_description, int_table_rows;
      IF int_diff_days > prm_rem_days THEN
          IF int_table_rows > 0 THEN
            SELECT COUNT(1)
              INTO int_cnt
              FROM information_schema.partitions
             WHERE table_schema = SCHEMA()
               AND UPPER(table_name) = UPPER(prm_his_table)
               AND partition_description = var_partition_description;
            -- (1)若历史表指定分区不存在则增加分区
            IF int_cnt = 0 THEN
              SET @sql = CONCAT('ALTER TABLE ', prm_his_table, ' ADD PARTITION ( PARTITION ', var_partition_name, ' VALUES LESS THAN (', var_partition_description, ') )');
              SELECT @sql;
              PREPARE stmt FROM @sql;
              EXECUTE stmt;
              DEALLOCATE PREPARE stmt;
            END IF;
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
          END IF;
          -- (4)删除业务表分区
          SET @sql = CONCAT('ALTER TABLE ', prm_biz_table, ' DROP PARTITION ', var_partition_name);
          PREPARE stmt FROM @sql;
          EXECUTE stmt;
          DEALLOCATE PREPARE stmt;
      END IF;
    END WHILE;
  CLOSE cur_arg;

END $$