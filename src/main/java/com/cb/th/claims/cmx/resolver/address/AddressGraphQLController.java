// AddressGraphQLController.java
package com.cb.th.claims.cmx.resolver.address;

import com.cb.th.claims.cmx.service.address.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AddressGraphQLController {

    private final AddressService addressService;

    @MutationMapping
    public AddressService.AddressResponse createAddress(@Argument("input") AddressService.CreateAddressCommand input) {
        // The command is a record carrying all fields from CreateAddressInput
        return addressService.createAddress(input);
    }

}
