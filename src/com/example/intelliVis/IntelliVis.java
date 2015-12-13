package com.example.intelliVis;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jonathan on 12/12/15.
 */
public class IntelliVis {

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

        Map map = null;
        try {
            map = (Map) object;
        } catch (Exception e) {
            System.out.println("YAML data is not in Map format");
            e.printStackTrace();
        }

        // Add each node as a child to all its parents

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry)it.next();

            // get node name
            String nodeName = (String) pair.getKey();

            // complete the tuple: description, parents, children
            ArrayList tuple = (ArrayList) pair.getValue();
            if (tuple.size() == 1){
                tuple.add(new ArrayList()); //parents
            }
            tuple.add(new ArrayList()); //children


            // get parents
            ArrayList<String> parents = (ArrayList<String>) tuple.get(1);

            for (int i=0; i<parents.size(); i++) {
                String str = parents.get(i);

                // add self to parent's children
                ArrayList parentTuple = null;

                try {
                    parentTuple = (ArrayList) map.get(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ArrayList<String> childList = (ArrayList<String>) parentTuple.get(2);
                childList.add(nodeName);
            }
        }

        System.out.println(map);
    }

}
