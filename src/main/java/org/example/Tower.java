package org.example;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NamedQuery(name="findTowerByName", query = "SELECT t FROM Tower t WHERE t.name = :name")
@NamedQuery(name="deleteTower", query="DELETE FROM Tower t WHERE t.name = :name")
@Getter
@RequiredArgsConstructor
@Entity
public class Tower {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition="UUID")
    UUID id;

    @Setter
    @Column(unique = true)
    private String name;

    @Setter
    private int height;

    @Setter
    @OneToMany(mappedBy = "tower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mage> mages;

    public Tower(String name, int height) {
        this.name = name;
        this.height = height;
        this.mages = new ArrayList<>();
    }

    public void addMage(Mage mage){
        mages.add(mage);
    }
    public void removeMage(Mage mage) {mages.remove(mage);}

    @Override
    public String toString() {
        return "Tower{" +
                "name='" + name + '\'' +
                ", height=" + height +
                ", mages=" + mages +
                '}';
    }
}
