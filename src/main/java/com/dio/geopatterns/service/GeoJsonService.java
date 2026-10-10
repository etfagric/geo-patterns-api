package com.dio.geopatterns.service;

import com.dio.geopatterns.strategy.GeometriaStrategy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GeoJsonService {

    private final GeometriaStrategy geometriaStrategy;

    public GeoJsonService(GeometriaStrategy geometriaStrategy) {
        this.geometriaStrategy = geometriaStrategy;
    }

    public Map<String, Object> gerarCirculo(Long pessoaId, Double latitude, Double longitude, Double raio) {

        // Etapa 1: Pede a geometria para a Strategy
        Map<String, Object> geometria = resolveGeometria(latitude, longitude, raio);

        // Etapa 2: Monta as duas features (cada método recebe o que precisa e devolve a feature pronta)
        Map<String, Object> featureCirculo = resolveFeatureCirculo(pessoaId, raio, geometria);
        Map<String, Object> featurePontoCentral = resolveFeaturePontoCentral(pessoaId, latitude, longitude);

        return renderizaJson(featureCirculo, featurePontoCentral);
    }

    private Map<String, Object> renderizaJson(Map<String, Object> featureCirculo, Map<String, Object> featurePontoCentral) {
            Map<String, Object> featureCollection = new LinkedHashMap<>();
            featureCollection.put("type", "FeatureCollection");
            featureCollection.put("features", List.of(featureCirculo, featurePontoCentral));
            return featureCollection;
    }

    private Map<String, Object> resolveFeatureCirculo(Long pessoaId, Double raio, Map<String, Object> geometria) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("pessoaId", pessoaId);
        properties.put("raioMetros", raio);
        Map<String, Object> feature = new LinkedHashMap<>();
        feature.put("type", "Feature");
        feature.put("geometry", geometria);
        feature.put("properties", properties);
        return feature;
    }

    private Map<String, Object> resolveFeaturePontoCentral(Long pessoaId, Double latitude, Double longitude) {
        Map<String, Object> geometry = new LinkedHashMap<>();
        geometry.put("type", "Point");
        geometry.put("coordinates", List.of(longitude, latitude));
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("pessoaId", pessoaId);
        properties.put("descricao", "Posição informada");
        Map<String, Object> feature = new LinkedHashMap<>();
        feature.put("type", "Feature");
        feature.put("geometry", geometry);
        feature.put("properties", properties);
        return feature;
    }


    public Map<String, Object> resolveGeometria (Double latitude, Double longitude, Double raio) {
        return geometriaStrategy.gerarGeometria(latitude, longitude, raio);
    }

}