package org.ggyool.catalogservice.controller;

import org.ggyool.catalogservice.entity.CatalogEntity;
import org.ggyool.catalogservice.service.CatalogService;
import org.ggyool.catalogservice.vo.ResponseCatalog;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/catalog-service")
public class CatalogController {

    private final CatalogService catalogService;
    private final ModelMapper modelMapper;

    public CatalogController(CatalogService catalogService, ModelMapper modelMapper) {
        this.catalogService = catalogService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getCatalogs() {
        Iterable<CatalogEntity> catalogs = catalogService.getAllCatalogs();
        List<ResponseCatalog> responseCatalogs = StreamSupport.stream(catalogs.spliterator(), false)
                .map(catalogEntity -> modelMapper.map(catalogEntity, ResponseCatalog.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseCatalogs);
    }

    @GetMapping("/health-check")
    public String status(@Value("${local.server.port}") String port) {
        return String.format("It's Working in Catalog Service on Port %s", port);
    }
}
