package com.example.cesarsk.say_it;

import android.support.v4.util.Pair;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Claudio on 10/03/2017.
 */

public class SayItPair extends Pair<String, String> implements Serializable {

    private Date adding_time;

    /**
     * Constructor for a Pair.
     *
     * @param first  the first object in the Pair
     * @param second the second object in the pair
     */
    public SayItPair(String first, String second) {
        super(first, second);
    }

    public SayItPair(String first, String second, Date current_time){
        super(first, second);
        adding_time = current_time;
    }

    public Date getAdding_time(){
        return adding_time;
    }
}
