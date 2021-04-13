package com.example.corona_2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON { //is URL istraukiamas JSON ir jis atspausdinamas

    private static String readAll(Reader rd) throws IOException { //metodas
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    //Visas ilgas tekstas is API nuskaitomas i stringa, o is stringo jis konvertuojamas i JSON objekta
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException { //metodas. Kreipsimes i URL ir mums is jo grazins JSON
        InputStream is = new URL(url).openStream();
        try { // i try talpinamas kodas, kuriame gali kilti kokia nors isimtis - klaida
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8"))); //nuskaitoma is URL linko
            String jsonText = readAll(rd); //Grazina stringa, padaromas stringas
            JSONObject json = new JSONObject(jsonText); //formuoja JSON objekta
            return json; //grazina JSON
        } finally { //Ivyks ar neivyks klaida, galiausiai viskas bus uzdaryta. Siuo atveju uzdaromi ivedimo srautai
            is.close();
        }
    }

    //metodas, kuris paims JSON objekta ir grazins masyva. Issitraukiame is JSON tik tai, kas mus domina. Nereikalingos info reikia atsikratyti. Isimsim pacia pradzia (meta duomenis) ir pabaigos simbolius
    public static ArrayList<Corona> getList(JSONArray jsonArray) throws JSONException {//metodo antraste. Grazins sarasa, Corona klases objektu (ArrayList, o ne visa JSON). JSONArray yra klase ir jsonArray yra objektas. ArrayList - vienas is sarasu programavime
        ArrayList<Corona> coronaList = new ArrayList<Corona>();//sukureme sarasa, kur norime pataplinti klases Corona objektus. coronaList - saraso pavadinimas.
        //isimti "data" is JSON (pirma eilute) ir issaugoti corona objektu sarase (coronaList)
        for (int i = 0; i < jsonArray.length(); i++) { //ciklas, rodo 3 salygas: pradinis iteratorius i
            JSONObject jsonObject = jsonArray.getJSONObject(i); //eisim per visa sarasa JSON masyvo
            Corona corona = new Corona( //Corona konstruktorius, susideda is 6 elemntu. 1 elementas - country, todel trauksime country
                    //public Corona(String country, String lastUpdate, String keyID, int confirmed, int deaths)
                    jsonObject.getString("country"), //country is mazosios, nes JSON duomenyse irgi is mazosios. Traukiant raktus is JSON, jie turi buti IDENTISKI
                    jsonObject.getString("lastUpdate"),
                    jsonObject.getString("keyId"),
                    jsonObject.getInt("confirmed"), //visi raktai yra String tipo, eilutes, nors ir grazins int skaiciu
                    jsonObject.getInt("deaths")
            );
            coronaList.add(corona); //eis per visus JSON sarase esancius objektus, paims objektus, issitrauks reiksmes, konstruosime corona klases objekta ir prideime i corona sarasa
        }

        return coronaList;
    }

    public static JSONArray getJSONArray(JSONObject jsonObject) throws JSONException {//metodas
        //pasalinama is JSON visa nereikalinga info (meta duomenys), paliekant tik covid19Stats masyva
        int jsonLength = jsonObject.toString().length(); //mums reikia tik covid19Stats. Gauname viso JSON ilgi (apie 80 tukst simboliu), grazina ilgio skaiciu
        String covid19Stats = "{" + jsonObject.toString().substring(96, jsonLength) + "}"; //jsonObject konvertuojame i eilute (String), substring - iskerpa dali simboliu is stringo. Iskirps nuo 96-to iki pacio galo

        //String konvertacija i JSON objekta
        JSONObject jsonObject1 = new JSONObject(covid19Stats); //perduodame covid19Stats stringa

        //JSONObject i JSONArray. Sukonstarvome is objekto sarasa, masyva
        JSONArray jsonArray = jsonObject1.getJSONArray("covid19Stats");
        return jsonArray; //jsonArray paims getList
    }

    //pagal visa sarasa suformuos tik tra valstybe, kurios mumsreikia
    public static ArrayList<Corona> getCoronaListByCountry(ArrayList<Corona> coronaArrayList, String country) { //metodas, noresim gauti sarasa pagal valstybe. F-ja paims du parametrus - ArrayList, valstybes pav. Grazins arrayList
        ArrayList<Corona> coronaListByCountry = new ArrayList<Corona>();
        //pereisime per visa sarasa Corona ArrayList, ieskosime tos valstybes ir formuosime
        for (Corona corona : coronaArrayList) { //desineje puseje bus sukuriamas tos klases objektas, per kurios sarasa iteruojame. Iteruojame per klases objektus
            if (corona.getKeyId().contains(country)) { //contains metodas (vienas is String metodu) - iesko zodzio dalies. Pradejus rasyti zodi, pradeda ieskoti.
                coronaListByCountry.add(corona);
            }
        }

        return coronaListByCountry;
    }
}

//    public static void main(String[] args) throws IOException, JSONException {
//        JSONObject json = readJsonFromUrl("https://graph.facebook.com/19292868552");
//        System.out.println(json.toString());
//        System.out.println(json.get("id"));
//    }
