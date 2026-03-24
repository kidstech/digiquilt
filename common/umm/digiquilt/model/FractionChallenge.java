/*
 * Created by biatekjt on Mar 24, 2010
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */
package umm.digiquilt.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Jason Biatek, last changed by $Author: lamberty $
 * on $Date: 2008/01/22 17:50:24 $
 * @version $Revision: 1.1 $
 *
 */
public class FractionChallenge implements Challenge {
    
    /**
     * The name of the person who made this challenge
     */
    private String name;
    
    /**
     * The fractions.
     */
    private Map<Fabric, Fraction> fractions;
    
    
    /**
     * The date that this Challenge was created.
     */
    private Date dateCreated;
    
    /**
     * @param name 
     * @param fractions 
     */
    public FractionChallenge(String name, Map<Fabric, Fraction> fractions) {
        this.name = name;
        this.fractions = fractions;
        this.dateCreated = new Date();
    }
    
    /**
     * @param name
     * @param fractions
     * @param date
     */
    public FractionChallenge(String name, Map<Fabric, Fraction> fractions, Date date){
    	this.name = name;
    	this.fractions = fractions;
    	this.dateCreated = date;
    }

    /**
     * @return the name for this FractionChallenge
     */
    public String getName() {
        return name;
    }

    /**
     * @return the fractions for this challenge.
     */
    public Map<Fabric, Fraction> getFractionMap() {
        return fractions;
    }
    
    /**
     * @return the formatted date (formatted as yy/MM/dd/ss/mm)
     */
    public Date getDateCreated(){
    	return this.dateCreated;
    }
    
    /**
     * @return the xml-ready date (formatted as "yyyy-MM-dd'T'HH:mm:ss")
     */
    public String getXMLDate(){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    	return sdf.format(this.dateCreated);
    }
    

    public boolean blockMatchesChallenge(Block block) {
        for (Fabric fabric : fractions.keySet()){
            if (!block.getBlockCoverage(fabric).equals(fractions.get(fabric))){
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String toString(){
        String description = "("+name+") Create a quilt that is ";
        
        List<Fabric> sortedList = new ArrayList<Fabric>(fractions.keySet());
        
        Collections.sort(sortedList, new Comparator<Fabric>(){

            public int compare(Fabric arg0, Fabric arg1) {
                return arg0.getName().compareTo(arg1.getName());
            }
            
        });
        
        if (sortedList.size() == 1){
            Fabric f = sortedList.get(0);
            description += fractions.get(f)+ " " + f.getName(); 
        } else if (sortedList.size() == 2){
            Fabric f0 = sortedList.get(0);
            Fabric f1 = sortedList.get(1);
            
            description += fractions.get(f0)+" "+f0.getName()+" and "+fractions.get(f1)+" "+f1.getName();
            
        } else {
            
            int fabricsDone = 0;
            for (Fabric fabric : sortedList){
                Fraction fraction = fractions.get(fabric);
                // Check to see if it's the last one or not
                if (fabricsDone < sortedList.size() - 1){
                    description += fraction + " " + fabric.getName() + ", ";
                } else {
                    description += "and "+fraction+" "+fabric.getName();
                }
                fabricsDone++;
            }
        }
        
        
        return description+ " ("+dateCreated.toString()+").";
        
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof FractionChallenge)){
            return false;
        }
        FractionChallenge other = (FractionChallenge) o;
        return fractions.equals(other.fractions);
    }
    
    @Override
    public int hashCode(){
        int hash = 0;
        for (Fabric fabric : fractions.keySet()){
            hash += fabric.hashCode() + fractions.get(fabric).hashCode();
        }
        return hash;
    }
    
    
}
