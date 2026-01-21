package ec.edu.espe.e_comerce_core.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "products_stock")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStock {

    @Id
    @UuidGenerator
    @Column(name = "productId", columnDefinition = "uuid DEFAULT gen_random_uuid()")
    private UUID productId;

    private Integer availableStock;
    private Integer reservedStock; // Para llevar control de lo reservado
}