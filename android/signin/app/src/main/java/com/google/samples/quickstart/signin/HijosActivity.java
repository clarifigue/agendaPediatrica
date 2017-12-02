package com.google.samples.quickstart.signin;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class HijosActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    public String idUsuario;

    private String TAG = HijosActivity.class.getSimpleName();

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    private static String url = "http://192.168.0.10:8084";



    ArrayList<HashMap<String, String>> contactList;

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hijos);

        contactList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new obtenerHijos().execute();

        this.setIdUsuario(getIntent().getExtras().getString("idUsuario"));

        dispararNotificaciones(this.getIdUsuario());
    }

    public void dispararNotificaciones(String id){
        String host = "http://192.168.1.7:8084";
        URL ob = null;
        int numero = 001;
        NotificationManager notiManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
            ob = new URL(host + "/WebApplication1/webresources/usuario/notificaciones/");
            HttpURLConnection con = (HttpURLConnection) ob.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoInput(true);
            con.setDoOutput(true);
            byte[] outputInBytes = id.getBytes("UTF-8");
            OutputStream os = con.getOutputStream();
            os.write(outputInBytes);
            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String aux;
            while ((aux = br.readLine()) != null) {
                sb.append(aux);
            }
            String[] respuesta = sb.toString().split(";");
            for (String str : respuesta){
                String[] campos = str.split(",");
                String body = "Pendiente: " + campos[1] + " - Para: " + campos[2];
                NotificationCompat.Builder lanzador =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.jeringa)
                        .setContentTitle("Vacunación de " + campos[0])
                        .setContentText(body);
                notiManager.notify(numero, lanzador.build());
                numero++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private class obtenerHijos extends AsyncTask<Void, Void, Void> {

        public String host = "http://192.168.0.10:8084";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(HijosActivity.this);
            pDialog.setMessage("Aguarda un momento...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            HttpHandler nuevo = new HttpHandler();

            JSONObject obj = new JSONObject();
            try {
                obj.put("idPadre", HijosActivity.this.getIdUsuario());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String jsonStr = nuevo.sendHTTPData(this.host+"/WebApplication1/webresources/hijo/obtenerHijosPost/",obj);

            // Making a request to url and getting response
            //String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray jsonObj = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < jsonObj.length(); i++) {
                        JSONObject c = jsonObj.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("nombre");
                        String email = c.getString("edad");
                        String address = c.getString("sexo");


//                        // Phone node is JSON Object
//                        JSONObject phone = c.getJSONObject("phone");
//                        String mobile = phone.getString("mobile");
//                        String home = phone.getString("home");
//                        String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value

                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", address);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "No se encontraron registros..",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    HijosActivity.this, contactList,
                    R.layout.list_item, new String[]{"name", "email",
                    "mobile"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});
            try {
                lv.setAdapter(adapter);
                // Se asigna la lógica para click sobre un elemento de la lista
                // Se trae el id del hijo seleccionado para pasar a la sgte actividad
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        Intent intent = new Intent(HijosActivity.this, VacunasHijo.class);
                        HashMap<String, String> aux = contactList.get(position);
                        String idHijo = aux.get("id");
                        intent.putExtra("idHijo", idHijo);
                        startActivity(intent);
                    }
                });
            }catch (Exception e){

            }
        }

    }

}
