package Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Client {
    @Id
    public long id;
    @Column
    public String name;
    @Column
    public long telephone;
}
