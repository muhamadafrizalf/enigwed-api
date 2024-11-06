package com.enigwed.service;

import com.enigwed.dto.request.DistrictRequest;
import com.enigwed.dto.request.ProvinceRequest;
import com.enigwed.dto.request.RegencyRequest;
import com.enigwed.entity.District;
import com.enigwed.entity.Province;
import com.enigwed.entity.Regency;

public interface AddressService {
    Province saveOrLoadProvince(ProvinceRequest provinceRequest);
    Regency saveOrLoadRegency(RegencyRequest regencyRequest);
    District saveOrLoadDistrict(DistrictRequest districtRequest);
}
