package com.yonghui.springbootmybatisgenerator.base;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @Author:lyh
 * @Description:
 * @Date:Created in 2020/8/24 7:30
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
