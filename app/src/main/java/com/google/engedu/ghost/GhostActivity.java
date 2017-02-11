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

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class GhostActivity extends AppCompatActivity {
    static final String STATE_PREFIX = "";
    static final String STATE_STATUS = "";

    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private boolean user_started = false;
    private Random random = new Random();
    private String g_word_frag = new String();

    @Override
    protected void onStop()
    {
        super.onStop();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(STATE_PREFIX, g_word_frag);
        savedInstanceState.putBoolean(STATE_STATUS, userTurn);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        g_word_frag = savedInstanceState.getString(STATE_PREFIX);
        userTurn = savedInstanceState.getBoolean(STATE_STATUS);
        TextView layout_ghost = (TextView) findViewById(R.id.ghostText);
        TextView layout_status = (TextView) findViewById(R.id.gameStatus);
        layout_ghost.setText(g_word_frag);
        if(userTurn)
            layout_status.setText(USER_TURN);
        else
            layout_status.setText(COMPUTER_TURN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            dictionary = new SimpleDictionary(assetManager.open("words.txt"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        user_started = userTurn;
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {

            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView layout_enter_text = (TextView) findViewById(R.id.ghostText);
        // Do computer turn stuff then make it the user's turn again
        if(g_word_frag.equals(""))
        {
            char c = (char) (random.nextInt(26) + 'a');
            g_word_frag += c;
            layout_enter_text.setText(g_word_frag);
            label.setText(USER_TURN);
            userTurn = true;
            return;
        }
        if( (g_word_frag.length() >= 4) && dictionary.isWord(g_word_frag) )
        {
            label.setText("Computer Wins!");
            userTurn = false;
        }
        else
        {
            String new_word = dictionary.getGoodWordStartingWith(g_word_frag, user_started);
            if(new_word == null)
            {
                label.setText("Computer Wins: no possible words");
                userTurn = false;
            }
            else
            {
                g_word_frag += new_word.charAt(g_word_frag.length());
                layout_enter_text.setText(g_word_frag);
                label.setText(USER_TURN);
                userTurn = true;
            }
        }

    }

    public void restart_game(View view)
    {
        g_word_frag = "";
        onStart(view);
    }

    public void challenge_user(View view)
    {
        if(!userTurn)
            return;
        TextView layout_status = (TextView) findViewById(R.id.gameStatus);
        if ( (g_word_frag.length() >= 4) && dictionary.isWord(g_word_frag) )
        {
            layout_status.setText("You win! String is a valid word.");
            userTurn = false;
        }
        else
        {
            String new_word = dictionary.getAnyWordStartingWith(g_word_frag);
            if(new_word == null)
            {
                layout_status.setText("You win! String is not a prefix!");
                userTurn = false;
            }
            else
            {
                layout_status.setText("Computer wins: String is a prefix to a word: " + new_word);
                userTurn = false;
            }
        }
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
        if(userTurn) {
            TextView layout_status = (TextView) findViewById(R.id.gameStatus);
            TextView layout_enter_text = (TextView) findViewById(R.id.ghostText);
            if (!(Character.isLetter((char) event.getUnicodeChar()))) {
                return super.onKeyUp(keyCode, event);
            }
            g_word_frag += (char) event.getUnicodeChar();
            layout_enter_text.setText(g_word_frag);
            userTurn = false;
            computerTurn();
        }

        return super.onKeyUp(keyCode, event);
}
}
