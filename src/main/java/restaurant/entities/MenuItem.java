package restaurant.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "menuItems")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_seq")
    @SequenceGenerator(name = "items_seq", allocationSize = 1)
    private Long id;
    private String name;
    @Column(length = 2000)
    private String image;
    private BigDecimal price;
    @Column(length = 1000)
    private String description;
    private boolean isVegetarian;


}
