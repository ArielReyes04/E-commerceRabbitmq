package ec.edu.espe.e_comerce_core.repository;


import ec.edu.espe.e_comerce_core.model.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, String> {
}