package ec.edu.espe.e_comerce_core.repository;


import ec.edu.espe.e_comerce_core.model.ProductStock;
import org.hibernate.validator.cfg.defs.UUIDDef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, UUID> {

}