package us.redsols.todo.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ExpenseGroup {
    @Id
    private String id;
    private String name;
    private List<String> members;

    public ExpenseGroup(String name, List<String> members) {
        super();
        this.name = name;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
