package mate.academy.booking.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "addresses")
@SQLDelete(sql = "UPDATE addresses SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String number;
    @Column(nullable = false)
    private String postcode;
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Accommodation> availableAccommodations = new HashSet<>();
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
