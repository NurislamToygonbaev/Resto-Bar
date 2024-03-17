package restaurant.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;

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
    private int quantity;

    @ManyToOne(cascade = {DETACH})
    private Restaurant restaurant;

    @ManyToMany(cascade = {DETACH}, mappedBy = "menuItems")
    private List<Cheque> cheques;

    @OneToOne(mappedBy = "menuItem", cascade = {REMOVE, PERSIST, MERGE})
    private StopList stopList;

    @ManyToOne(cascade = {DETACH})
    private SubCategory subCategory;

    public void addCheque(Cheque cheque){
        if (this.cheques == null) this.cheques = new ArrayList<>();
        this.cheques.add(cheque);
    }
}
