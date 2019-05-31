package lk.ijse.pos.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="`Order`")
public class Order extends SuperEntity{

    @Id
    private int id;
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name="customerId",referencedColumnName = "id")
    private Customer customer;
    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public Order() {
    }

    public Order(int id, LocalDate date, Customer customer) {
        this.id = id;
        this.date = date;
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date=" + date +
                ", customer='" + customer + '\'' +
                '}';
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

}
