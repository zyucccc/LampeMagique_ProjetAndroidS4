package fr.u_psud.keller.chantal.lampemagique;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class LampeMagiqueMainActivity extends AppCompatActivity implements View.OnClickListener {

    // Partie 3 : Boutons
    Button couleur;
    Button plusRouge, moinsRouge, plusVert, moinsVert, plusBleu, moinsBleu;

    // Partie 3 : valeur de la zone de couleur (MVC !)
    int rouge = 255;
    int vert = 0;
    int bleu = 0;

    // Partie 3 : clés pour la persistance courte
    public final static String ROUGE = "ROUGE";
    public final static String VERT = "VERT";
    public final static String BLEU = "BLEU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Partie 4 : observation du cycle de vie
        Log.i("activites","LampeMagiqueMainActivity.onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lampe_magique_main);

        // Partie 3 : récupération des boutons de l'interface
        couleur = (Button) findViewById(R.id.couleur);
        plusRouge = (Button) findViewById(R.id.plusRouge);
        moinsRouge = (Button) findViewById(R.id.moinsRouge);
        plusVert = (Button) findViewById(R.id.plusVert);
        moinsVert = (Button) findViewById(R.id.moinsVert);
        plusBleu = (Button) findViewById(R.id.plusBleu);
        moinsBleu = (Button) findViewById(R.id.moinsBleu);

        // Partie 3 : ajout d'un écouteur sur les boutons
        plusRouge.setOnClickListener(this);
        moinsRouge.setOnClickListener(this);
        plusVert.setOnClickListener(this);
        moinsVert.setOnClickListener(this);
        plusBleu.setOnClickListener(this);
        moinsBleu.setOnClickListener(this);

        // Partie 4 : récupération de la couleur choisie par l'écran d'accueil
        Intent intent = getIntent();
        int c = intent.getIntExtra(AccueilActivity.COULEUR, AccueilActivity.ROUGE);
        switch (c) {
            case AccueilActivity.ROUGE:
                rouge = 255; vert = 0; bleu = 0;
                break;
            case AccueilActivity.VERT:
                rouge = 0; vert = 255; bleu = 0;
                break;
            case AccueilActivity.BLEU:
                rouge = 0; vert = 0; bleu = 255;
                break;
        }

        // Partie 3 : récupération de la couleur avant rotation
        if (savedInstanceState != null) {
            rouge = savedInstanceState.getInt(ROUGE);
            vert = savedInstanceState.getInt(VERT);
            bleu = savedInstanceState.getInt(BLEU);
        }

        changerCouleur(rouge, vert, bleu);

        // Partie 5 : ajout d'un écouteur sur la zone de couleur
        couleur.setOnClickListener(this);
    }

    // Partie 3 : implantation de l'écouteur sur les boutons
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.moinsRouge:
                rouge -= 16;
                if (rouge < 0) rouge = 0;
                break;
            case R.id.plusRouge:
                rouge += 16;
                if (rouge > 255) rouge = 255;
                break;
            case R.id.moinsVert:
                vert -= 16;
                if (vert < 0) vert = 0;
                break;
            case R.id.plusVert:
                vert += 16;
                if (vert > 255) vert = 255;
                break;
            case R.id.moinsBleu:
                bleu -= 16;
                if (bleu < 0) bleu = 0;
                break;
            case R.id.plusBleu:
                bleu += 16;
                if (bleu > 255) bleu = 255;
                break;
        }
        changerCouleur(rouge, vert, bleu);

        // Partie 5 : écouteur sur la zone de couleur
        if (view.getId() == R.id.couleur) {
            new ProgrammationCouleur().execute();
        }
    }

    // Partie 3 : sauvegarde de la couleur avant rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ROUGE, rouge);
        outState.putInt(VERT, vert);
        outState.putInt(BLEU, bleu);
    }

    // Partie 5 : thread gérant la programmation de la zone de couleur
    private class ProgrammationCouleur extends AsyncTask<Void, Integer, Void> {

        int r = 255, v = 0, b = 0;

        @Override
        protected Void doInBackground(Void... voids) {
            for (int count = 0; count < 6; count++) {
                publishProgress(count);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... counts) {
            int count = counts[0];
            switch (count) {
                case 1:
                    v = 255;
                    break;
                case 2:
                    r = 0;
                    break;
                case 3:
                    b = 255;
                    break;
                case 4:
                    v = 0;
                    break;
                case 5:
                    r = 255;
                    break;
            }
            changerCouleur(r, v, b);
        }

        @Override
        protected void onPostExecute(Void v) {
            changerCouleur(rouge, vert, bleu);
        }
    }

    // Partie 6 : thread communiquant avec le serveur
    private class CommunicationServeur extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... c) {
            try {
                // Calcul du message à envoyer au serveur : dans ce corrigé, on a choisi la lampe n°7
                String r = Integer.toHexString(c[0]);
                if (r.length() == 1) r = "0" + r;
                String v = Integer.toHexString(c[1]);
                if (v.length() == 1) v = "0" + v;
                String b = Integer.toHexString(c[2]);
                if (b.length() == 1) b = "0" + b;
                String message = "07" + r + v + b;

                // Ouverture de la socket client et flux correspondant
                Socket socket = new Socket("chadok.info", 9998);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Envoi du message et affichage du retour
                writer.println(message);
                String ack = reader.readLine();
                Log.i("server", ack);

                // Fermeture de la socket client
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // Partie 6 : méthode regroupant les opérations à effectuer lorsque la couleur change
    private void changerCouleur(int r, int v, int b) {
        couleur.setBackgroundColor(Color.rgb(r, v, b));
        new CommunicationServeur().execute(r, v, b);

        // Partie 7 : on applique les préférences à chaque changement de la couleur
        appliquerPref(r, v, b);
    }

    // Partie 7 : menu permettant d'ouvrir les préférences
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menupref, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pref:
                Intent intent = new Intent(this, PrefActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Partie 7 : méthode pour appliquer les préférences
    private void appliquerPref(int r, int v, int b) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean composantes = sharedPreferences.getBoolean("PREF_COMPOSANTES", true);
        if (composantes) {
            float l = Color.luminance(Color.rgb(r, v, b));
            int textColor = (l <= 0.5) ? getColor(R.color.white) : getColor(R.color.black);
            couleur.setTextColor(textColor);
            couleur.setText(getString(R.string.composantes, r, v, b));
        } else {
            couleur.setText("");
        }
    }

    // Partie 4 : observation du cycle de vie
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("activites","LampeMagiqueMainActivity.onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("activites","LampeMagiqueMainActivity.onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("activites","LampeMagiqueMainActivity.onResume");

        // Partie 7 : on applique les préférences au lancement de l'activité
        appliquerPref(rouge, vert, bleu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("activites","LampeMagiqueMainActivity.onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("activites","LampeMagiqueMainActivity.onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("activites","LampeMagiqueMainActivity.onDestroy");
    }
}
