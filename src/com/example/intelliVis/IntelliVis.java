package com.example.intelliVis;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.sun.corba.se.impl.interceptors.InterceptorList;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    // STATIC VARS
    // node, parent, cousins, children
    public static Color[] backgroundColors = { Color.RED, Color.ORANGE, Color.BLUE, Color.GREEN};

    // MEMBER VARS
    String label;
    int width = 100;
    int height = 60;
    IntelliVis vis;
    int type;

    public Button(IntelliVis vis, String label, int type) {
        // Instantiate parent class
        super(label);

        // Member Vars
        this.label = label;
        this.vis = vis;
        this.type = type;

        // Clear default rectangular border
        LineBorder border = new LineBorder(getBackground(), 1);
        this.setBorder(border);

        // add Click Handler
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vis.setNodeName(label);
                vis.render();
            }
        });
    }

    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(backgroundColors[type]);
        }

        g.fillOval(0, 0, getSize().width-1,
                getSize().height-1);
        super.paintComponent(g);

    }

    // Paint the border of the button using a simple stroke.
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawOval(0, 0, getSize().width-1,
                getSize().height-1);
    }


}


public class IntelliVis extends JFrame {

    String nodeName;
    Surface p, p0, p1, p2;
    Map map;
    GridBagConstraints c;


    public IntelliVis(String name) {
        this.nodeName = name;
    }

    public void setNodeName(String name) {
        nodeName = name;
    }

    public void initUI() {
        setTitle("Knowledge Visualizer");
        render();
    }

    public Surface initSurface(Surface s) {

//        if (s != null){
//            s.removeAll();
//            s.repaint();
//        }

        Surface ns = new Surface();
        ns.setLayout(new GridBagLayout());
        return ns;
    }

    public void render() {


        // Init Surface Panels
        if (p != null) {
            p.removeAll();
        }
        if (p0 != null) {
            p0.removeAll();
        }
        if (p1 != null) {
            p1.removeAll();
        }
        if (p2 != null) {
            p2.removeAll();
        }
        p = initSurface(p);
        p0 = initSurface(p0);
        p1 = initSurface(p1);
        p2 = initSurface(p2);

        c = new GridBagConstraints();
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;

        c.gridy = 0;
        p.add(p0, c);

        c.gridy = 1;
        p.add(p1, c);

        c.gridy = 2;
        p.add(p2, c);


        ArrayList<String> parents = getParents(nodeName);
        drawParents(parents);

        ArrayList<String> children = getChildren(nodeName);
        drawChildren(children);

        ArrayList<String> cousins = getCousins(nodeName);
        drawCousins(cousins);

        int nodeLocX = cousins.size()/2;
        drawNode(nodeLocX);

        getContentPane().add(p);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,600);
        setVisible(true);
    }

    public void clear() {
        p.removeAll();
        p.repaint();
    }

    public void drawNode(int x) {
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = x;
        c.gridy = 0;

        Button node = new Button(this, nodeName, 0);
        p1.add(node, c);
    }

    public void drawParents(ArrayList<String> parents) {
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;

        if (parents.size() > 0) {
            Button myBtn5 = new Button(this, parents.get(0), 1);
            p0.add(myBtn5, c);
        }

        if (parents.size() > 1){
            c.gridx = GridBagConstraints.RELATIVE;

            for (int i = 1; i < parents.size(); i++) {
                Button myBtn6 = new Button(this, parents.get(i), 1);
                p0.add(myBtn6, c);
            }
        }
    }

    public void drawChildren(ArrayList<String> children) {
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;

        if (children.size() > 0) {
            Button myBtn5 = new Button(this, children.get(0), 3);
            p2.add(myBtn5, c);
        }

        if (children.size() > 1){
            c.gridx = GridBagConstraints.RELATIVE;

            for (int i = 1; i < children.size(); i++) {
                Button myBtn6 = new Button(this, children.get(i), 3);
                p2.add(myBtn6, c);
            }
        }
    }

    public void drawCousins(ArrayList<String> cousins) {

        int half = cousins.size()/2;

        int offset = 0;
        drawCousinsSlice(cousins, 0, half, offset);

        offset = 1;
        drawCousinsSlice(cousins, half, cousins.size(), offset);

    }

    public void drawCousinsSlice(ArrayList<String> cousins, int lo, int hi, int offset) {
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = lo + offset;
        c.gridy = 0;

        int numCousins = hi - lo;

        if (numCousins > 0) {
            Button btn = new Button(this, cousins.get(lo), 2);
            p1.add(btn, c);
        }

        if (numCousins > 1){
            c.gridx = GridBagConstraints.RELATIVE;

            for (int i = lo + 1; i < hi; i++) {
                Button myBtn6 = new Button(this, cousins.get(i), 2);
                p1.add(myBtn6, c);
            }
        }
    }

    public ArrayList<String> getParents(String name) {
        ArrayList tuple = (ArrayList) map.get(name);
        ArrayList<String> parents = (ArrayList<String>) tuple.get(1);
        return parents;
    }

    public ArrayList<String> getChildren(String name) {
        ArrayList tuple = (ArrayList) map.get(name);
        ArrayList<String> children = (ArrayList<String>) tuple.get(2);
        return children;
    }

    public ArrayList<String> getCousins(String name) {
        ArrayList<String> cousins = new ArrayList<String>();

        ArrayList<String> parents = getParents(name);
        for (int i=0; i<parents.size(); i++) {
            String parent = parents.get(i);
            ArrayList<String> cousinGroup = getChildren(parent);
            for (int j=0; j<cousinGroup.size(); j++) {
                String cousin = cousinGroup.get(j);
                if (!cousin.equals(nodeName))
                    cousins.add(cousin);
            }
        }

        return cousins;
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
