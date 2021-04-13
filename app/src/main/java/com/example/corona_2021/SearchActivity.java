package com.example.corona_2021;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity { //cia yra globalus kintamieji, kurie aprasomi klases virsuje
    public static final String COVID_API = "https://covid19-api.weedmark.systems/api/v1/stats"; //kreipiames i URL, kad duotu si API
    private ArrayList <Corona> coronaList = new ArrayList <Corona>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Bus paleidziama nauja gija (procesas) - nuskaitymui JSON is API
        AsyncFetch asyncFetch = new AsyncFetch(); //Sukuriame AsyncFetch klase
        asyncFetch.execute(); //execute iskviecia metodus toje klaseje sukurtoje (t. y. AsyncFetch)
    }
    //klase AsyncFetch naudojame todel, kad bus du lygiagretus procesai (uzduotys). AsyncFetch turi tuos metodus, reikalingus lygiagreciam keliu procesu veikimui
    private class AsyncFetch extends AsyncTask<String, String, JSONObject> {//klase bus naudojama tik sios AsyncFetch klases viduje. Extends - prapleciame tevine klase AsyncTask, kuri atsakinga uz lygiagretuma (keliu procesu veikima vienu metu)
        ProgressDialog progressDialog = new ProgressDialog(SearchActivity.this); //nurodome, kur jis suksis, t.y. SearchActivity lange

        @Override
        protected void onPreExecute() { //onPreExecute bus vykdomas pries doingBackground metoda. Paprasysime vartotojo palaukti, kol gausime duomenis is API. Bus besisukantis vaizdas
            super.onPreExecute();
            progressDialog.setMessage(getResources().getString(R.string.search_loading_data)); //Issitraukiame stringa
            progressDialog.setCancelable(false); //vartotojas negales atsaukti, tures islaukti, kol kraus informacija
            progressDialog.show(); //bus rodomas besisukantis dialogas
        }

        @Override //vyksta antras procesas tuo paciu metu, kai sukasi rutuliukas ekrane. Fone - kreipiames i URL, gauname duomenis
        //Sis metodas skirtas gavimui JSON is API
        protected JSONObject doInBackground(String... strings) { //Sis metodas bus vykdomas tuo metu, kai vartotojas matys besisukanti dialoga (progressDialog)
            try { //try yra blogas, kuriame gali kilti klaidu, pvz. nepavyks nuskaityti JSON. Tai, kas ivyks, bus apdorojama catch'u - isimtyse. Programa neuzlus, o isves vartotojui tam tikrus pranesimus
                JSONObject jsonObject = JSON.readJsonFromUrl(COVID_API); //Kreipsimes i klase Json.java, kuria nusikopijavome. Perduosime URL, kuris yra globalus kintamasis, public tipo
                return jsonObject; //Jei viskas gerai, grazinsime jsonObject
            } catch (IOException e) { //input output exception - ivedimo, isvedimo isimtys
                Toast.makeText(SearchActivity.this,
                        getResources().getString(R.string.search_error_reading_data) + e.getMessage(), //ismesime pranesima apie error'a, getMessage - pateiksime papildomos info
                        Toast.LENGTH_LONG
                ).show();
            } catch (JSONException e) {
                Toast.makeText(SearchActivity.this,
                        getResources().getString(R.string.search_error_reading_data) + e.getMessage(), //ismesime pranesima apie error'a, getMessage - pateiksime papildomos info
                        Toast.LENGTH_LONG
                ).show();
            }
            return null; //null - tuscia, nieko neegzistuoja. Jei bus problemos - grazinsime null - nieko
        } // baigiasi doInBackground metodas

        @Override
        protected void onPostExecute(JSONObject jsonObject) { //onPostExecute - padaryk kazka po. Execute atitinka doInBackground
            progressDialog.dismiss(); //gavome duomenis is background, todel panaikiname besisukanti dialoga

            int statusCode = 0; //kadangi statusCode nezinome, tai irasome 0, pradine reiksme. Virsuj aprasome try
            try { //Kai yra JSON, visada bus try-catch, nes su JSON gali buti klaidu
                statusCode = jsonObject.getInt("statusCode"); //Bandoma gauti statusCode (HTTP busenos koda), kuris bus grazinamas is JSON. Jei bus problemu - catch
            } catch (JSONException e) {
                Toast.makeText(SearchActivity.this,
                        getResources().getString(R.string.search_error_reading_data) + e.getMessage(), //ismesime pranesima apie error'a, getMessage - pateiksime papildomos info
                        Toast.LENGTH_LONG
                ).show();
            } //baigiasi catch

            if(statusCode == HttpURLConnection.HTTP_OK){ //Jei viskas OK, galime tiketis JSON
                //System.err.println(jsonObject.toString()); //Atspausdins ilga JSON teksta (valstybiu sarasa). err - spausdins kaip klaida, kitokios spalvos. System.err - galime pasiziureti teksta terminale
                //Toast.makeText(SearchActivity.this, jsonObject.toString(), //Isspausdins vartotojui JSON, jei viskas gerai
                        //Toast.LENGTH_LONG
                //).show();

                JSONArray jsonArray = null;
                try {
                    jsonArray = JSON.getJSONArray(jsonObject); //is JSON object suformuoja JSON Array
                    coronaList = JSON.getList(jsonArray);
                    System.out.println("Italy covidStats:" + JSON.getCoronaListByCountry(coronaList, "Italy")); //grazins sarasa
                } catch (JSONException e) {
                    //e.printStackTrace();
                    System.out.println(getResources().getString(R.string.search_error_reading_data) + e.getMessage());
                }

            } else { // Kazkas nepavyko (serveris negrazino kodo 200 (OK))
                String message = null;
                try {
                    message = jsonObject.getString("message");
                } catch (JSONException e) { //zemiau apdoroma zinute JSON exeption'ui. Catch pagauna klaida ir ja isspausdina (pvz. per Toast)
                    Toast.makeText(SearchActivity.this,
                            getResources().getString(R.string.search_error_reading_data) + e.getMessage(), //ismesime pranesima apie error'a, getMessage - pateiksime papildomos info
                            Toast.LENGTH_LONG
                    ).show();
                } //baigiasi catch
                // Jei viskas bus gerai
                Toast.makeText(SearchActivity.this,
                        getResources().getString(R.string.search_error_reading_data) + message, //ismesime pranesima apie error'a
                        Toast.LENGTH_LONG
                ).show();
            } //Baigiasi else
        } //Baigiasi onPostExecute
    } //Baigiasi AsyncFetch klase
} //Baigiasi SearchActivity klase