package sk.fri.uniza;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Person")
@Table(name = "persons")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    @Embedded
    private Adress adress;

    private int age;
    @OneToMany(mappedBy = "owner", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<PhoneNumber> phones = new ArrayList<>();

    public Person(String firstName, String lastName, List<PhoneNumber> phones) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phones = phones;
    }
    public Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public Person() {
    }

    public Adress getAdress() {
        return adress;
    }

    public void setAdress(Adress adress) {
        this.adress = adress;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<PhoneNumber> getPhones() {
        return phones;
    }

    public void setPhones(List<PhoneNumber> phones) {
        this.phones = phones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
