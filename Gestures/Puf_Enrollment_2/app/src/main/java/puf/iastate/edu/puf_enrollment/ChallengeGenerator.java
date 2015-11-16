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

    private Point arragement(int index)
    {
        if(index==0)
            return randPntInBndstl();
        else if(index ==1)
            return randPntInBndstr();
        else if(index ==2)
            return randPntInBndsbl();
        else if(index ==3)
            return randPntInBndsbr();
        else
            return null;
    }

    private Point randPntInBndstl()
    {
        int x = 100+mRand.nextInt(450);
        int y = 150+mRand.nextInt(625);
        return new Point(x,y,0);
    }

    private Point randPntInBndstr()
    {
        int x = 650+mRand.nextInt(450);
        int y = 150+mRand.nextInt(625);
        return new Point(x,y,0);
    }

    private Point randPntInBndsbl()
    {
        int x = 100+mRand.nextInt(450);
        int y = 875+mRand.nextInt(625);
        return new Point(x,y,0);
    }

    private Point randPntInBndsbr()
    {
        int x = 650+mRand.nextInt(450);
        int y = 875+mRand.nextInt(625);
        return new Point(x,y,0);
    }



   /* private Point randPntInBnds()
    {
        int x = 100 + mRand.nextInt(550); // x ranges from 100 to 650
        int y = 100 + mRand.nextInt(800); // y ranges from 100 to 900
        return new Point(x, y, 0);
    }*/



    private double slope(Point p1, Point p2)
    {
        double s = (p1.y-p2.y)/(p1.x-p2.x);
        return 180*Math.atan(s)/Math.PI;
    }


    /**
     * First generation method
     * @return A challenge vector
     */
    private ArrayList<Point> challGenMethod1()
    {
        ArrayList<Point> challenge = new ArrayList<Point>();
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        indexs.add(0);
        indexs.add(1);
        indexs.add(2);
        indexs.add(3);
        int index1 = mRand.nextInt(3);
        challenge.add(arragement(indexs.get(index1)));
        indexs.remove(index1);

        int index2 = mRand.nextInt(2);
        challenge.add(arragement(indexs.get(index2)));
        indexs.remove(index2);

        int index3 = mRand.nextInt(1);
        challenge.add(arragement(indexs.get(index3)));
        indexs.remove(index3);

        challenge.add(arragement(indexs.get(0)));
        int rejectionPoint = 100;

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

        for(int i=1;i<challenge.size()-1 && !badPath;i++)
        {
            double slope1 = slope(challenge.get(i-1),challenge.get(i));
            double slope2 = slope(challenge.get(i),challenge.get(i+1));
            if(Math.abs(slope1-slope2)<30)  badPath=true;
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

    private Point P1()
    {
        int x = 100+mRand.nextInt(500);
        int y = 150+mRand.nextInt(338);
        return new Point(x,y,0);
    }

    private Point P2()
    {
        int x = 600+mRand.nextInt(500);
        int y = 150+mRand.nextInt(338);
        return new Point(x,y,0);
    }

    private Point P3()
    {
        int x = 100+mRand.nextInt(500);
        int y = 488+mRand.nextInt(337);
        return new Point(x,y,0);
    }

    private Point P4()
    {
        int x = 600+mRand.nextInt(500);
        int y = 488+mRand.nextInt(337);
        return new Point(x,y,0);
    }

    private Point P5()
    {
        int x = 100+mRand.nextInt(500);
        int y = 825+mRand.nextInt(337);
        return new Point(x,y,0);
    }

    private Point P6()
    {
        int x = 600+mRand.nextInt(500);
        int y = 825+mRand.nextInt(337);
        return new Point(x,y,0);
    }

    private Point P7()
    {
        int x = 100+mRand.nextInt(500);
        int y = 1162+mRand.nextInt(338);
        return new Point(x,y,0);
    }

    private Point P8()
    {
        int x = 600+mRand.nextInt(500);
        int y = 1162+mRand.nextInt(338);
        return new Point(x,y,0);
    }


    /*
     * This method generates longer paths
     */
    private ArrayList<Point> challGenMethod2()
    {
        ArrayList<Point> challenge = new ArrayList<Point>();
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        for(int i=0;i<8;i++)
        {
            indexs.add(i);
        }
        for(int i=7;i>0;i--)
        {
            int index = mRand.nextInt(i);
            challenge.add(arragement(indexs.get(index)));
            indexs.remove(index);
        }
        challenge.add(arragement(indexs.get(0)));

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

        for(int i=1;i<challenge.size()-1 && !badPath;i++)
        {
            double slope1 = slope(challenge.get(i - 1), challenge.get(i));
            double slope2 = slope(challenge.get(i),challenge.get(i+1));
            if(Math.abs(slope1-slope2)<30)  badPath=true;
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
