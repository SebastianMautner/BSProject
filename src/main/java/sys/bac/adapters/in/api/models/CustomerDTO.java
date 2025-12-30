package sys.bac.adapters.in.api.models;

import jakarta.validation.constraints.NotBlank;

public class CustomerDTO extends AbstractDataTransferObject{
    
    @NotBlank
    private String surname;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String eMail;
    
    @NotBlank
    private String phone;
    
    public CustomerDTO() {
    }

    public CustomerDTO(long id, String surname, String name, String eMail, String phone) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.eMail = eMail;
        this.phone = phone;
    }
    
    public String getSurname() {
        return surname;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEMail() {
        return eMail;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

    public void setPhoneNr(String phoneNr) {
        this.phone = phoneNr;
    }
}
