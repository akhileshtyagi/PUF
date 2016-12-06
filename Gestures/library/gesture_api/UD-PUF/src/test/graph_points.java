package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import dataTypes.Point;

/**
 * Created by element on 1/15/16.
 * <p>
 * extends JFrame and is capable of graphing lists of points
 * each list will be graphed in a differant color
 */
public class graph_points extends JFrame {
    public static boolean POINT_LABELS = false;

    private JPanel panel;

    private List<List<Point>> point_list_list;
    private List<String> point_name_list;

    /**
     * create set up the frame and panel to which points will be added
     */
    public graph_points() {
        // set up private variables
        this.point_list_list = new ArrayList<List<Point>>();
        this.point_name_list = new ArrayList<String>();

        // set up and add the panel
        this.panel = new GraphPanel();
        this.add(panel);

        this.pack();

        // wait
        try {
            Thread.sleep(1000);
        } catch (Exception e){
            e.printStackTrace();
        }

        // set up frame properties
        this.setVisible(true);
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
    }

    /**
     * add a point list to the frame
     * points is a list of points to add. They will be added with the same color.
     * <p>
     * name will be placed in the legend.
     *
     * @param points
     */
    public void addPointList(List<Point> points, String name) {
        this.point_list_list.add(points);
        this.point_name_list.add(name);
    }

    /**
     * GraphPanel Class is where the points and legend are actually drawn
     */
    class GraphPanel extends JPanel {
        public final int LEGEND_X = 20;
        public final int LEGEND_Y = 20;

        public final int GRID_X = 250;
        public final int GRID_Y = 50;
        public final int GRID_WIDTH = 1080;
        public final int GRID_HEIGHT = 1920;
        public final int SCALE = 2;

        public final int BORDER_THICKNESS = 20;

        public final int POINT_SIZE = 10;
        public final int LEGEND_SEPARATION = 20;

        private ArrayList<Color> colors;

        GraphPanel() {
            // make a list of all colors used
            this.colors = new ArrayList<Color>();
            this.colors.add(Color.blue);
            this.colors.add(Color.black);
            this.colors.add(Color.green);
            this.colors.add(Color.yellow);
            this.colors.add(Color.red);
            this.colors.add(Color.magenta);
            this.colors.add(Color.orange);

            // set a preferred size for the custom panel.
            setPreferredSize(new Dimension(1920, 1080));
            setVisible(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // draw border around grid
            draw_border(g);

            // draw the grid
            draw_grid(g);

            // draw points to panel
            draw_points(g);

            // draw point values
            if(POINT_LABELS) draw_point_values(g);

            // draw the name to the panel
            draw_name(g);
        }

        /**
         * draw a border around the grid
         */
        private void draw_border(Graphics g) {
            g.setColor(Color.pink);

            // draw left side
            g.fillRect(GRID_X - BORDER_THICKNESS, GRID_Y, BORDER_THICKNESS, GRID_HEIGHT / SCALE);

            // draw right side
            g.fillRect(GRID_WIDTH / SCALE + GRID_X, GRID_Y, BORDER_THICKNESS, GRID_HEIGHT / SCALE);

            // draw bottom
            g.fillRect(GRID_X - BORDER_THICKNESS, GRID_Y + GRID_HEIGHT / SCALE, GRID_WIDTH / SCALE + 2*BORDER_THICKNESS, BORDER_THICKNESS);

            // draw top
            g.fillRect(GRID_X - BORDER_THICKNESS, GRID_Y - BORDER_THICKNESS, GRID_WIDTH / SCALE + 2*BORDER_THICKNESS, BORDER_THICKNESS);
        }

        /**
         * draw a grid where the points will go
         */
        private void draw_grid(Graphics g) {
            // set background color
            g.setColor(Color.white);

            // draw the background
            g.fillRect(GRID_X, GRID_Y, GRID_WIDTH / SCALE, GRID_HEIGHT / SCALE);

            // TODO draw grid lines
        }

        /**
         * draw points to the panel
         */
        private void draw_points(Graphics g) {
            // for each list of points
            for (int i = 0; i < point_list_list.size(); i++) {
                // set the color for this index
                g.setColor(this.colors.get(i % colors.size()));

                // draw the first point in the list
                Point p = point_list_list.get(i).get(0);
                int current_point_x = GRID_X + (int) (p.getX() / SCALE);
                int current_point_y = GRID_Y + (int) (p.getY() / SCALE);

                g.fillOval(current_point_x, current_point_y, POINT_SIZE, POINT_SIZE);

                // draw the list of points at this index
                for (int j = 1; j < point_list_list.get(i).size(); j++) {
                    // get the point
                    p = point_list_list.get(i).get(j);

                    // determine previous and this point x,y
                    current_point_x = GRID_X + (int) (p.getX() / SCALE);
                    current_point_y = GRID_Y + (int) (p.getY() / SCALE);
                    int previous_point_x = GRID_X + (int) (point_list_list.get(i).get(j - 1).getX() / SCALE);
                    int previous_point_y = GRID_Y + (int) (point_list_list.get(i).get(j - 1).getY() / SCALE);

                    // draw a line from the previous point to this point
                    g.drawLine(previous_point_x + POINT_SIZE / 2, previous_point_y + POINT_SIZE / 2, current_point_x + POINT_SIZE / 2, current_point_y + POINT_SIZE / 2);

                    // draw the point
                    g.fillOval(current_point_x, current_point_y, POINT_SIZE, POINT_SIZE);
                }
            }
        }

        /**
         * draw the point values associated with each point.
         *
         * These point values include (x,y,pressure,distance,time)
         */
        private void draw_point_values(Graphics g) {
            // first draw a key describing what the values are for each point
            g.setColor(Color.black);
            g.drawString("(x, y, pressure, distance, time)", LEGEND_X, LEGEND_Y + (point_name_list.size() + 1) * LEGEND_SEPARATION);

            // for each list of points
            for (int i = 0; i < point_list_list.size(); i++) {
                // set the color for this index
                g.setColor(this.colors.get(i % colors.size()));

                // draw the list of points at this index
                for (int j = 0; j < point_list_list.get(i).size(); j++) {
                    // get the point
                    Point p = point_list_list.get(i).get(j);

                    // determine previous and this point x,y
                    int current_point_x = GRID_X + (int) (p.getX() / SCALE);
                    int current_point_y = GRID_Y + (int) (p.getY() / SCALE);

                    // draw the point
                    g.drawString(p.toString(), current_point_x + LEGEND_SEPARATION, current_point_y + i*15);
                }
            }
        }

        /**
         * draw name to the panel
         */
        private void draw_name(Graphics g) {
            // for all the names, draw them next to a color
            for (int i = 0; i < point_name_list.size(); i++) {
                // set the color for this index
                g.setColor(this.colors.get(i % colors.size()));

                // draw the point color and the corresponding name
                g.fillOval(LEGEND_X, LEGEND_Y + i * LEGEND_SEPARATION, POINT_SIZE, POINT_SIZE);
                g.drawString(point_name_list.get(i), LEGEND_X + LEGEND_SEPARATION, LEGEND_Y + g.getFont().getSize() + i * LEGEND_SEPARATION);
            }
        }
    }
}
