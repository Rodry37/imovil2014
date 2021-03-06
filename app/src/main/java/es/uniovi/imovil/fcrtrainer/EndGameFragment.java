package es.uniovi.imovil.fcrtrainer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

import es.uniovi.imovil.fcrtrainer.highscores.Highscore;
import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;

public class EndGameFragment extends Fragment implements Button.OnClickListener {
    private final static String ARG_SCORE = "score";
    private final static  String ARG_ID = "exerciseID";
    private int score;  //Score of the game that just ended
    private int exerciseId;

    //Declarations of layout vars
    private Button btPlayAgain;
    private TextView lbScore;
    private TextView lbMotivation;
    private TextView lbHighScore;
    private ImageView ivEndGame;


    public EndGameFragment() {
        // Required empty public constructor
    }


    public static EndGameFragment newInstance(int score, int exerciseId) {
        EndGameFragment fragment = new EndGameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        args.putInt(ARG_ID, exerciseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.score = getArguments().getInt(ARG_SCORE);
            this.exerciseId = getArguments().getInt(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_endgame, container, false);
        btPlayAgain = (Button) rootview.findViewById(R.id.btPlayAgain);
        btPlayAgain.setOnClickListener(this);

        lbScore = (TextView) rootview.findViewById(R.id.lbScore);
        lbHighScore = (TextView) rootview.findViewById(R.id.lbHighScore);
        lbMotivation = (TextView) rootview.findViewById(R.id.lbMotivation);
        ivEndGame = (ImageView) rootview.findViewById(R.id.ivEndGame);
        resolveScore();
        return rootview;


    }

    /**
    * resolveScore computes the score and gives you a motivational phrase depending on the score
    * This also changes the icon on topright of the layout depending on the score
    * If the score is a Max Score, the icon is different and appear a new TextView (lbHighScore)
    */
    private void resolveScore() {
        lbScore.setText(R.string.endgame_score);
        lbScore.setText(lbScore.getText() + "" + this.score);
        lbMotivation.setText(gameOverMessage(this.score));

        if(score < 5)
            ivEndGame.setImageResource(R.drawable.incorrect);

        boolean isHighscore = false;
        try {
            ArrayList<Highscore> highscores = HighscoreManager.loadHighscores(getActivity().getApplicationContext(),
                    PreferenceUtils.getLevel(getActivity()));
            ArrayList<Highscore> trimmedHighscores = new ArrayList<Highscore>();

            for(Highscore h : highscores)
                if(h.getExercise() == this.exerciseId)
                    trimmedHighscores.add(h);

            for(Highscore h : trimmedHighscores)
                if(h.getScore() <= this.score)
                    isHighscore = true;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(isHighscore){
            lbHighScore.setText(R.string.endgame_newrecord);
            ivEndGame.setImageResource(R.drawable.ic_star);
        }
        else
            lbHighScore.setVisibility(View.GONE);

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btPlayAgain)
            getFragmentManager().popBackStackImmediate();   //Return to the previous fragment
    }

    /**
     * Depending on the score, returns a sentence
     * @param pScore
     * @return The motivational sentence
     */
    private String gameOverMessage(int pScore) {
        if(pScore < -99)
            return getResources().getString(R.string.endgame_luck);
        else if(pScore < 1)
            return getResources().getString(R.string.endgame_verybad);
        else if (pScore < 5)
            return getResources().getString(R.string.endgame_bad);
        else if (pScore < 15)
            return getResources().getString(R.string.endgame_good);
        else if (pScore < 40)
            return getResources().getString(R.string.endgame_verygood);
        else if (pScore < 75)
            return getResources().getString(R.string.endgame_excellent);

        else return getResources().getString(R.string.game_over_message);
    }
}
