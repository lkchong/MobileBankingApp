package com.fyp.mobilebankingapp;

public class TransactionItem {
    private String transactionDatetime;
    private String transactionReference;
    private String transactionAmount;

    public TransactionItem(String transactionDatetime, String transactionReference, String transactionAmount) {
        this.transactionDatetime = transactionDatetime;
        this.transactionReference = transactionReference;
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionDatetime() {
        return transactionDatetime;
    }

    public void setTransactionDatetime(String transactionDatetime) {
        this.transactionDatetime = transactionDatetime;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}


