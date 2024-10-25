package com.example.whooweswho.objects;

import java.util.ArrayList;

public class Circuit
{
    public ArrayList<PaytrixEdge> circuitElements;
    public double minValue;
    public int numRec; // number of occurrences of minValue in circuit

    public Circuit()
    {
        this.circuitElements = new ArrayList<PaytrixEdge>();
        this.minValue = 0;
        this.numRec = 0;
    }
}
