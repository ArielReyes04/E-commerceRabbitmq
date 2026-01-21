package ec.edu.espe.OrderService.repositories;

import ec.edu.espe.OrderService.models.Order;
import ec.edu.espe.OrderService.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Búsqueda por referencia de pago (para validaciones de unicidad)
    Optional<Order> findByPaymentReference(String paymentReference);

    // Validaciones booleanas rápidas
    boolean existsByPaymentReference(String paymentReference);
    boolean existsByOrderId(UUID orderId);

    // Búsqueda de todas las órdenes de un cliente específico
    List<Order> findByCustomerId(UUID customerId);

    // Consulta JPQL personalizada: Buscar órdenes por estado
    // Útil para encontrar todas las que están "PENDING" o "CONFIRMED"
    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findByStatus(@Param("status") OrderStatus status);

    // Consulta JPQL personalizada: Buscar órdenes de un cliente por estado
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.status = :status")
    List<Order> findByCustomerIdAndStatus(@Param("customerId") UUID customerId,
                                          @Param("status") OrderStatus status);

    // Consulta JPQL con búsqueda por coincidencia parcial en la referencia de pago
    @Query("SELECT o FROM Order o WHERE LOWER(o.paymentReference) LIKE LOWER(concat('%', :ref, '%'))")
    List<Order> findByPaymentReferenceContainingIgnoreCase(@Param("ref") String ref);
}