package com.dio.geopatterns.strategy;

import java.util.Map;

public interface GeometriaStrategy {
    Map<String, Object> gerarGeometria(Double latitude, Double longitude, Double raio);
}
