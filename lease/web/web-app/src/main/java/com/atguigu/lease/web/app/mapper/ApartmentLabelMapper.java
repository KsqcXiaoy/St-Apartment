package com.atguigu.lease.web.app.mapper;

import com.atguigu.lease.model.entity.ApartmentLabel;
import com.atguigu.lease.model.entity.LabelInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【apartment_label(公寓标签关联表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.atguigu.lease.model.entity.ApartmentLabel
*/
public interface ApartmentLabelMapper extends BaseMapper<ApartmentLabel> {

    List<LabelInfo> selectListByApartmentId(Long apartmentId);

}




