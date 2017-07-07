package com.javifocus2009gmail.ubication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Actividad principal que mostrará en un spinner las máquinas que hay operativas
 */
public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SENDER_ID = "maquinas-146710";
    // ArrayList necesarios para meter las máquinas y las ubicaciones de cada máquina
    private ArrayList<HashMap<String, String>> machineList;
    private ArrayList<Ubication> ubicationsList;

    // Y sus adaptadores correspondientes para mostrarlos en la vista
    private ListAdapter adapterMachines;
    private MyListAdapter adapterUbications;

    // Elementos a mostrar
    private Spinner spinner;
    private ListView listView;
    private Button btnGuardar;
    private Button btnSaveNave;
    private Button btnDel = null;
    private EditText etUbication;
    private AlertDialog.Builder dialog;
    private ProgressDialog pDialog;

    // URLs para conectar y mostrar resultados
    private static final String URL_ROOT = "http://mismaquinas.esy.es";
//    private static final String URL_ROOT = "http://192.168.100.2";
    private static final String URL_GET_MACHINES = URL_ROOT + "/get_machines_2.php";
//    private static final String URL_GET_MACHINES = URL_ROOT + "/get_machines.php";
    private static final String URL_SET_UBICATION = URL_ROOT + "/set_ubication_machine_2.php";
//    private static final String URL_SET_UBICATION = URL_ROOT + "/set_ubication_machine.php";
    private static final String URL_GET_UBICATIONS_MACHINE = URL_ROOT + "/get_ubications_machine_2.php";
