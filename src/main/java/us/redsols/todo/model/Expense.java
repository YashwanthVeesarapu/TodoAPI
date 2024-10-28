package us.redsols.todo.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Expense {
    @Id
    private String id;
    private String description;
    private double amount;
    private String date;
    private String paidBy;
    private List<ExpenseSplit> splits;
    private String groupId;

    public Expense(String description, double amount, String date, String paidBy, List<ExpenseSplit> splits,
            String groupId) {
        super();
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.paidBy = paidBy;
        this.splits = splits;
        this.groupId = groupId;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public List<ExpenseSplit> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseSplit> splits) {
        this.splits = splits;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
