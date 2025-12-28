package sys.bac.adapters.out.persistence.customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import sys.bac.application.domain.models.LongId;
import jakarta.persistence.GeneratedValue;

@Entity
@Table(name = "Customer")
public class CustomerJPAEntity {
    
    @Id
    @GeneratedValue
    @Positive
    private long customerId;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String name;

    private String eMail;

    private String phone;

    public CustomerJPAEntity() {
    }

    public CustomerJPAEntity(LongId id, String surname, String name, String eMail, String phone) {
        this.customerId = id.getId();
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
