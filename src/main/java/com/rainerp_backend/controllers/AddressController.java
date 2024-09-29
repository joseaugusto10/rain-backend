package com.rainerp_backend.controllers;

import com.rainerp_backend.models.Address;
import com.rainerp_backend.services.AddressService;
import com.rainerp_backend.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "http://localhost:3000") // Permite requisições de localhost:3000
public class AddressController {

    @Autowired
    private AddressService addressService;

    // Listar todos os endereços com paginação
    @GetMapping
    public ResponseEntity<Page<Address>> getAllAddresses(
            @RequestParam(required = false) String searchTerm,
            Pageable pageable) {

        try {
            // Chama o serviço para buscar os endereços com base no termo de pesquisa e paginação
            Page<Address> addresses = addressService.getAllAddresses(pageable, searchTerm);
            return new ResponseEntity<>(addresses, HttpStatus.OK);
        } catch (Exception e) {
            // Em caso de erro, retorna erro interno do servidor
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. Buscar endereço por ID
    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Long id) {
        try {
            Address address = addressService.getAddressById(id);
            return new ResponseEntity<>(address, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. Criar novo endereço
    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        try {
            Address createdAddress = addressService.createAddress(address);
            return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 4. Atualizar endereço por ID
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address updatedAddress) {
        try {
            Address address = addressService.updateAddress(id, updatedAddress);
            return new ResponseEntity<>(address, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. Deletar endereço por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("Endereço não encontrado.", HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            // Captura a exceção gerada na verificação de clientes associados
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 BAD REQUEST
        } catch (Exception e) {
            return new ResponseEntity<>("Erro interno do servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
