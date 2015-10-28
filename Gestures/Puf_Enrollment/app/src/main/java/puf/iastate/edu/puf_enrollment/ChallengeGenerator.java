package puf.iastate.edu.puf_enrollment;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Paul on 10/27/2015.
 * This abstracts the authentication functionality from the authenticate
 * method to create a challenge generator object for generating challenges given a seed
 */
public class ChallengeGenerator {
    private Random mRand;
    private long mSeed;

    private static final int SEEDLENGTH = 8;

    /**
     * public constructor for a challenge generator
     * @param seed The seed for this challenge
     */
    public ChallengeGenerator(long seed){
        mRand = new Random(); //No seed for generating pin padding - this way two people w/ the same pin have different padding

        //Expand pin to SEEDLENGTH digits
        String seedAsString = String.valueOf(seed);
        while ( seedAsString.length() < SEEDLENGTH ) {
            seedAsString += mRand.nextInt(9); //Single digit integers only.
        }

        mSeed = Long.valueOf(seedAsString); //Set the seed

        mRand = new Random(mSeed); //Need a new random w/ this seed
    }

    /**
     * Public accessor for the seed
      * @return
     */
    public long getSeed(){
        return mSeed;
    }

    /**
     * Generate a challenge
     * @return A challenge vector
     */
    public ArrayList<Point> generateChallenge()
    {
        //return challGenMethod3();
        //return challGenMethod2();
        return challGenMethod1();
    }

    /**
     * First generation method
     * @return A challenge vector
     */
    private ArrayList<Point> challGenMethod1()
    {
        ArrayList<Point> challenge = new ArrayList<>();
        challenge.add(randPntInBnds());
        challenge.add(randPntInBnds());
        challenge.add(randPntInBnds());
        challenge.add(randPntInBnds());
        int rejectionPoint = 50;

        // ensure that none of the points are too close to each other
        boolean badPath = false;
        for (int i = 0; i < challenge.size() && !badPath; i++)
        {
            Point point1 = challenge.get(i);
            for (int j = i + 1; j < challenge.size() && !badPath; j++)
            {
                Point point2 = challenge.get(j);
                if (Math.abs((point1.x - point2.x)) < rejectionPoint
                        && Math.abs((point1.y - point2.y)) < rejectionPoint)
                {
                    badPath = true;
                }
            }
        }

        if (badPath)
        {
            return generateChallenge(); // TODO: probably the laziest way to do this.
        }
        else
        {
            return challenge;
        }
    }

    /*
     * This method generates longer paths
     */
    private ArrayList<Point> challGenMethod2()
    {
        ArrayList<Point> challenge = new ArrayList<>();
        challenge.add(randPntInBnds());
        challenge.add(randPntInBnds());
        challenge.add(randPntInBnds());
        challenge.add(randPntInBnds());
        challenge.add(randPntInBnds());
        challenge.add(randPntInBnds());
        challenge.add(randPntInBnds());
        int rejectionPoint = 100;

        // ensure that none of the points are too close to eachother
        boolean badPath = false;
        for (int i = 0; i < challenge.size() && !badPath; i++)
        {
            Point point1 = challenge.get(i);
            for (int j = i + 1; j < challenge.size() && !badPath; j++)
            {
                Point point2 = challenge.get(j);
                if (Math.abs((point1.x - point2.x)) < rejectionPoint
                        && Math.abs((point1.y - point2.y)) < rejectionPoint)
                {
                    badPath = true;
                }
            }
        }

        if (badPath)
        {
            return generateChallenge(); // TODO: probably the laziest way to do
            // this.
        }
        else
        {
            return challenge;
        }
    }

    /*
     * This method generates paths with only a single line
     */
    private ArrayList<Point> challGenMethod3()
    {
        ArrayList<Point> challenge = new ArrayList<>();
        challenge.add(randPntInBnds());
        challenge.add(randPntInBnds());
        int rejectionPoint = 400;

        // ensure that none of the points are too close to eachother
        boolean badPath = false;
        for (int i = 0; i < challenge.size() && !badPath; i++)
        {
            Point point1 = challenge.get(i);
            for (int j = i + 1; j < challenge.size() && !badPath; j++)
            {
                Point point2 = challenge.get(j);
                if (Math.abs((point1.x - point2.x)) < rejectionPoint
                        && Math.abs((point1.y - point2.y)) < rejectionPoint)
                {
                    badPath = true;
                }
            }
        }

        if (badPath)
        {
            return generateChallenge(); // TODO: probably the laziest way to do
            // this.
        }
        else
        {
            return challenge;
        }
    }

    /**
     * Generate a random point
     * @return a new point
     */
    private Point randPntInBnds()
    {
        int x = 100 + mRand.nextInt(550); // x ranges from 100 to 650
        int y = 100 + mRand.nextInt(800); // y ranges from 100 to 900
        return new Point(x, y, 0);
    }
}