//    private static final String URL_GET_UBICATIONS_MACHINE = URL_ROOT + "/get_ubications_machine.php";
    private static final String URL_NOTIFICATION = URL_ROOT + "/send_notifications_2.php";// Previsto añadir sistema de notificaciones

    // Variables necesarias para modificar estados
    private int pos = -1;
    private String idUser = null;
    private String nameUser = null;
    private User user;
    private String nameMachine = null;
    private String idMachine = null;
    private String ubicationMachine = null;
    private String message = null;
    private String object = null;
    private GetWebService threadConec;

    // Variables necesarias para el sistema de notificaciones
    Intent intent;
    PendingIntent pendingIntent;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 1;

    private Bundle bundle;
    private static int msgId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        machineList = new ArrayList<>();
        spinner = (Spinner) findViewById(R.id.spinner);
        btnGuardar = (Button) findViewById(R.id.btnSave);
        btnSaveNave = (Button) findViewById(R.id.buttonClean);
        etUbication = (EditText) findViewById(R.id.inputUbication);

        bundle = getIntent().getExtras();
        idUser = bundle.getString("id");
        nameUser = bundle.getString("name");
        nameUser = capitalize(nameUser);
        user = new User(idUser, nameUser);

        if(user != null) {
            setActionBar();
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // Compruebo si hay conexión a Internet
            if (networkInfo != null && networkInfo.isConnected()) {
                getMachines();
                btnGuardar.setOnClickListener(this);
            } else {
                Toast.makeText(this, "Error de conexión, comprueba si tienes acceso" +
                        " a Internet", Toast.LENGTH_LONG).show();
            }

            btnSaveNave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialog("¿Seguro que quiere guardar en la Nave la máquina " + nameMachine + " ?", true, 3);
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    pos = position;
                    object = adapterMachines.getItem(pos).toString();

                    // Para móviles pequeños
//                    nameMachine = object.substring(object.indexOf("maquina="), object.lastIndexOf(",")).replace("maquina=", "");
//                    idMachine = object.substring(object.indexOf("id="), object.indexOf(",")).replace("id=", "");

                    // Para móviles potentes
                    nameMachine = object.substring(object.indexOf("maquina="), object.lastIndexOf("}")).replace("maquina=", "");
                    idMachine = object.substring(object.indexOf("id="), object.lastIndexOf(",")).replace("id=", "");

                    getUbicationMachine();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else{
            Toast.makeText(this, "Error, no se puede acceder " +
                    "a esta parte de la aplicación", Toast.LENGTH_LONG).show();
        }
    }

    private void setActionBar(){
        String userName = new String(nameUser);
        getSupportActionBar().setTitle("Bienvenido " + capitalize(userName));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSave:
                ubicationMachine = etUbication.getText().toString().trim();
                if (ubicationMachine.length() < 3) {
                    showAlertDialog("Debe escribir al menos 3 caracteres.", false, 3);
                } else {
                    showAlertDialog("¿Seguro que quiere añadir esta ubicación a la máquina " + nameMachine + " ?", true, 2);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Capitaliza las palabras
     */
    private String capitalize(String s) {
        String firstChar = s.substring(0,1);
        if(firstChar.equals("\"")){
            firstChar = s.substring(1,2);
        }
        s = s.replaceFirst(firstChar, firstChar.toUpperCase());
        return s;
    }

    /**
     * Oculta el teclado
     */
    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, 0);
    }

    /**
     * Obtengo las ubicaciones de una máquina
     */
    private void getUbicationMachine() {
        threadConec = new GetWebService();
        threadConec.execute(URL_GET_UBICATIONS_MACHINE, "1", idMachine);
    }

    private void showAlertDialog(final String message, final boolean b, final int quest) {
        dialog = new AlertDialog.Builder(SecondActivity.this);
        dialog.setMessage(message)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (b) {
                            if(quest == 2) {
                                showAlertDialog("¿Quieres añadir geolocalización a la máquina " + nameMachine + " ?", true, 1);
                                hideKeyboard();
                            }else if(quest == 1){
                                Intent intent = new Intent(SecondActivity.this, CreateUbicationMapActivity.class);
                                intent.putExtra("UBICATION", etUbication.getText().toString().trim());
                                intent.putExtra("MACHINE", Integer.parseInt(idMachine));
                                intent.putExtra("USER", user.getId());
                                startActivityForResult(intent, 1);
                                overridePendingTransition(R.anim.fade_in_2, R.anim.fade_out_2);
                            }else{
                                threadConec = new SecondActivity.GetWebService();
                                threadConec.execute(URL_SET_UBICATION, "2", "Nave", idMachine, user.getId());
                                etUbication.setText("");
                                showNotification();
                                getUbicationMachine();
                            }
                        }
                    }
                });
        if (b) {
            String bt;
            if(quest == 2 || quest == 3){
                bt = "Cancelar";
            }else{
                bt = "No quiero";
            }
            dialog.setNegativeButton(bt, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(quest == 1){
                        threadConec = new SecondActivity.GetWebService();
                        threadConec.execute(URL_SET_UBICATION, "2", etUbication.getText().toString().trim(), idMachine, user.getId());
                        etUbication.setText("");
                        showNotification();
                        getUbicationMachine();
                    }else if(quest == 3){
                        return;
                    }
                }
            });
        }
        dialog.show();
    }

    private void showNotification() {

    }

    /**
     * Obtengo todas las máquinas disponibles
     */
    private void getMachines() {
        new getMachines().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        etUbication.setText("");
        idMachine = data.getStringExtra("MACHINE");
        getUbicationMachine();
    }

    /**
     * Clase AsyncTask para realizar operaciones contra el servidor
     */
    class GetWebService extends AsyncTask<String, Integer, String> {
        ProgressDialog pDialog = new ProgressDialog(SecondActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SecondActivity.this);
            pDialog.setMessage("Espere por favor...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String string = params[0];
            URL url = null;
            String id = "";
            String ubi = "";
            String dat = "";
            String user = "";
            String latitude = "";
            String longitude = "";
            ubicationsList = new ArrayList<>();
            String resp = null;

            if (params[1] == "1") { // GET - Consulta que muestra las ubicaciones de una máquina
                string += "?idMachine=" + idMachine;

                try {
                    url = new URL(string);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    int respCode = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    if (respCode == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        JSONObject responseJSON = new JSONObject(result.toString());
                        String resultJSON = responseJSON.getString("estado");

                        if (resultJSON == "1" || resultJSON == String.valueOf(1)) {


                            JSONArray ubicationsJSON = responseJSON.getJSONArray("ubicaciones_maquina");
                            Ubication ubication;


                            for (int i = 0; i < ubicationsJSON.length(); i++) {

                                id = ubicationsJSON.getJSONObject(i).getString("id");
                                ubi = ubicationsJSON.getJSONObject(i).getString("ubicacion");
                                dat = ubicationsJSON.getJSONObject(i).getString("fecha");
                                user = ubicationsJSON.getJSONObject(i).getString("usuario");
                                if(ubicationsJSON.getJSONObject(i).getString("latitud") == "null"
                                        || ubicationsJSON.getJSONObject(i).getString("latitud") == ""){
                                    latitude = null;
                                    longitude = null;
                                }else{
                                    latitude = ubicationsJSON.getJSONObject(i).getString("latitud");
                                    longitude = ubicationsJSON.getJSONObject(i).getString("longitud");
                                }
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = simpleDateFormat.parse(dat);
                                simpleDateFormat = new SimpleDateFormat("EEEEEE, d MMM yyyy HH:mm:ss");
                                String dateFormat = simpleDateFormat.format(date);
                                if(user == "null") user = "";
                                ubication = new Ubication(id, ubi, dateFormat, user, latitude, longitude, nameMachine, Integer.parseInt(idMachine));
                                ubicationsList.add(ubication);
                            }
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (params[1] == "2") { // POST - Insercción de una nueva ubicación para un máquina
                try {

                    url = new URL(string);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.connect();

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("ubication", params[2]);
                    jsonParam.put("idMachine", params[3]);
                    jsonParam.put("idUser", params[4]);

                    System.out.println("-- JsonParam(GetWebService) -- : "+String.valueOf(jsonParam));

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();

                    int response = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    if (response == HttpURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }

                        JSONObject responseJSON = new JSONObject(result.toString());

                        String resultJSON = responseJSON.getString("estado");

                        if (resultJSON == "1") {
                            resp = "Ubicación añadida correctamente";
                        } else {
                            resp = "No se ha podido añadir la ubicación";
                        }

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String c = "";

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            // Si el usuario que hay logeado es el administrador o Paco le cambio el valor a la cadena
            if(user.getName().equals("\"administrador\"") || user.getName().equals("\"Paco\"")){
                c = "permission";
            }

            // Creo el adaptador para mostrar las ubicaciones
            adapterUbications = new MyListAdapter(SecondActivity.this, ubicationsList, c);
            listView.setAdapter(adapterUbications);
        }
    }

    /**
     * Clase AsyncTask para mostrar las máquinas disponibles
     */
    private class getMachines extends AsyncTask<Void, Void, Void> {

        // Muestro la ventana de carga
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SecondActivity.this);
            pDialog.setMessage("Espera por favor...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // Ejecuto la funcionalidad de la clase: selecciono todas las máquinas disponibles
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Construyo la URL para la consulta
            String jsonStr = sh.makeServiceCall(URL_GET_MACHINES);
            if (jsonStr != null) {
                try {
                    System.out.println("SecondActivity: ---- "+jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray machines = jsonObj.getJSONArray("maquinas");
                    JSONObject c;
                    HashMap<String, String> machine;
                    String id;
                    String name;
                    String ubication;
                    String enrolment = "prueba";

                    // looping through All Machines
                    for (int i = 0; i < machines.length(); i++) {

                        c = machines.getJSONObject(i);

                        id = c.getString("id");
                        name = c.getString("maquina");
//                        ubication = c.getString("ubicacion");
//                        enrolment = c.getString("matricula");

                        // tmp hash map for single machine
                        machine = new HashMap<>();

                        // Añade un nodo hijo al elemento  HashMap machine: key => value
                        machine.put("id", id);
                        machine.put("maquina", name);
//                        machine.put("ubicacion", ubication);
//                        machine.put("matricula", enrolment);
//                        machine.put("matricula", enrolment);

                        // adding machine to machine list
                        machineList.add(machine);
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Error obteniendo las máquinas",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "No se puede conectar con el servidor. Comprueba tu conexión a Internet",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        // Después de la ejecución...
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Elimino el cuadro de carga
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Meto en el adaptador todas las máquinas de la consulta
             * */
            adapterMachines = new SimpleAdapter(
                    SecondActivity.this, machineList,
                    R.layout.list_item, new String[]{"maquina"},
                    new int[]{R.id.nameMachine});
            spinner.setAdapter((SpinnerAdapter) adapterMachines);
        }
    }
}