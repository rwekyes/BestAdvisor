package edu.advising.permissions;

import java.util.ArrayList;

/*
value object with list of active holds each containing type, description, office, and resolution instructions
 */
public class RestrictionSummary {
        private final ArrayList<Restriction> holds = new ArrayList<>(); // final because this is a snapshot object

        public void add(Restriction s){
            holds.add(s);
        }

        public ArrayList<Restriction> getHolds(){
            return holds;
        }

        public boolean hasRestrictions(){return !holds.isEmpty();}
}
