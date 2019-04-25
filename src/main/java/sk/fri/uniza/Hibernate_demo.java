package sk.fri.uniza;

import com.github.lalyos.jfiglet.FigletFont;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Hibernate_demo {

    public static void main(String[] args) {
        new Hibernate_demo().run();
    }

    public void run() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        SessionFactory buildSessionFactory = new MetadataSources(registry).addResource("hibernate.cfg.xml").buildMetadata().buildSessionFactory();


        // Generate new Authors
        List<Author> authors = Arrays.asList(
                new Author("Milan", "Rúfus", 80, null),     // index 0
                new Author("Ľubomír", "Feldek", 83, null),  // index 1
                new Author("Ján", "Smrek", 83, null),       // index 2
                new Author("Pavel", "Dvořák", 81, null),    // index 3
                new Author("Karol", "Kállay", 86, null),    // index 4
                new Author("Jakub", "Dvořák", 47, null)    // index 5
        );

        // Vytvorenie telefonych cisel a ich nasledne priradenie
        List<PhoneNumber> phoneNumbers = Arrays.asList(new PhoneNumber("441445", authors.get(3)),
                new PhoneNumber("1245", authors.get(3)));
        authors.get(3).setPhones(phoneNumbers);

        // Generate publication
        List<Publication> publications = Arrays.asList(
                new Publication("Chlapec", Set.<Author>of(authors.get(0))), //index 0
                new Publication("Kolíska", Set.<Author>of(authors.get(0))), // index 1
                new Publication("Kriedový kruh", Set.<Author>of(authors.get(1))), //index 2
                new Publication("Usmiaty otec", Set.<Author>of(authors.get(1))), //index 3
                new Publication("Zrno", Set.<Author>of(authors.get(2))), //index 4
                new Publication("Struny", Set.<Author>of(authors.get(2))), // index 5
                new Publication("Krvavá grófka", Set.<Author>of(authors.get(3), authors.get(4))), // index 6
                new Publication("Najstaršie dejiny Slovenska v reči obrazov", Set.<Author>of(authors.get(3), authors.get(5))) //index 7
        );

        authors.get(0).setPublications(Set.of(publications.get(0), publications.get(1)));
        authors.get(1).setPublications(Set.of(publications.get(2), publications.get(3)));
        authors.get(2).setPublications(Set.of(publications.get(4), publications.get(5)));
        authors.get(3).setPublications(Set.of(publications.get(6), publications.get(7)));
        authors.get(4).setPublications(Set.of(publications.get(6)));
        authors.get(5).setPublications(Set.of(publications.get(7)));

        {
            final Session session = buildSessionFactory.openSession();
            session.beginTransaction();

            // Ulozenie dat do databazy

        /*
            V pripade, ze pri definovani vztahu povolime "cascade = CascadeType.ALL" staci ulozit iba jeden objekt,
             nasledne sa ulozia aj vsetky objekty, ktore su vo vztahu.

             Napr. saveOrUpdate(publication) -> ulozi aj objekty "author", ktore su vo vztahu s publication

             v pripade, ze nepouzivame CascadeType.ALL(resp. iny variant napr. CascadeType.PERSIST) vsetky objekty treba ulozit manualne: session.saveOrUpdate(publication); session.saveOrUpdate(author));


         */
            publications.forEach(publication -> session.saveOrUpdate(publication));

            // Pouzivame CascadeType.ALL, nemusime manualne ukladat author objekt
            //        authors.forEach(author -> session.saveOrUpdate(author)); //

            session.flush();
            session.getTransaction().commit();

            session.close();
        }

        /***
         *    ██╗  ██╗ ██████╗ ██╗
         *    ██║  ██║██╔═══██╗██║
         *    ███████║██║   ██║██║
         *    ██╔══██║██║▄▄ ██║██║
         *    ██║  ██║╚██████╔╝███████╗
         *    ╚═╝  ╚═╝ ╚══▀▀═╝ ╚══════╝
         */
        try {
            String asciiArt1 = FigletFont.convertOneLine("HQL Query");
            System.out.println(asciiArt1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer i = 0;
        // Vytvorenie Query
        final Session session = buildSessionFactory.openSession();

        Query<Author> authorQuery = session.createQuery("FROM Author AS a  where a.age < :age", Author.class)
                .setParameter("age", 83);
        List<Author> authorList = authorQuery.getResultList();

        System.out.println(i++ + ":");
        System.out.format("%25s%25s%10s\n", "First Name", "Last Name", "Age");
        authorList.forEach(author -> System.out.format("%25s%25s%10s\n", author.getFirstName(), author.getLastName(), author.getAge()));

        // Vytvorenie Join + query nad tabulkov
        authorList = session.createQuery("SELECT author FROM Publication AS pub JOIN pub.authors as author where pub.nazov like :nazov", Author.class)
                .setParameter("nazov", "Chlapec")
                .getResultList();

        System.out.println(i++ + ":");
        System.out.format("%25s%25s%10s\n", "First Name", "Last Name", "");
        authorList.forEach(author -> System.out.format("%25s%25s\n", author.getFirstName(), author.getLastName()));

        // Create join session
        List<Object[]> findList = session.createQuery("SELECT author.firstName,author.lastName, pub.nazov FROM Publication AS pub JOIN pub.authors as author where pub.nazov like :nazov", Object[].class)
                .setParameter("nazov", "%grófka%")
                .getResultList();

        System.out.println(i++ + ":");
        System.out.format("%25s%25s%25s\n", "First Name", "Last Name", "Book");
        findList.forEach(author -> System.out.format("%25s%25s%25s\n", author[0], author[1], author[2]));


        // Vytvorenie Query, ktora najde publikacie obsahujuce v nazve hladany vyraz
        List<Publication> pubList = session.createQuery("FROM Publication AS p where p.nazov like :nazov", Publication.class)
                .setParameter("nazov", "%dejiny%")
                .getResultList();

        System.out.println(i++ + ":");
        System.out.format("%35s%45s\n", "Book", "Authors");
        pubList.forEach(pub -> System.out.format("%35s%45s\n", pub.getNazov(), pub.getAuthors().stream().map(author -> author.getFirstName() + " " + author.getLastName()).collect(Collectors.joining(", "))));

        /***
         *     ██████╗██████╗ ██╗████████╗███████╗██████╗ ██╗ █████╗      ██████╗ ██╗   ██╗███████╗██████╗ ██╗   ██╗
         *    ██╔════╝██╔══██╗██║╚══██╔══╝██╔════╝██╔══██╗██║██╔══██╗    ██╔═══██╗██║   ██║██╔════╝██╔══██╗╚██╗ ██╔╝
         *    ██║     ██████╔╝██║   ██║   █████╗  ██████╔╝██║███████║    ██║   ██║██║   ██║█████╗  ██████╔╝ ╚████╔╝
         *    ██║     ██╔══██╗██║   ██║   ██╔══╝  ██╔══██╗██║██╔══██║    ██║▄▄ ██║██║   ██║██╔══╝  ██╔══██╗  ╚██╔╝
         *    ╚██████╗██║  ██║██║   ██║   ███████╗██║  ██║██║██║  ██║    ╚██████╔╝╚██████╔╝███████╗██║  ██║   ██║
         *     ╚═════╝╚═╝  ╚═╝╚═╝   ╚═╝   ╚══════╝╚═╝  ╚═╝╚═╝╚═╝  ╚═╝     ╚══▀▀═╝  ╚═════╝ ╚══════╝╚═╝  ╚═╝   ╚═╝
         *
         *  https://www.baeldung.com/hibernate-criteria-queries
         */


        try {
            String asciiArt1 = FigletFont.convertOneLine("Criteria Query");
            System.out.println(asciiArt1);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Create criteria builder
        CriteriaBuilder cb = session.getCriteriaBuilder();

        {
            CriteriaQuery<Author> cq = cb.createQuery(Author.class);
            Root<Author> root = cq.from(Author.class);

            // Create criteria query ordered by Age
            cq.select(root)
                    .orderBy(cb.asc(root.get(Author_.AGE)));

            // Get standard query
            Query<Author> query = session.createQuery(cq);
            List<Author> resultList = query.getResultList();

            System.out.println((i = 0) + ":");
            System.out.format("%25s%25s%10s\n", "First Name", "Last Name", "Age");
            resultList.forEach(author -> System.out.format("%25s%25s%10s\n", author.getFirstName(), author.getLastName(), author.getAge()));

            // Filtrovanie podla veku autora
            int age = 83;
            query = session.createQuery(
                    cq.select(root)
                            .where(cb.lessThan(root.get(Author_.AGE), age))
                            .orderBy(cb.asc(root.get(Author_.FIRST_NAME)))
            );

            resultList = query.getResultList();

            System.out.println(++i + ":");
            System.out.format("%25s%25s%10s\n", "First Name", "Last Name", "Age");
            resultList.forEach(author -> System.out.format("%25s%25s%10s\n", author.getFirstName(), author.getLastName(), author.getAge()));

        }
        //Ziskaj pocet pocet prvkov v tabulke
        {

            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Author> root = cq.from(Author.class);

            cq.select(cb.count(root));
            Long count = session.createQuery(cq).uniqueResult();
            System.out.println(++i + ":");
            System.out.println("Count: " + count);
        }
        //Ziskaj priemerny vek autorov
        {
            CriteriaQuery<Double> cq = cb.createQuery(Double.class);
            Root<Author> root = cq.from(Author.class);

            cq.select(cb.avg(root.get(Author_.AGE)));
            Double count = session.createQuery(cq).uniqueResult();
            System.out.println(++i + ":");
            System.out.println("Priemerny vek autorov: " + count);

        }

        //CriteriaUpdate
        {
            CriteriaUpdate<Author> cu = cb.createCriteriaUpdate(Author.class);
            Root<Author> root = cu.from(Author.class);
            cu.set(Author_.age, 30); // Nastav novy vek
            cu.where(cb.equal(root.get(Author_.LAST_NAME), "Rúfus"));

            Transaction transaction = session.beginTransaction();
            int i1 = session.createQuery(cu).executeUpdate();
            transaction.commit();

        }

        //Delete: analogicky ku CriteriaUpdate existuje aj CriteriaDelete, ale v pripade navazujucich tabuliek(OneToMany na PhoneNumbers) nie je mozne vyuzit tento sposob
        {
            CriteriaQuery<Author> cq = cb.createQuery(Author.class);
            Root<Author> root = cq.from(Author.class);
            cq.select(root);
            cq.where(cb.equal(root.get(Author_.LAST_NAME), "Dvořák"));
            List<Author> authorList1 = session.createQuery(cq).getResultList();

            Transaction transaction = session.beginTransaction();
            authorList1.forEach(author -> session.delete(author));

            transaction.commit();


        }
        session.close();

        // Z dovodu zmeny dat v databaze bolo potrebne zavriet staru session a vytvorit novu
        final Session session2 = buildSessionFactory.openSession();
        // Print result
        {
            CriteriaBuilder cb2 = session2.getCriteriaBuilder();
            CriteriaQuery<Author> cq = cb2.createQuery(Author.class);
            Root<Author> root = cq.from(Author.class);

            // Create criteria query ordered by Age
            cq.select(root)
                    .orderBy(cb2.asc(root.get(Author_.AGE)));

            // Get standard query
            System.out.println(++i + ":");
            List<Author> resultList = session2.createQuery(cq).getResultList();
            System.out.format("%25s%25s%10s\n", "First Name", "Last Name", "Age");
            resultList.forEach(author -> System.out.format("%25s%25s%10s\n", author.getFirstName(), author.getLastName(), author.getAge()));
        }
        session2.close();


    }


}

