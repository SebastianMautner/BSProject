package sys.bac.application.domain.models.customer;

import java.util.Objects;

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

    public Customer(LongId id, String surname, String name, String eMail, String phone) {
        customerId = id;
        this.surname = surname;
        this.name = name;
        this.eMail = eMail;
        this.phone = phone;
    }

    public Customer(long id, String surname, String name, String eMail, String phone) {
        customerId = new LongId(id);
        this.surname = surname;
        this.name = name;
        this.eMail = eMail;
        this.phone = phone;
    }

    public long getcustomerId() {
        return customerId.getId();
    }

    public LongId getLongId() {
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

    @Override
    public boolean equals(Object dto) {
        if (this == dto) return true;
        if(!(dto instanceof Customer)) return false;
        Customer dto2 = (Customer) dto;
        return Objects.equals(this.customerId, dto2.customerId) &&
        surname == dto2.surname &&
        name == dto2.name &&
        eMail == dto2.eMail &&
        phone == dto2.phone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, surname, name, eMail, phone);
    }
}
