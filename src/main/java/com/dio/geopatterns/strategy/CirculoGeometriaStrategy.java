package com.dio.geopatterns.strategy;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Component
public class CirculoGeometriaStrategy implements GeometriaStrategy {

    private static final double RAIO_TERRA_METROS = 6_371_000.0;
    private static final int NUMERO_PONTOS = 64;

    @Override
    public Map<String, Object> gerarGeometria(Double latitude, Double longitude, Double raio) {

        List<List<Double>> coordenadas = new ArrayList<>();

        double latitudeRad = Math.toRadians(latitude);
        double longitudeRad = Math.toRadians(longitude);
        double distanciaAngular = raio / RAIO_TERRA_METROS;

        for (int i = 0; i < NUMERO_PONTOS; i++) {

            double angulo = 2 * Math.PI * i / NUMERO_PONTOS;

            double latitudePonto = Math.asin(
                    Math.sin(latitudeRad) * Math.cos(distanciaAngular)
                            + Math.cos(latitudeRad)
                            * Math.sin(distanciaAngular)
                            * Math.cos(angulo)
            );

            double longitudePonto = longitudeRad + Math.atan2(
                    Math.sin(angulo)
                            * Math.sin(distanciaAngular)
                            * Math.cos(latitudeRad),

                    Math.cos(distanciaAngular)
                            - Math.sin(latitudeRad)
                            * Math.sin(latitudePonto)
            );

            coordenadas.add(List.of(
                    Math.toDegrees(longitudePonto),
                    Math.toDegrees(latitudePonto)
            ));
        }

        coordenadas.add(coordenadas.getFirst());

        Map<String, Object> geometry = new LinkedHashMap<>();
        geometry.put("type", "Polygon");
        geometry.put("coordinates", List.of(coordenadas));

        return geometry;
    }

}
