package com.enigwed.service.impl;

import com.enigwed.constant.SErrorMessage;
import com.enigwed.constant.SMessage;
import com.enigwed.dto.request.DistrictRequest;
import com.enigwed.dto.request.ProvinceRequest;
import com.enigwed.dto.request.RegencyRequest;
import com.enigwed.entity.District;
import com.enigwed.entity.Province;
import com.enigwed.entity.Regency;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.DistrictRepository;
import com.enigwed.repository.ProvinceRepository;
import com.enigwed.repository.RegencyRepository;
import com.enigwed.service.AddressService;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final ProvinceRepository provinceRepository;
    private final RegencyRepository regencyRepository;
    private final DistrictRepository districtRepository;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Province saveOrLoadProvince(ProvinceRequest provinceRequest) {
        /* VALIDATE INPUT */
        validationUtil.validateAndThrow(provinceRequest);
        /* CREATE PROVINCE */
        Province province = Province.builder()
                .id(provinceRequest.getId())
                .name(provinceRequest.getName())
                .build();
        /* LOAD OR SAVE PROVINCE */
        return provinceRepository.findById(province.getId()).orElse(provinceRepository.saveAndFlush(province));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Regency saveOrLoadRegency(RegencyRequest regencyRequest) {
        /* VALIDATE INPUT */
        validationUtil.validateAndThrow(regencyRequest);
        /* LOAD PROVINCE */
        Province province = provinceRepository.findById(regencyRequest.getProvince_id())
                .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.DATA_NOT_FOUND, SErrorMessage.PROVINCE_NOT_FOUND(regencyRequest.getProvince_id())));
        /* CREATE REGENCY */
        Regency regency = Regency.builder()
                .id(regencyRequest.getId())
                .name(regencyRequest.getName())
                .province(province)
                .build();
        /* LOAD OR SAVE REGENCY */
        return regencyRepository.findById(regency.getId()).orElse(regencyRepository.saveAndFlush(regency));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public District saveOrLoadDistrict(DistrictRequest districtRequest) {
        /* VALIDATE INPUT */
        validationUtil.validateAndThrow(districtRequest);
        /* LOAD REGENCY */
        Regency regency = regencyRepository.findById(districtRequest.getRegency_id())
                .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.DATA_NOT_FOUND, SErrorMessage.REGENCY_NOT_FOUND(districtRequest.getRegency_id())));
        /* CREATE DISTRICT */
        District district = District.builder()
                .id(districtRequest.getId())
                .name(districtRequest.getName())
                .regency(regency)
                .build();
        /* LOAD OR SAVE DISTRICT */
        return districtRepository.findById(district.getId()).orElse(districtRepository.saveAndFlush(district));
    }
}
