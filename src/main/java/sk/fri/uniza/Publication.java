package sk.fri.uniza;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nazov;
    @ManyToMany(mappedBy = "publications" , cascade = CascadeType.ALL) // Nazov premennej v naprotivnom objekte
    private Set<Author> authors = new HashSet<Author>();

    public Publication(String nazov, Set<Author> authors) {
        this.nazov = nazov;
        this.authors = authors;
    }
    public Publication() {
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
