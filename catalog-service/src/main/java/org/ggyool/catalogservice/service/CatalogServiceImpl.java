package org.ggyool.catalogservice.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ggyool.catalogservice.entity.CatalogEntity;
import org.ggyool.catalogservice.entity.CatalogRepository;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
public class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository catalogRepository;

    public CatalogServiceImpl(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Override
    public Iterable<CatalogEntity> getAllCatalogs() {
        return catalogRepository.findAll();
    }
}
