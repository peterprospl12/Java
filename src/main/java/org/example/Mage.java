package org.example;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@NamedQuery(name="findAllMages", query="SELECT m FROM Mage m")
@NamedQuery(name="findMageByName", query = "SELECT m FROM Mage m WHERE m.name = :name")
@NamedQuery(name="deleteMage", query="DELETE FROM Mage m WHERE m.name = :name")
@Getter
@RequiredArgsConstructor
@Entity
public class Mage {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition="UUID")
    UUID id;

    @Setter
    @Column(unique = true)
    private String name;

    @Setter
    private int level;

    @Setter
    @ManyToOne
    @JoinColumn(name = "tower_id")
    private Tower tower;

    public Mage(String name, int level, Tower tower) {
        this.name = name;
        this.level = level;
        this.tower = tower;
        this.tower.addMage(this);
    }

    @Override
    public String toString() {
        return "Mage{" +
                "name='" + name + '\'' +
                ", level=" + level +
                ", tower=" + tower.getName()+
                '}';
    }
}
