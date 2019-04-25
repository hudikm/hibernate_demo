# Hibernate demo

Ukážkový príklad ako použiť Hibernate ORM na prácu s databázov.

## Požiadavky na spojenie s DB
-  URL: `localhost:3306`
- Typ databázy: `MariaDB`
-  Názov vyvorenej databázy: `hibernate`
-  Užívatel: `userDemo`
-  Heslo: `userDemo`

Štandardné nastavenie môžete zmeniť v súbore: `hibernate.cfg.xml`

## Popis:

Aplikácia demoštruje základné možnosti použitia Hibernate frameworku na vytvorenie dátového modelu ako napr. použitie `@ManyToMany, @OneToMany, @Embeddable, @Inheritance,@Entity`. Ďalej ilustuje základné možnosti použitia *HQL* jazyka a api *CriteriaQuery*.

### ER model (entitne vzťahový model) 

![](er_diagram.svg)

 



 


