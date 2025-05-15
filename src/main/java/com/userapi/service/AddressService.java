package com.userapi.service;

import com.userapi.dto.AddressDTO;
import com.userapi.entity.Address;
import com.userapi.entity.User;
import com.userapi.repository.AddressRepository;
import com.userapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication; // Adicionado
import org.springframework.security.core.context.SecurityContextHolder; // Adicionado
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.Optional;

@Service
@Validated
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public AddressDTO create(AddressDTO addressDTO) {
        // Obter o email do usuário autenticado do contexto de segurança
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Usuário não autenticado. Não é possível criar endereço.");
        }
        String userEmail = authentication.getName(); // getName() geralmente retorna o username (email neste caso)

        // Carregar o usuário do repositório
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado no banco de dados: " + userEmail));

        validateCep(addressDTO.getCep());

        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user); // Associar o usuário carregado ao novo endereço
        // O ID do AddressDTO (addressDTO.getId()) deve ser nulo aqui, pois é uma criação.
        // Se o modelMapper estiver tentando definir um ID no Address a partir de um ID nulo no DTO, isso pode ser um problema.
        // Garantir que o mapeamento de DTO para Address não tente definir o ID do Address se o ID do DTO for nulo.
        // Normalmente, ModelMapper lida bem com isso para IDs não definidos.
        address.setId(null); // Garante que estamos criando um novo endereço, o ID será gerado pelo banco

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    public Page<AddressDTO> findAll(Pageable pageable) {
        return addressRepository.findAll(pageable)
                .map(address -> modelMapper.map(address, AddressDTO.class));
    }

    public Optional<AddressDTO> findById(Long id) {
        return addressRepository.findById(id)
                .map(address -> modelMapper.map(address, AddressDTO.class));
    }

    public AddressDTO update(Long id, AddressDTO addressDTO) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        validateCep(addressDTO.getCep());

        address.setLogradouro(addressDTO.getLogradouro());
        address.setNumero(addressDTO.getNumero());
        address.setComplemento(addressDTO.getComplemento());
        address.setBairro(addressDTO.getBairro());
        address.setCidade(addressDTO.getCidade());
        address.setEstado(addressDTO.getEstado());
        address.setCep(addressDTO.getCep());

        return modelMapper.map(addressRepository.save(address), AddressDTO.class);
    }

    public void delete(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new RuntimeException("Endereço não encontrado");
        }
        addressRepository.deleteById(id);
    }

    private void validateCep(String cep) {
        if (cep == null || !cep.matches("\\d{8}|\\d{5}-\\d{3}")) {
            throw new RuntimeException("CEP inválido");
        }
    }
}
