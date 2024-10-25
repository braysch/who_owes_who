package com.example.whooweswho.objects;

public class DebitStatement
{
    public String creditor;
    public double debit; // money you owe
    public double payment;

    public DebitStatement()
    {
        this.creditor = "creditor";
        this.debit = 0.00;
        this.payment = 0.00;
    }
}
