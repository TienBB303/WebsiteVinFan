package com.example.vinfan.mapper;

import com.example.vinfan.dto.HoaDonDTO;
import com.example.vinfan.entity.HoaDon;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component

public interface HoaDonMapper extends BaseMapper<HoaDonDTO, HoaDon>{

}
