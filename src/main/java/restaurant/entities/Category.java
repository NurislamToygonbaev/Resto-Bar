package restaurant.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;

@Entity
@Table(name = "categories")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_seq")
    @SequenceGenerator(name = "categories_seq", allocationSize = 1)
    private Long id;
    private String name;

    @OneToMany(cascade = {REMOVE}, mappedBy = "category")
    private List<SubCategory> subCategories;

    @ManyToOne(cascade = {DETACH})
    private Restaurant restaurant;

    public void addSubCategory(SubCategory subCategory){
        if (this.subCategories == null) this.subCategories = new ArrayList<>();
        this.subCategories.add(subCategory);
    }
}
