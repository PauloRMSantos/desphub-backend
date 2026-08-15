package Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;


@Entity
public class Vehicle {
    @Id
    protected long id;
    @Column
    public String plate;
    @Column
    public String brand;
    @Column
    public String model;
    @Column
    public String fabricationAndModel;
    @Column
    public String color;
    @Column
    public String renavam;
    @Column
    public String chassis;
    @Column
    @ForeignKey
    public long clientId;
}
