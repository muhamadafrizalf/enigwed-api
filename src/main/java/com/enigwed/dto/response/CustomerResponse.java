package com.enigwed.dto.response;

import com.enigwed.entity.Customer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerResponse {
    private String name;
    private String phone;
    private String email;
    private String address;

    public static CustomerResponse all(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setName(customer.getName());
        response.setPhone(customer.getPhone());
        response.setEmail(customer.getEmail());
        response.setAddress(customer.getAddress());
        return response;
    }
}
