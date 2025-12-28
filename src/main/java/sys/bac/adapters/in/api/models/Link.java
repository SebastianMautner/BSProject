package sys.bac.adapters.in.api.models;

public class Link {
    private String href;
    private String rel;
    private String type;

    public Link() {}

    public Link(String href, String rel, String type) {
        this.href = href;
        this.rel = rel;
        this.type = type;
    }

    public String getHref() {
        return href;
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

    public String toString() {
        return "Link {href=" + href + "; rel=\"" + rel + "\"; type=\"" + type + "\"}";
    }

    public static void main(String[] args) {
        Link test = new Link("test/test", "args", "application/json");
        System.out.println(test);
    }
}
