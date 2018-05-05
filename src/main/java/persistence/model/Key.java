package persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Tomas Perez Molina
 */

@Entity
@Table(name = "key")
public class Key {
    @Id
    @Column(name = "target_table")
    private String targetTable;

    @Column(name = "next_id", nullable = false)
    private int nextId;

    public Key(String targetTable) {
        this.targetTable = targetTable;
    }

    public Key() {
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
}
