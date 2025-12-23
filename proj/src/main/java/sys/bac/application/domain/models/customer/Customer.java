package sys.bac.application.domain.models.customer;

import sys.bac.application.domain.models.LongId;

public class Customer {
    private LongId customerId;
    private String surname;

    private String name;

    private String eMail;

    private String phone;

    public Customer() {
        this.customerId = new LongId();
    }

    public LongId getcustomerId() {
        return customerId;
    }

    public void setCId(long customerId) {
        this.customerId = new LongId(customerId);
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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
