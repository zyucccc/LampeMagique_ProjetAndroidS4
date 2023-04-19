package fr.u_psud.keller.chantal.lampemagique;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// Partie 7 : activité de préférences
public class PrefActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        // On attache le fragment de préférences
        Pref pref = new Pref();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.pref, pref);
        transaction.commit();
    }
}
