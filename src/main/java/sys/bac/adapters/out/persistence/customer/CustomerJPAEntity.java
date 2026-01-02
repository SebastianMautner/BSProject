package sys.bac.adapters.out.persistence.customer;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import sys.bac.adapters.out.persistence.device.DeviceJPAEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "Customer")
public class CustomerJPAEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long customerId;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String name;

    private String eMail;

    private String phone;

    @OneToMany(mappedBy = "customer")
    private List<DeviceJPAEntity> devices = new ArrayList<>();

    public CustomerJPAEntity() {
    }

    public CustomerJPAEntity(String surname, String name, String eMail, String phone) {
        this.surname = surname;
        this.name = name;
        this.eMail = eMail;
        this.phone = phone;
    }

    public long getId() {
        return customerId;
    }

    public void setId(long id) {
        this.customerId = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return name;
    }

    public void setFirstName(String name) {
        this.name = name;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
