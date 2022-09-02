package org.sid.entities;


import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product extends PanacheEntityBase {
    @Id
    public String id;
    public String name;
    public double price;
    public int quantity;
}
