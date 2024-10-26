package us.redsols.todo.model;

public class Template {
    private String name;
    private String uid;
    private String html;
    private Object design;
    private Boolean isPublic;

    public Template(String name, String uid, String html, Object design, Boolean isPublic) {
        this.name = name;
        this.uid = uid;
        this.html = html;
        this.design = design;
        this.isPublic = isPublic;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getHtml() {
        return html;
    }

    public Object getDesign() {
        return design;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setDesign(Object design) {
        this.design = design;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public String toString() {
        return "Template{" +
                "name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                ", html='" + html + '\'' +
                ", design=" + design +
                ", isPublic=" + isPublic +
                '}';
    }

}
