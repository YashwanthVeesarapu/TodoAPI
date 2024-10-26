package us.redsols.todo.model;

public class ExpenseSplit {
    private String expenseId;
    private String user;
    private double amount;

    public ExpenseSplit(String expenseId, String user, double amount) {
        super();
        this.expenseId = expenseId;
        this.user = user;
        this.amount = amount;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
