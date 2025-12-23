package sys.bac.adapters.in.api.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customer")
public class CustomerDTO extends AbstractDataTransferObject{
    
    @NotBlank
    private String surname;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String eMail;
    
    @NotBlank
    private String phoneNr;
    
    public CustomerDTO() {
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
    
    public String getPhoneNr() {
        return phoneNr;
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
        this.phoneNr = phoneNr;
    }
}
