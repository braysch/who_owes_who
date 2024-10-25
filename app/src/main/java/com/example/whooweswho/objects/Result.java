package com.example.whooweswho.objects;

import java.util.ArrayList;

public class Result
{
    public String memberName;
    public String memberUsername;
    public double totalDebt;
    public int incompletePayments;
    public double progress;
    public ArrayList<CreditStatement> creditStatements;
    public ArrayList<DebitStatement> debitStatements;

    public Result() {
        this.memberName = "default name";
        this.memberUsername = "default username";
        this.totalDebt = 0.00;
        this.incompletePayments = 0;
        this.progress = 0;
        this.creditStatements = new ArrayList<CreditStatement>();
        this.debitStatements = new ArrayList<DebitStatement>();
    }
}
