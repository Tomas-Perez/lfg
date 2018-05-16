package persistence.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "KEY", schema = "PUBLIC", catalog = "PUBLIC")
public class KeyEntity {
    @Id
    @Column(name = "target_table")
    private String targetTable;

    @Column(name = "next_id", nullable = false)
    private int nextId;

    public KeyEntity(String targetTable) {
        this.targetTable = targetTable;
    }

    public KeyEntity() {
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String name) {
        this.targetTable = name;
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int id) {
        this.nextId = id;
    }

    public int getAndIncrement(){
        return nextId++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyEntity keyEntity = (KeyEntity) o;
        return nextId == keyEntity.nextId &&
                Objects.equals(targetTable, keyEntity.targetTable);
    }

    @Override
    public int hashCode() {

        return Objects.hash(targetTable, nextId);
    }
}