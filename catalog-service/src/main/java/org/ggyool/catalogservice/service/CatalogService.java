package org.ggyool.catalogservice.service;

import org.ggyool.catalogservice.entity.CatalogEntity;

public interface CatalogService {
    Iterable<CatalogEntity> getAllCatalogs();
}
