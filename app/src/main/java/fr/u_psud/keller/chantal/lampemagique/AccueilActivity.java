package fr.u_psud.keller.chantal.lampemagique;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

// Partie 4 : contrôleur de l'écran d'accueil

public class AccueilActivity extends AppCompatActivity implements View.OnClickListener {

    Button rouge, vert, bleu;

    final static String COULEUR = "COULEUR";
    final static int ROUGE = 0, VERT = 1, BLEU = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Partie 4 : observation du cycle de vie
        Log.i("activites","AccueilActivity.onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        rouge = (Button) findViewById(R.id.rouge);
        vert = (Button) findViewById(R.id.vert);
        bleu = (Button) findViewById(R.id.bleu);

        rouge.setOnClickListener(this);
        vert.setOnClickListener(this);
        bleu.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int c = 0;
        switch (view.getId()) {
            case R.id.rouge:
                c = ROUGE;
                break;
            case R.id.vert:
                c = VERT;
                break;
            case R.id.bleu:
                c = BLEU;
                break;
        }
        Intent intent = new Intent(this, LampeMagiqueMainActivity.class);
        intent.putExtra(COULEUR, c);
        startActivity(intent);
    }

    // Partie 4 : observation du cycle de vie
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("activites","AccueilActivity.onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("activites","AccueilActivity.onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("activites","AccueilActivity.onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("activites","AccueilActivity.onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("activites","AccueilActivity.onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("activites","AccueilActivity.onDestroy");
    }
}
