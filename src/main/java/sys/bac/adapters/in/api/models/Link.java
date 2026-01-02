package sys.bac.adapters.in.api.models;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Link {
    private String href;
    private String rel;
    private String type;
    
    public static final Link customers = new Link("customers", "getAllCustomers", "application/json");
    public static final Link orders = new Link("orders", "getAllOrders", "application/json");
    public static final Link devices = new Link("devices", "getAllDevices", "application/json");
    
    public Link() {}
    
    public Link(String href, String rel, String type) {
        this.href = href;
        this.rel = rel;
        this.type = type;
    }
    
    public String getHref() {
        return href;
    }
    
    private String getHeaderHref(URI uri) {
        return uri.toString() + href;
    }
    
    public void setHref(String href) {
        this.href = href;
    }
    
    public String getRel() {
        return rel;
    }
    
    public void setRel(String rel) {
        this.rel = rel;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    @JsonIgnore
    public String getHeaderLink(URI uri) {
        return "<" + getHeaderHref(uri) + ">;rel=\"" + getRel() + "\";type=\"" + getType() + "\"";
    }
    
    public String toString(URI uri) {
        return "Link {href=" + getHeaderHref(uri) + "; rel=\"" + getRel() + "\"; type=\"" + getType() + "\"}";
    }
}
