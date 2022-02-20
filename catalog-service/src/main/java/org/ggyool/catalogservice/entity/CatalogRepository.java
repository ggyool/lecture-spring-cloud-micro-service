package org.ggyool.catalogservice.entity;


import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CatalogRepository extends CrudRepository<CatalogEntity, Long> {
    Optional<CatalogEntity> findByProductId(String productId);
}
