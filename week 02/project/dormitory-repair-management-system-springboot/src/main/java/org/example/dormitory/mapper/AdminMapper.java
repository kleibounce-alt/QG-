package org.example.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.dormitory.entity.Admin;

/**
 * @author klei
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}