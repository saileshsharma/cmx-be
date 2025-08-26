package com.cb.th.claims.cmx.service;


import com.cb.th.claims.cmx.entity.address.Address;

import java.util.List;

public interface AddressService {
    Address save(Address entity);
    Address findById(Long id);
    List<Address> findAll();
    void delete(Long id);
}
