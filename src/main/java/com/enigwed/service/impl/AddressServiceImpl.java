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
import com.enigwed.exception.ValidationException;
import com.enigwed.repository.DistrictRepository;
import com.enigwed.repository.ProvinceRepository;
import com.enigwed.repository.RegencyRepository;
import com.enigwed.service.AddressService;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {
    private final ProvinceRepository provinceRepository;
    private final RegencyRepository regencyRepository;
    private final DistrictRepository districtRepository;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Province saveOrLoadProvince(ProvinceRequest provinceRequest) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(provinceRequest);

            /* CHECK AND VALIDATE IF PROVINCE ALREADY EXIST */
            // DataIntegrityViolationException //
            Province existingProvince = provinceRepository.findById(provinceRequest.getId()).orElse(null);
            if (existingProvince != null && !existingProvince.getName().equals(provinceRequest.getName())) {
                throw new DataIntegrityViolationException(SErrorMessage.PROVINCE_ID_ALREADY_EXIST(existingProvince.getId(), existingProvince.getName()));
            } else if (existingProvince != null) {
                return existingProvince;
            }

            /* CREATE PROVINCE */
            Province province = Province.builder()
                    .id(provinceRequest.getId())
                    .name(provinceRequest.getName())
                    .build();

            /* SAVE PROVINCE */
            return provinceRepository.saveAndFlush(province);

        } catch (ValidationException e) {
            log.error("Validation error while creating province: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, e.getErrors().get(0));
        } catch (DataIntegrityViolationException e) {
            log.error("Database conflict error during creating province: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.CONFLICT, SMessage.CREATE_FAILED, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Regency saveOrLoadRegency(RegencyRequest regencyRequest) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(regencyRequest);

            /* LOAD PROVINCE */
            // ErrorResponse //
            Province province = provinceRepository.findById(regencyRequest.getProvince_id())
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.DATA_NOT_FOUND, SErrorMessage.PROVINCE_NOT_FOUND(regencyRequest.getProvince_id())));

            /* CHECK AND VALIDATE IF REGENCY ALREADY EXIST */
            // DataIntegrityViolationException //
            Regency existingRegency = regencyRepository.findById(regencyRequest.getId()).orElse(null);
            if (existingRegency != null && !existingRegency.getName().equals(regencyRequest.getName())) {
                throw new DataIntegrityViolationException(SErrorMessage.REGENCY_ID_ALREADY_EXIST(existingRegency.getId(), existingRegency.getName()));
            } else if (existingRegency != null) {
                return existingRegency;
            }

            /* CREATE REGENCY */
            Regency regency = Regency.builder()
                    .id(regencyRequest.getId())
                    .name(regencyRequest.getName())
                    .province(province)
                    .build();

            /* SAVE REGENCY */
            return regencyRepository.saveAndFlush(regency);

        } catch (ValidationException e) {
            log.error("Validation error while creating regency: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, e.getErrors().get(0));
        } catch (DataIntegrityViolationException e) {
            log.error("Database conflict error during creating regency: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.CONFLICT, SMessage.CREATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while creating regency: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public District saveOrLoadDistrict(DistrictRequest districtRequest) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(districtRequest);

            /* LOAD REGENCY */
            // ErrorResponse //
            Regency regency = regencyRepository.findById(districtRequest.getRegency_id())
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.DATA_NOT_FOUND, SErrorMessage.REGENCY_NOT_FOUND(districtRequest.getRegency_id())));

            /* CHECK AND VALIDATE IF DISTRICT ALREADY EXIST */
            // DataIntegrityViolationException //
            District existingDistrict = districtRepository.findById(districtRequest.getId()).orElse(null);
            if (existingDistrict != null && !existingDistrict.getName().equals(districtRequest.getName())) {
                throw new DataIntegrityViolationException(SErrorMessage.DISTRICT_ID_ALREADY_EXIST(existingDistrict.getId(), existingDistrict.getName()));
            } else if (existingDistrict != null) {
                return existingDistrict;
            }

            /* CREATE DISTRICT */
            District district = District.builder()
                    .id(districtRequest.getId())
                    .name(districtRequest.getName())
                    .regency(regency)
                    .build();

            /* SAVE DISTRICT */
            return districtRepository.saveAndFlush(district);

        } catch (ValidationException e) {
            log.error("Validation error while creating district: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, e.getErrors().get(0));
        } catch (DataIntegrityViolationException e) {
            log.error("Database conflict error during creating district: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.CONFLICT, SMessage.CREATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while creating district: {}", e.getMessage());
            throw e;
        }
    }
}
