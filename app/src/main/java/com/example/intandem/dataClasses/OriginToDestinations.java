package com.example.intandem.dataClasses;

import java.util.ArrayList;
import java.util.List;

public class OriginToDestinations {

    List<DistanceInfo> elements;

    public OriginToDestinations() {
        elements = new ArrayList<>();
    }

    public List<DistanceInfo> getElements() {
        return elements;
    }
}
