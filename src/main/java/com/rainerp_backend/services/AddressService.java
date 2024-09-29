package com.rainerp_backend.services;

import com.rainerp_backend.models.Address;
import com.rainerp_backend.repositories.AddressRepository;
import com.rainerp_backend.exceptions.ResourceNotFoundException;
import com.rainerp_backend.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public Page<Address> getAllAddresses(Pageable pageable, String searchTerm) {
        try {
            Page<Address> addresses;

            // Verifica se o termo de busca está vazio ou nulo
            if (searchTerm == null || searchTerm.isEmpty()) {
                // Sem termo de busca: busca todos os endereços paginados
                addresses = addressRepository.findAll(pageable);
            } else {
                // Com termo de busca: busca filtrando pelos campos do endereço
                addresses = addressRepository.findBySearchTerm(searchTerm.toLowerCase(), pageable);
            }

            // Conta o número total de endereços para cálculo de páginas
            long totalAddresses = addresses.getTotalElements();
            int totalPages = (int) Math.ceil((double) totalAddresses / pageable.getPageSize());

            // Se a página solicitada for maior que o total de páginas, ajusta para a última página válida
            if (pageable.getPageNumber() >= totalPages && totalPages > 0) {
                pageable = PageRequest.of(totalPages - 1, pageable.getPageSize());
                // Busca novamente com a página ajustada
                if (searchTerm == null || searchTerm.isEmpty()) {
                    addresses = addressRepository.findAll(pageable);
                } else {
                    addresses = addressRepository.findBySearchTerm(searchTerm.toLowerCase(), pageable);
                }
            }

            return addresses;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar todos os endereços.", e);
        }
    }

    @Transactional(readOnly = true)
    public Address getAddressById(Long id) {
        try {
            Optional<Address> address = addressRepository.findById(id);
            if (address.isPresent()) {
                return address.get();
            } else {
                throw new ResourceNotFoundException("Endereço", id);
            }
        } catch (ResourceNotFoundException e) {
            throw e; // Relança a exceção customizada
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar endereço com ID " + id, e);
        }
    }

    @Transactional
    public Address createAddress(Address address) {
        try {
            return addressRepository.save(address);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar endereço.", e);
        }
    }

    @Transactional
    public Address updateAddress(Long id, Address updatedAddress) {
        try {
            Address existingAddress = getAddressById(id); // Garante que o endereço existe
            existingAddress.setStreet(updatedAddress.getStreet());
            existingAddress.setCity(updatedAddress.getCity());
            existingAddress.setState(updatedAddress.getState());
            existingAddress.setZipCode(updatedAddress.getZipCode());
            existingAddress.setCountry(updatedAddress.getCountry());
            return addressRepository.save(existingAddress);
        } catch (ResourceNotFoundException e) {
            throw e; // Relança a exceção customizada
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar endereço com ID " + id, e);
        }
    }

    @Transactional
    public void deleteAddress(Long id) {
        try {
            Address address = getAddressById(id); // Garante que o endereço existe

            // Verifique se há clientes associados a este endereço
            if (clientRepository.countByAddressId(id) > 0) {
                throw new RuntimeException("Não é possível deletar o endereço, pois está vinculado a clientes.");
            }
            addressRepository.delete(address);
        } catch (ResourceNotFoundException e) {
            throw e; // Relança a exceção customizada
        }
    }

}

