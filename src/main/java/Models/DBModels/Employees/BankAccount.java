package Models.DBModels.Employees;

import Models.DBModels.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.Session;

@Entity
@Table(name = "BankAccounts")
public class BankAccount extends BaseModel {
    @Column(nullable = false)
    private String bankName;
    @Column(nullable = false)
    private String accountNumber;
    @Column(nullable = false)
    private String branch;

    public BankAccount(String bankName, String accountNumber, String branch) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.branch = branch;
    }

    public BankAccount() {
        super(0,BankAccount.class.getName());
    }

    @Override
    public String toString() {
        return "Bank Information= \n" +
                "bankName= " + bankName +
                ", accountNumber= " + accountNumber +
                ", branch= " + branch;
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (!(other instanceof BankAccount)) return false;
        BankAccount otherBankAccount = (BankAccount) other;

        // Copy all bank account fields
        this.bankName = otherBankAccount.bankName;
        this.accountNumber = otherBankAccount.accountNumber;
        this.branch = otherBankAccount.branch;

        return true;
    }

    public String getBankName() {
        return bankName;
    }
}
