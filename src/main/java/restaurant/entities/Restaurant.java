package restaurant.entities;

import jakarta.persistence.*;
import lombok.*;
import restaurant.entities.enums.RestType;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurant_seq")
    @SequenceGenerator(name = "restaurant_seq", allocationSize = 1)
    private Long id;
    private String name;
    private String location;
    @Enumerated(EnumType.STRING)
    private RestType restType;
    private int numberOfEmployees;
    private String service;

    @OneToMany(cascade = {PERSIST, REMOVE, MERGE})
    private List<User> users;

    @OneToMany(cascade = {REMOVE, MERGE})
    private List<MenuItem> menuItems;

    public void addUser(User user){
        if (this.users == null) this.users = new ArrayList<>();
        this.users.add(user);
    }

    public void addMenuItem(MenuItem menuItem){
        if (this.menuItems == null) this.menuItems = new ArrayList<>();
        this.menuItems.add(menuItem);
    }
}
