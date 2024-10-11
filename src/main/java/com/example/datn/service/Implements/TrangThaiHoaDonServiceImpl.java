package com.example.datn.service.Implements;

import com.example.datn.dto.request.TrangThaiHoaDonRequest;
import com.example.datn.service.TrangThaiHoaDonService;
import org.springframework.stereotype.Service;

@Service
public class TrangThaiHoaDonServiceImpl implements TrangThaiHoaDonService {
    @Override
    public TrangThaiHoaDonRequest getTrangThaiHoaDonRequest() {
        TrangThaiHoaDonRequest request = new TrangThaiHoaDonRequest(0, 1, 2,
                3, 4, 5);
        return request;
    }
}
