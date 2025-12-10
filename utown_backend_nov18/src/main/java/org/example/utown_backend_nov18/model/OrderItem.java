package org.example.utown_backend_nov18.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "order_items")
@Data
@EqualsAndHashCode(exclude = "order")
@ToString(exclude = "order")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id")
    private Dish dish;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer priceAtMoment;
}
