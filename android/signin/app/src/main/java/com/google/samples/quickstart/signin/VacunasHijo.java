package com.google.samples.quickstart.signin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 *
 */

public class VacunasHijo extends AppCompatActivity {

    public Button btnNombre;
    public Button btnFecha;
    public Button btnAplicada;
    public ListView listNombres;
    public ListView listFechas;
    public ListView listAplicadas;
    public List<String> nombres;
    public List<String> fechas;
    public List<String> aplicadas;
    public String idHijo;
    public boolean nombreAsc;
    public boolean fechaAsc;
    public boolean aplicadaAsc;

    public String getIdHijo() {
        return idHijo;
    }

    public void setIdHijo(String idHijo) {
        this.idHijo = idHijo;
    }

    public static String url = "http://192.168.0.10:8084";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacunas_hijo);
        btnNombre = findViewById(R.id.btn_nombre);
        btnFecha = findViewById(R.id.btn_fecha);
        btnAplicada = findViewById(R.id.btn_aplicada);
        listNombres = findViewById(R.id.nombres);
        listFechas = findViewById(R.id.fechas);
        listAplicadas = findViewById(R.id.aplicadas);
        idHijo = getIntent().getExtras().getString("idHijo");
        nombreAsc = false;
        fechaAsc = false;
        aplicadaAsc = false;
        try {
            traerValores(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        btnNombre.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    traerValores(1);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnFecha.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    traerValores(2);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnAplicada.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    traerValores(3);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void traerValores(int tipo) throws IOException, ParseException {
        String endpoint = new String();
        switch (tipo) {
            case 0:
                endpoint = "/WebApplication1/webresources/hijo/obtenerVacunasPorHijo/";
                nombreAsc = false;
                fechaAsc = false;
                aplicadaAsc = false;
                btnNombre.setBackground(getResources().getDrawable(R.drawable.b1));
                btnFecha.setBackground(getResources().getDrawable(R.drawable.b1));
                btnAplicada.setBackground(getResources().getDrawable(R.drawable.b1));
                break;
            case 1:
                if (!nombreAsc){
                    nombreAsc = true;
                    endpoint = "/WebApplication1/webresources/hijo/obtenerVacunasPorHijoNA/";
                    btnNombre.setBackground(getResources().getDrawable(R.drawable.b2));
                }
                else{
                    nombreAsc = false;
                    endpoint = "/WebApplication1/webresources/hijo/obtenerVacunasPorHijoND/";
                    btnNombre.setBackground(getResources().getDrawable(R.drawable.b3));
                }
                btnFecha.setBackground(getResources().getDrawable(R.drawable.b1));
                btnAplicada.setBackground(getResources().getDrawable(R.drawable.b1));
                break;
            case 2:
                if (!fechaAsc){
                    fechaAsc = true;
                    endpoint = "/WebApplication1/webresources/hijo/obtenerVacunasPorHijoFA/";
                    btnFecha.setBackground(getResources().getDrawable(R.drawable.b2));
                }
                else{
                    fechaAsc = false;
                    endpoint = "/WebApplication1/webresources/hijo/obtenerVacunasPorHijoFD/";
                    btnFecha.setBackground(getResources().getDrawable(R.drawable.b3));
                }
                btnNombre.setBackground(getResources().getDrawable(R.drawable.b1));
                btnAplicada.setBackground(getResources().getDrawable(R.drawable.b1));
                break;
            case 3:
                if (!aplicadaAsc){
                    aplicadaAsc = true;
                    endpoint = "/WebApplication1/webresources/hijo/obtenerVacunasPorHijoAA/";
                    btnAplicada.setBackground(getResources().getDrawable(R.drawable.b2));
                }
                else{
                    aplicadaAsc = false;
                    endpoint = "/WebApplication1/webresources/hijo/obtenerVacunasPorHijoAD/";
                    btnAplicada.setBackground(getResources().getDrawable(R.drawable.b3));
                }
                btnNombre.setBackground(getResources().getDrawable(R.drawable.b1));
                btnFecha.setBackground(getResources().getDrawable(R.drawable.b1));
                break;
        }
        URL ob = new URL(this.url + endpoint);
        HttpURLConnection con = (HttpURLConnection) ob.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoInput(true);
        con.setDoOutput(true);
        byte[] outputInBytes = idHijo.getBytes("UTF-8");
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
        this.nombres = new LinkedList<>();
        this.fechas = new LinkedList<>();
        this.aplicadas = new LinkedList<>();
        if (respuesta.length <= 0){
            respuesta = " , , ;".split(";");
        }
        for (int i=0; i<respuesta.length; i++){
            String[] au = respuesta[i].split(",");
            String dateA = au[1];
            String[] rd = dateA.split("\\s");
            switch (rd[1]){
                case "Jan":
                    rd[1] = "Ene";
                    break;
                case "Apr":
                    rd[1] = "Abr";
                    break;
                case "Aug":
                    rd[1] = "Ago";
                    break;
                case "Dec":
                    rd[1] = "Dic";
                    break;
            }
            this.nombres.add(au[0]);
            this.fechas.add(rd[2] + "/" + rd[1] + "/" + rd[5]);
            this.aplicadas.add(au[2]);
        }
        ArrayAdapter<String> adapNom = new ArrayAdapter<String>(
                this,
                R.layout.valor,
                this.nombres);
        ArrayAdapter<String> adapFec = new ArrayAdapter<String>(
                this,
                R.layout.valor2,
                this.fechas);
        ArrayAdapter<String> adapApl = new ArrayAdapter<String>(
                this,
                R.layout.valor3,
                this.aplicadas);
        this.listNombres.setAdapter(adapNom);
        this.listFechas.setAdapter(adapFec);
        this.listAplicadas.setAdapter(adapApl);
    }
}