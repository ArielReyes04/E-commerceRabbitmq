package ec.edu.espe.OrderService.repositories;

import ec.edu.espe.OrderService.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Buscar todos los ítems asociados a un pedido específico
    List<OrderItem> findByOrderOrderId(UUID orderId);

    // Consulta JPQL: Contar cuántas unidades se han vendido de un producto específico en total
    // (Útil para reportes, aunque no sea requisito estricto del PDF)
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.productId = :productId")
    Integer countTotalUnitsSoldByProduct(@Param("productId") UUID productId);
}