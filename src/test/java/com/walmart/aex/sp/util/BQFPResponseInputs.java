package com.walmart.aex.sp.util;

import com.walmart.aex.sp.dto.bqfp.BumpSet;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import com.walmart.aex.sp.dto.bqfp.Fixture;

import java.util.ArrayList;
import java.util.List;

public class BQFPResponseInputs {

    public static List<Fixture> getFixtureList(int fixtrLength, String... fixtureType) {
        String fixtureType1 = fixtureType.length > 0 ? fixtureType[0] : null;
        String fixtureType2 = fixtureType.length > 1 ? fixtureType[1] : null;
        List<Fixture> fixtures = new ArrayList<>();
        Fixture fixture1 = getFixture(fixtureType1,2);
        Fixture fixture3 = getFixture(fixtureType2,3);

        switch(fixtrLength) {
            case 1:
                fixtures.add(fixture1);
                break;
            case 2:
                fixtures.add(fixture1);
                fixtures.add(fixture3);
                break;
        }
        return fixtures;
    }

    private static Fixture getFixture(String fixtureType , int clustLength) {
        Fixture fixture1 = new Fixture();
        fixture1.setFixtureType(fixtureType);
        fixture1.setClusters(getAllClusters(clustLength));
        return fixture1;
    }

    private static List<Cluster> getAllClusters(int clustLength ){
        List<Cluster> clusters = new ArrayList<>();
        BumpSet b1 = new BumpSet();
        BumpSet b2 = new BumpSet();
        BumpSet b3 = new BumpSet();
        Cluster c1 = getCluster(b1);
        Cluster c2 = getCluster(b1, b2);
        Cluster c3 = getCluster(b1, b2, b3);
        switch(clustLength){
            case 1:
                clusters.add(c1);
                break;
            case 2:
                clusters.add(c1);
                clusters.add(c2);
                break;
            case 3:
                clusters.add(c1);
                clusters.add(c2);
                clusters.add(c3);
                break;
        }
        return clusters;
    }

    private static Cluster getCluster(BumpSet b1, BumpSet... b) {
        Cluster c = new Cluster();
        BumpSet b2 = b.length > 0 ? b[0] : null;
        BumpSet b3 = b.length > 1 ? b[1] : null;
        List<BumpSet> bumpSets = new ArrayList<>();
        bumpSets.add(b1);
        if(b2!=null){
            bumpSets.add(b2);
        }
        if(b3!=null){
            bumpSets.add(b3);
        }
        c.setBumpList(bumpSets);
        return c;
    }
}
