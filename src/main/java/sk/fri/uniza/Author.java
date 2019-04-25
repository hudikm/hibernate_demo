package sk.fri.uniza;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "Author")
public class Author extends Person {

    @ManyToMany(cascade = CascadeType.ALL) // Ulozenie dat zo vsetkych objektov, ktore su vo vztahu
    @JoinTable(
            name = "publicationsAuthors",
            joinColumns = {@JoinColumn(name = "author_id")},
            inverseJoinColumns = {@JoinColumn(name = "publication_id")}
    )
    private Set<Publication> publications = new HashSet<Publication>();

    public Author(String firstName, String lastName, List<PhoneNumber> phones, Set<Publication> publications) {
        super(firstName, lastName, phones);
        this.publications = publications;
    }

    public Author(String firstName, String lastName, int age, Set<Publication> publications) {
        super(firstName, lastName, age);
        this.publications = publications;
    }

    public Author() {
    }

    public Set<Publication> getPublications() {
        return publications;
    }

    public void setPublications(Set<Publication> publications) {
        this.publications = publications;
    }
}
