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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;
import android.util.Log;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if(prefix.isEmpty())
        {
            Random rand = new Random();
            int rand_val = rand.nextInt(words.size());
            return words.get(rand_val);
        }
        int lower = 0;
        int upper = words.size() - 1;
        int mid = -1;
        while(lower <= upper)
        {
            mid = lower + (upper - lower)/2;

            int compare_result = words.get(mid).compareTo(prefix);
            if(words.get(mid).contains(prefix))
            {
                return words.get(mid);
            }
            else if(compare_result < 0)
            {
                lower = mid + 1;
            }
            else
            {
                upper = mid - 1;
            }
        }
        return null;
    }

    @Override
    public String getGoodWordStartingWith(String prefix, boolean user_started) {
        if(prefix.isEmpty())
        {
            Random rand = new Random();
            int rand_val = rand.nextInt(words.size());
            return words.get(rand_val);
        }

        Set<String> odd_strings = new HashSet<String>();
        Set<String> even_strings = new HashSet<String>();


        int lower = 0;
        int upper = words.size() - 1;
        int mid = -1;
        while(lower <= upper)
        {
            mid = lower + (upper - lower)/2;

            int compare_result = words.get(mid).compareTo(prefix);
            if(words.get(mid).contains(prefix))
            {
                String temp;
                int temp_index = mid;
                //Increment downwards to get all other values that contain the prefix
                while( (temp = words.get(temp_index-1)).contains(prefix))
                {
                    if( (temp.length()%2) == 0 )
                    {
                        even_strings.add(temp);
                    }
                    else
                    {
                        odd_strings.add(temp);
                    }
                    temp_index--;
                }
                //Reset to now go upwards
                temp_index = mid;
                while( (temp = words.get(temp_index+1)).contains(prefix))
                {
                    if( (temp.length()%2) == 0 )
                    {
                        even_strings.add(temp);
                    }
                    else
                    {
                        odd_strings.add(temp);
                    }
                    temp_index++;
                }
                //If user went first, choose from the odd set. Otherwise, choose from the even set.
                if(user_started)
                {
                    Random rand = new Random();
                    int rand_val = rand.nextInt(odd_strings.size());
                    int index = 0;
                    for (String obj : odd_strings)
                    {
                        if(index != rand_val) {
                            index++;
                            continue;
                        }
                        return obj;
                    }
                    Log.d(null, "Bad value in odd_strings");
                    return null;
                }
                else
                {
                    Random rand = new Random();
                    int rand_val = rand.nextInt(even_strings.size());
                    int index = 0;
                    for (String obj : even_strings)
                    {
                        if(index != rand_val) {
                            index++;
                            continue;
                        }
                        return obj;
                    }
                    Log.d(null, "Bad value in even_strings");
                    return null;
                }

            }
            else if(compare_result < 0)
            {
                lower = mid + 1;
            }
            else
            {
                upper = mid - 1;
            }
        }
        return null;
    }
}
