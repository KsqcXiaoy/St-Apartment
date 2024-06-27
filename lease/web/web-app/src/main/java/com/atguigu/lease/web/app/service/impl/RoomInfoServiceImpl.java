package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.BrowsingHistoryService;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.attr.AttrValueVo;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.atguigu.lease.web.app.vo.room.RoomItemVo;
import com.atguigu.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private RoomLeaseTermMapper leaseTermMapper;

    @Autowired
    private RoomFacilityMapper facilityInfoMapper;

    @Autowired
    private RoomLabelMapper labelInfoMapper;

    @Autowired
    private RoomPaymentTypeMapper paymentTypeMapper;

    @Autowired
    private AttrValueMapper attrValueMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    @Autowired
    private BrowsingHistoryService browsingHistoryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public IPage<RoomItemVo> pageItem(Page<RoomItemVo> page, RoomQueryVo queryVo) {

        return roomInfoMapper.pageItem(page, queryVo);
    }

    @Override
    public IPage<RoomItemVo> pageItemByApartmentId(Page<RoomItemVo> page, Long id) {
        return roomInfoMapper.pageItemByApartmentId(page,id);
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        String key = RedisConstant.APP_ROOM_PREFIX;
        RoomDetailVo roomDetailVo = (RoomDetailVo)redisTemplate.opsForValue().get(key);

        if (roomDetailVo == null){
            RoomInfo roomInfo = roomInfoMapper.selectById(id);
            if (roomInfo == null){
                return null;
            }
            //1.2. 获取房间公寓信息
            ApartmentItemVo apartmentItemVo = apartmentInfoService.selectApartmentItemVoById(roomInfo.getApartmentId());
            //1.3. 获取房间图片信息
            List<GraphVo> graphVoList = graphInfoMapper.selectListByItemAndId(ItemType.ROOM,id);
            //1.4. 获取房间属性信息
            List<AttrValueVo> attrValueVoList = attrValueMapper.selectListByRoomId(id);
            //1.5. 获取配套设施信息
            List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByRoomId(id);
            //1.6. 获取标签信息
            List<LabelInfo> labelInfoList = labelInfoMapper.selectListByRoomId(id);
            //1.7. 获取支付方式信息
            List<PaymentType> paymentTypeList  = paymentTypeMapper.selectListByRoomId(id);
            //1.8.查询杂费信息
            List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(roomInfo.getApartmentId());
            //1.9. 获取可选租期信息
            List<LeaseTerm> leaseTermList = leaseTermMapper.selectListByRoomId(id);
            //2. 封装信息
            roomDetailVo = new RoomDetailVo();
            BeanUtils.copyProperties(roomInfo,roomDetailVo);
            roomDetailVo.setApartmentItemVo(apartmentItemVo);
            roomDetailVo.setFacilityInfoList(facilityInfoList);
            roomDetailVo.setLabelInfoList(labelInfoList);
            roomDetailVo.setGraphVoList(graphVoList);
            roomDetailVo.setLeaseTermList(leaseTermList);
            roomDetailVo.setAttrValueVoList(attrValueVoList);
            roomDetailVo.setFeeValueVoList(feeValueVoList);
            roomDetailVo.setPaymentTypeList(paymentTypeList);

            redisTemplate.opsForValue().set(key,roomDetailVo);
        }


        // 保存浏览历史
        browsingHistoryService.saveHistory(LoginUserHolder.getLoginUser().getUserId(),id);
        //3. 返回
        return roomDetailVo;
    }
}




