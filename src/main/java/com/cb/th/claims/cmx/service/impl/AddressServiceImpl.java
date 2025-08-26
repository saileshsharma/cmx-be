package com.cb.th.claims.cmx.service.impl;


import com.cb.th.claims.cmx.entity.address.Address;
import com.cb.th.claims.cmx.repository.address.AddressRepository;
import com.cb.th.claims.cmx.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    @Override
    public Address save(Address entity) {
        return addressRepository.save(entity);
    }
    @Override
    public Address findById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + id));
    }
    @Override
    public List<Address> findAll() {
        return addressRepository.findAll();
    }
    @Override
    public void delete(Long id) {
        addressRepository.deleteById(id);
    }
}
