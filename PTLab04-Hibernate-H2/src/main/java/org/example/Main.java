package org.example;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PTLab04");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Tower tower = new Tower("Khorinis", 150);
        Tower tower1 = new Tower("Onar", 251);
        Tower tower2 = new Tower("Old mine", 90);
        Tower tower3 = new Tower("New mine", 43);
        Tower tower4 = new Tower("Old camp", 51);

        Mage mage = new Mage("Xardas", 125, tower);
        Mage mage1 = new Mage("Saturas", 285, tower);
        Mage mage2 = new Mage("Santiago", 243, tower1);
        Mage mage3 = new Mage("Milten", 123, tower2);
        Mage mage4 = new Mage("Drago", 45, tower2);
        Mage mage5 = new Mage("Nefarius", 512, tower3);
        tx.begin();
        em.persist(tower);
        em.persist(tower1);
        em.persist(tower2);
        em.persist(tower3);
        em.persist(tower4);

        em.persist(mage);
        em.persist(mage1);
        em.persist(mage2);
        em.persist(mage3);
        em.persist(mage4);
        em.persist(mage5);

        tx.commit();
        String command = "";
        Scanner scanner = new Scanner(System.in);
        while (!command.equals("END")) {
            System.out.println("Write query");
            command = scanner.nextLine().toUpperCase();
            switch (command) {
                case "PRINTTOWERS" -> printTowers(em);
                case "PRINTMAGES" -> printMages(em);
                case "REMOVE" -> {
                    command = scanner.nextLine().toUpperCase();
                    switch (command) {
                        case "MAGE" -> {
                            System.out.println("Mage name: ");
                            command = scanner.nextLine();
                            try {
                                deleteMage(em, command);
                            } catch (NoResultException e) {
                                System.out.println("Incorrect mage name");
                                em.getTransaction().rollback();
                            }
                        }
                        case "TOWER" -> {
                            System.out.println("Tower name: ");
                            command = scanner.nextLine();
                            try {
                                deleteTower(em, command);
                            } catch (NoResultException e) {
                                System.out.println("Incorrect tower name");
                                em.getTransaction().rollback();
                            }
                        }
                    }
                }
                case "APPEND" -> {
                    command = scanner.nextLine().toUpperCase();
                    switch (command) {
                        case "MAGE" -> appendMage(em, scanner);
                        case "TOWER" -> appendTower(em, scanner);
                    }
                }
                case "CUSTOM" -> customQuery(em, scanner);
                case "END" -> {}
                default -> System.out.println("Incorrect query");
            }
        }

        em.close();
        emf.close();
    }

    private static void printTowers(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        String queryString = "SELECT p from Tower p";
        Query query = em.createQuery(queryString);
        List<Tower> towers = query.getResultList();
        for (Tower tower : towers) {
            System.out.println(tower);
        }
        tx.commit();

    }

    private static void printMages(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        List<Mage> mages = em.createNamedQuery("findAllMages", Mage.class).getResultList();
        for (Mage mage : mages) {
            System.out.println(mage);
        }
        tx.commit();

    }

    private static void deleteMage(EntityManager em, String name) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Mage mage = (Mage) em.createNamedQuery("findMageByName").setParameter("name", name).getSingleResult();
        if (mage != null) {
            mage.getTower().removeMage(mage);
            em.remove(em.merge(mage));
        }
        tx.commit();
    }

    private static void deleteTower(EntityManager em, String name) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Tower tower = (Tower) em.createNamedQuery("findTowerByName").setParameter("name", name).getSingleResult();
        em.remove(tower);
        tx.commit();
    }

    private static void appendTower(EntityManager em, Scanner scanner) {
        System.out.println("Tower name:");
        String tower_name = scanner.nextLine();
        System.out.println("Tower height:");
        int tower_height = scanner.nextInt();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(new Tower(tower_name, tower_height));
        try {
            tx.commit();
        } catch (RollbackException e) {
            if (e.getCause() instanceof PersistenceException) {
                System.out.println("Tower with this name already exists");
            } else {
                System.out.println("An error occurred while committing the transaction: " + e.getMessage());
            }
            tx.rollback();
        }
    }

    private static void appendMage(EntityManager em, Scanner scanner) {
        System.out.println("Mage name:");
        String mage_name = scanner.nextLine();
        System.out.println("Mage level:");
        int mage_level = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Tower name:");
        String tower_name = scanner.nextLine();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Tower tower;
        try {
            tower = (Tower) em.createNamedQuery("findTowerByName").setParameter("name", tower_name).getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Incorrect tower name");
            tx.rollback();
            return;
        }

        em.persist(new Mage(mage_name, mage_level, tower));
        try {
            tx.commit();
        } catch (RollbackException e) {
            if (e.getCause() instanceof PersistenceException) {
                System.out.println("Mage with this name already exists");
            } else {
                System.out.println("An error occurred while committing the transaction: " + e.getMessage());
            }
            tx.rollback();
        }

    }

    public static void customQuery(EntityManager em, Scanner scanner) {
        System.out.println("Enter your custom query:");
        String customQuery = scanner.nextLine();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Query query = em.createQuery(customQuery);
            List<?> result = query.getResultList();
            result.forEach(System.out::println);
            tx.commit();
        } catch (PersistenceException | IllegalArgumentException e) {
            System.out.println("An error occurred while executing the custom query: " + e.getMessage());
            tx.rollback();
        }
    }
}

//    SELECT m FROM Mage m WHERE m.level > 125 AND m.tower.name = 'Khorinis'
//    SELECT m FROM Tower m WHERE m.height < 91
//    SELECT m FROM Mage m WHERE m.level > 125
