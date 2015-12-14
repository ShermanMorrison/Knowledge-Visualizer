package com.example.intelliVis;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jonathan on 12/12/15.
 */




class Button extends JButton {

    String label;
    int width = 100;
    int height = 60;

    public Button(String label) {
        super(label);
        this.label = label;

    }

//    protected void paintComponent(Graphics g) {
//        if (getModel().isArmed()) {
//            g.setColor(Color.lightGray);
//        } else {
//            g.setColor(getBackground());
//        }
//        g.fillOval(0, 0, getSize().width-1,
//                getSize().height-1);
//        g.setColor(Color.RED);
//        g.drawString("hello!",0,0);
//    }

    // Paint the border of the button using a simple stroke.
//    protected void paintBorder(Graphics g) {
//        g.setColor(getForeground());
//        g.drawOval(0, 0, getSize().width-1,
//                getSize().height-1);
//    }


//    Shape shape;
//    public boolean contains(int x, int y) {
//        if (shape == null ||
//                !shape.getBounds().equals(getBounds())) {
//            shape = new Ellipse2D.Float(0, 0,
//                    getWidth(), getHeight());
//        }
//        return shape.contains(x, y);
//    }

}


public class IntelliVis extends JFrame {

    String nodeName;
    Surface p;
    Map map;
    GridBagConstraints c;

    public IntelliVis(String name) {
        this.nodeName = name;
    }

    public void initUI() {
        setTitle("Knowledge Visualizer");
        render();
    }

    public void render() {
        p = new Surface();
        p.setLayout(new GridBagLayout());
        c = new GridBagConstraints();

        drawNode();

        ArrayList<String> parents = getParents();
        drawParents(parents);

        ArrayList<String> children = getChildren();
        drawChildren(children);

        getContentPane().add(p);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,600);
        setVisible(true);
    }

    public void drawNode() {
        c.gridheight = 1;
        c.weightx = 1.0;

        c.anchor = GridBagConstraints.CENTER;
        c.gridy = 1;
        c.gridx = 1;

        Button node = new Button(nodeName);
        p.add(node, c);
    }

    public void drawParents(ArrayList<String> parents) {
        c.gridheight = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 2;

        if (parents.size() > 0) {
            Button myBtn5 = new Button(parents.get(0));
            p.add(myBtn5, c);
        }

        if (parents.size() > 1){
            c.gridx = GridBagConstraints.RELATIVE;

            for (int i = 1; i < parents.size(); i++) {
                Button myBtn6 = new Button(parents.get(i));
                p.add(myBtn6, c);
            }
        }
    }

    public void drawChildren(ArrayList<String> children) {
        c.gridheight = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 2;

        if (children.size() > 0) {
            Button myBtn5 = new Button(children.get(0));
            p.add(myBtn5, c);
        }

        if (children.size() > 1){
            c.gridx = GridBagConstraints.RELATIVE;

            for (int i = 1; i < children.size(); i++) {
                Button myBtn6 = new Button(children.get(i));
                p.add(myBtn6, c);
            }
        }
    }

    public void drawCousins(ArrayList<String> cousins) {

    }

    public ArrayList<String> getParents() {
        ArrayList tuple = (ArrayList) map.get(nodeName);
        ArrayList<String> parents = (ArrayList<String>) tuple.get(1);
        return parents;
    }

    public ArrayList<String> getChildren() {
        ArrayList tuple = (ArrayList) map.get(nodeName);
        ArrayList<String> children = (ArrayList<String>) tuple.get(2);
        return children;
    }

    public ArrayList<String> getCousins() {
        return null;
    }

    static class Surface extends JPanel {
        private void doDrawing(Graphics g) {

            Graphics2D g2d = (Graphics2D) g.create();

//        BasicStroke bs1 = new BasicStroke(8, BasicStroke.CAP_ROUND,
//                BasicStroke.JOIN_BEVEL);
//        g2d.setStroke(bs1);
//        g2d.drawString("This is gona be awesome", 50, 50);
//        g2d.drawOval(0, 0, 100, 100);

            g2d.dispose();
        }

        @Override
        public void paintComponent(Graphics g) {

            super.paintComponent(g);
            doDrawing(g);
        }
    }

    public static void main(String[] args) {

        // Parse Yaml Map
        YamlReader reader = null;
        try {
            reader = new YamlReader(new FileReader("data/data.yaml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Object object = null;
        try {
            object = reader.read();
        } catch (YamlException e) {
            e.printStackTrace();
        }

        IntelliVis vis = new IntelliVis("root");
        vis.map = null;
        try {
            vis.map = (Map) object;
        } catch (Exception e) {
            System.out.println("YAML data is not in Map format");
            e.printStackTrace();
        }

        // Add each node as a child to all its parents

        Iterator it = vis.map.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry)it.next();

            // get node name
            String nodeName = (String) pair.getKey();

            // complete the tuple: description, parents, children
            ArrayList tuple = (ArrayList) pair.getValue();
            while (tuple.size() < 3){
                tuple.add(new ArrayList());
            }

            // get parents
            ArrayList<String> parents = (ArrayList<String>) tuple.get(1);

            for (int i=0; i<parents.size(); i++) {
                String str = parents.get(i);

                // add self to parent's children
                ArrayList parentTuple = null;

                try {
                    parentTuple = (ArrayList) vis.map.get(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (parentTuple.size() < 3){
                    parentTuple.add(new ArrayList());
                }
                ArrayList<String> childList = (ArrayList<String>) parentTuple.get(2);
                childList.add(nodeName);
            }
        }

        System.out.println(vis.map);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                vis.initUI();
                vis.setVisible(true);
            }
        });
    }

}
