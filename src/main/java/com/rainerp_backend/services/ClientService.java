package com.rainerp_backend.services;

import com.rainerp_backend.models.Client;
import com.rainerp_backend.repositories.ClientRepository;
import com.rainerp_backend.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public Page<Client> getAllClients(Pageable pageable, String searchTerm) {
        try {
            Page<Client> clients;

            // Verifica se o termo de busca está vazio ou nulo
            if (searchTerm == null || searchTerm.isEmpty()) {
                // Sem termo de busca: busca todos os clientes paginados
                clients = clientRepository.findAll(pageable);
            } else {
                // Com termo de busca: busca filtrando por nome, email ou telefone
                clients = clientRepository.findBySearchTerm(searchTerm.toLowerCase(), pageable);
            }

            // Conta o número total de clientes para cálculo de páginas
            long totalClients = clients.getTotalElements();
            int totalPages = (int) Math.ceil((double) totalClients / pageable.getPageSize());

            // Se a página solicitada for maior que o total de páginas, ajusta para a última página válida
            if (pageable.getPageNumber() >= totalPages && totalPages > 0) {
                pageable = PageRequest.of(totalPages - 1, pageable.getPageSize());
                // Busca novamente com a página ajustada
                if (searchTerm == null || searchTerm.isEmpty()) {
                    clients = clientRepository.findAll(pageable);
                } else {
                    clients = clientRepository.findBySearchTerm(searchTerm.toLowerCase(), pageable);
                }
            }
            return clients;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar todos os clientes.", e);
        }
    }

    @Transactional(readOnly = true)
    public Client getClientById(Long id) {
        try {
            Optional<Client> client = clientRepository.findById(id);
            if (client.isPresent()) {
                return client.get();
            } else {
                throw new ResourceNotFoundException("Cliente", id);
            }
        } catch (ResourceNotFoundException e) {
            throw e; // Relança a exceção customizada
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar cliente com ID " + id, e);
        }
    }

    @Transactional
    public Client createClient(Client client) {
        try {
            return clientRepository.save(client);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar cliente.", e);
        }
    }

    @Transactional
    public Client updateClient(Long id, Client updatedClient) {
        try {
            Client existingClient = getClientById(id); // Busca o cliente para garantir que ele existe
            existingClient.setName(updatedClient.getName());
            existingClient.setEmail(updatedClient.getEmail());
            existingClient.setPhone(updatedClient.getPhone());
            existingClient.setAvatar(updatedClient.getAvatar());
            existingClient.setAddress(updatedClient.getAddress());
            return clientRepository.save(existingClient);
        } catch (ResourceNotFoundException e) {
            throw e; // Relança a exceção customizada
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar cliente com ID " + id, e);
        }
    }

    @Transactional
    public void deleteClient(Long id) {
        try {
            Client client = getClientById(id); // Garante que o cliente existe
            clientRepository.delete(client);
        } catch (ResourceNotFoundException e) {
            throw e; // Relança a exceção customizada
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar cliente com ID " + id, e);
        }
    }
}
