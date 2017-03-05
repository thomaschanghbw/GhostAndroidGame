/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        if (s.equals(""))
        {
            children.put("\0", null);
        }
        else
        {
            String first_letter = Character.toString(s.charAt(0));
            String remainder_of_string = s.substring(1);
            if (children.containsKey(first_letter))
            {
                children.get(first_letter).add(remainder_of_string);
            }
            else
            {
                TrieNode node = new TrieNode();
                children.put(first_letter, node);
                node.add(remainder_of_string);
            }
        }
    }

    public boolean isWord(String s) {
        if (s.isEmpty() && children.containsKey("\0"))
        {
            return true;
        }
        else if (!s.isEmpty())
        {
            String first_letter = Character.toString(s.charAt(0));
            String remainder_of_string = s.substring(1);
            return children.containsKey(first_letter) && children.get(first_letter).isWord(remainder_of_string);
        }
        return false;
    }

    private TrieNode getNodeStartingWith(String s) {
        if (s.isEmpty())
        {
            return this;
        }
        else
        {
            String first_letter = Character.toString(s.charAt(0));
            String remainder_of_string = s.substring(1);
            if (children.containsKey(first_letter))
            {
                return children.get(first_letter).getNodeStartingWith(remainder_of_string);
            }
            else
            {
                return null;
            }
        }
    }
    

    public String getAnyWordStartingWith(String s) {
        TrieNode node_starting_with_s = getNodeStartingWith(s);
        if (node_starting_with_s == null) {
            return null;
        }
        String random_word = s;
        while (true) {
            Random random = new Random();
            List<String> keys = new ArrayList<>(node_starting_with_s.children.keySet());
            String ran_string = keys.get(random.nextInt(keys.size()));
            if (ran_string.equals("\0")) {
                return random_word;
            }
            node_starting_with_s = node_starting_with_s.children.get(ran_string);
            random_word += ran_string;
        }
    }


    private String getGoodWordStartingWithHelper() {
        List<String> keys = new ArrayList<>(children.keySet());
        if (keys.size() > 0) {
            Random random = new Random();
            String randomKey = keys.get(random.nextInt(keys.size()));
            TrieNode node = children.get(randomKey);
            while (true) {
                Random random1 = new Random();
                List<String> keys1 = new ArrayList<>(node.children.keySet());
                String randomKey1 = keys1.get(random.nextInt(keys1.size()));
                node = node.children.get(randomKey1);

                if (randomKey1.equals("\0")) {
                    return randomKey;
                }
                randomKey += randomKey1;
            }
        }

        return "";
    }

    public String getGoodWordStartingWith(String s) {
        TrieNode nodeWithS = getNodeStartingWith(s);
        if (nodeWithS == null) {
            return null;
        }

        return s + nodeWithS.getGoodWordStartingWithHelper();
    }
}

