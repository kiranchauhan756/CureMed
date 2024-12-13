package authn_server.service;


import authn_server.controller.client.ClientRequest;
import authn_server.controller.client.ClientResponse;
import authn_server.converter.Converter;
import authn_server.entity.Client;
import authn_server.exception.ClientAlreadyExistException;
import authn_server.exception.NoSuchClientExistException;
import authn_server.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/* This class defines business logic means what actions we can perform with client entity */
@Service
public class ClientService {


    private final ClientRepository clientRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final Converter converter;

    public ClientService(ClientRepository clientRepository, PasswordEncoder bCryptPasswordEncoder, Converter converter) {
        this.clientRepository = clientRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.converter = converter;
    }


    public ClientResponse add(ClientRequest clientRequest) {
        Optional<Client> client1 = this.clientRepository.findByUsername(clientRequest.getUsername());

        if (client1.isEmpty()) {
            Client client = (Client) converter.convert(clientRequest, new Client());
            client.setPassword(bCryptPasswordEncoder.encode(client.getPassword()));
            Client savedClient = clientRepository.save(client);
            return (ClientResponse) converter.convert(savedClient, new ClientResponse());
        } else
            throw new ClientAlreadyExistException("Client Already Exist!!");
    }


    public List<ClientResponse> getClientList() {
        Optional<List<Client>> clients = Optional.of(clientRepository.findAll());
        if (!clients.get().isEmpty()) {
            return clients.get().stream().map(client -> (ClientResponse) converter.convert(client, new ClientResponse())).collect(Collectors.toList());

        } else
            throw new NoSuchClientExistException("No Client is available currently");
    }


    public Client findByUsername(String username) {
        Optional<Client> clientOptional = clientRepository.findByUsername(username);
        if (clientOptional.isPresent()) {
            return clientOptional.get();
        } else {
            throw new NoSuchClientExistException("Client with this username does not exist!!");
        }
    }

    public ClientResponse updateClient(String username, ClientRequest clientRequest) {
        Optional<Client> optionalClient = clientRepository.findByUsername(username);
        if (optionalClient.isPresent()) {
            Client client1 = optionalClient.get();
            client1.setUsername(clientRequest.getUsername());
            client1.setPassword(bCryptPasswordEncoder.encode(clientRequest.getPassword()));
            Client savedClient = clientRepository.save(client1);
            return (ClientResponse) converter.convert(savedClient, new ClientResponse());
        } else
            throw new NoSuchClientExistException("Client with this username does not exist!!");
    }

    public void deleteClient(String username) {
        Optional<Client> optionalClient = clientRepository.findByUsername((username));
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            clientRepository.delete(client);
        } else
            throw new NoSuchClientExistException("Client with this username does not exist!!");
    }
}
