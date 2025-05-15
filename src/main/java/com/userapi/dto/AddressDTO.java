package com.userapi.dto;

import jakarta.validation.constraints.NotBlank;

public class AddressDTO {
    private Long id;
    
    @NotBlank(message = "O logradouro é obrigatório")
    private String logradouro;
    
    @NotBlank(message = "O número é obrigatório")
    private String numero;
    
    private String complemento;
    
    @NotBlank(message = "O bairro é obrigatório")
    private String bairro;
    
    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;
    
    @NotBlank(message = "O estado é obrigatório")
    private String estado;
    
    @NotBlank(message = "O CEP é obrigatório")
    private String cep;
    
    private Long userId;

    public AddressDTO() {}

    public AddressDTO(Long id, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep, Long userId) {
        this.id = id;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
