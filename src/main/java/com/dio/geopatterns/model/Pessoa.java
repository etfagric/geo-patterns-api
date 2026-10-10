package com.dio.geopatterns.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Pessoa {

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String nome;

    private Double latitude;

    private Double longitude;

    private Double raio;

    public Pessoa() {

    }

    public Pessoa(Long id, String nome, Double latitude, Double longitude, Double raio) {
        this.id = id;
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.raio = raio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRaio() {
        return raio;
    }

    public void setRaio(Double raio) {
        this.raio = raio;
    }
}
