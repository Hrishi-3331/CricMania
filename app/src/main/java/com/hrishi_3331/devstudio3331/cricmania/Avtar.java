package com.hrishi_3331.devstudio3331.cricmania;

public class Avtar {

    private int[] avtars = {R.drawable.asian, R.drawable.bandit, R.drawable.batman, R.drawable.bull, R.drawable.chef, R.drawable.cricketer,
            R.drawable.doctor, R.drawable.dragon, R.drawable.girl_blonde, R.drawable.grandpa, R.drawable.japanese_dragon, R.drawable.king,
            R.drawable.man_coat, R.drawable.man_glasses, R.drawable.man_goatee, R.drawable.nanny, R.drawable.policeman, R.drawable.skull,
            R.drawable.spy, R.drawable.superhero, R.drawable.wolf, R.drawable.woman, R.drawable.woman_hat
    };

    public Avtar() {

    }

    public int getMyAvtar(int i){
        return avtars[i];
    }
}
