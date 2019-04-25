package sk.fri.uniza;

import javax.persistence.*;

@Entity
@Table(name = "phone_numbers")
public class PhoneNumber {
    public PhoneNumber() {

    }

    public PhoneNumber(String number, Person owner) {
        this.number = number;
        this.owner = owner;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private String number;

    @ManyToOne(cascade = CascadeType.ALL)
    private Person owner;

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }
}
