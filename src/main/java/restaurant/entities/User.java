package restaurant.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import restaurant.entities.enums.Role;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", allocationSize = 1)
    private Long id;
    private String lastName;
    private String firstName;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Role role;
    private int experience;

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE}, mappedBy = "user")
    private List<Cheque> cheques;

    @ManyToOne(cascade = {CascadeType.DETACH})
    private Restaurant restaurant;

    public void addCheque(Cheque cheque){
        if (this.cheques == null) this.cheques = new ArrayList<>();
        this.cheques.add(cheque);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }
    @Override
    public String getPassword(){
        return password;
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
