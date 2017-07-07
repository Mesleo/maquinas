package com.javifocus2009gmail.ubication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jbenitez on 20/10/16.
 */

public class ChangePasswordActivity extends Login  implements View.OnClickListener{

    // URLs para acceder a los datos JSON de la base de datos
    private static final String URL_CHANGE_PASSWORD = URL_ROOT+"/change_password.php";
//    private static final String URL_CHANGE_PASSWORD = URL_ROOT+"/change_password_2.php";
    public static String NAME = "NAME";

    private AlertDialog.Builder dialog;
    private Intent resultIntent;

    private EditText etPassOld;
    private EditText etPassNew;
    private EditText etPassNew2;
    private Button btnCancel;
    private Button btnChangePass;

    private String passOld = "";
    private String pass1 = "";
    private String pass2 = "";
    private String nameUser = "";
    private String us = null;
    private String name = null;
    private int codeOk = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        etPassOld = (EditText) findViewById(R.id.etPassOld);
        etPassNew = (EditText) findViewById(R.id.etNewPass);
        etPassNew2 = (EditText) findViewById(R.id.etNewPass2);
        btnChangePass = (Button) findViewById(R.id.btnChangePass2);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        spinnerUser = (Spinner) findViewById(R.id.spinnerUsers2);
        dialog = new AlertDialog.Builder(ChangePasswordActivity.this);

        btnChangePass.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        new LoginService().execute("1", URL_GET_USERS, "9");


        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etPassOld.setText("");
                etPassNew.setText("");
                etPassNew2.setText("");
                us = adapterList.getItem(position).toString();
                name = us.substring(us.indexOf("nombre="), us.lastIndexOf("}")).replace("nombre=", "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnCancel:
                resultIntent = new Intent(ChangePasswordActivity.this, Login.class);
                startActivity(resultIntent);
                break;
            default:
                passOld = etPassOld.getText().toString().trim();
                pass1 = etPassNew.getText().toString().trim();
                pass2 = etPassNew2.getText().toString().trim();
                if(pass1.length() < 4 || pass1.length() > 11) {
                    showAlertDialog("La contraseña debe tener entre 4 y 10 caracteres", false);
                }else{
                    showAlertDialog("¿Está seguro que desea cambiar la contraseña?", true);
                }
                break;
        }
    }

    private void showAlertDialog(String message, final boolean b) {
        dialog.setMessage(message)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(b) {
                            new WebService().execute("2", URL_CHANGE_PASSWORD, name, passOld, pass1, pass2);
                        }
                    }
                });
        if (b) {
            dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
        }
        dialog.show();
    }

    /**
     * Clase AsyncTask para realizar operaciones contra el servidor
     */
    class WebService extends AsyncTask<String, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(ChangePasswordActivity.this);
        String resultJSON = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ChangePasswordActivity.this);
            pDialog.setMessage("Comprobando datos...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String string = params[1];
            URL url = null;
            Boolean resp = false;

            if (params[0] == "2") {// POST - Envío de nombre, password viejo y nuevo password
                                    // para cambiar el password actual
                try {
                    url = new URL(string);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setConnectTimeout(30000000);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.connect();

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("name", params[2]);
                    jsonParam.put("password", params[3]);
                    jsonParam.put("new_password", params[4]);
                    jsonParam.put("new_password2", params[5]);

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
                        resultJSON = responseJSON.getString("estado");

                        if (resultJSON == String.valueOf(codeOk)) {
//
//                            String us = responseJSON.getString("usuario");
//                            System.out.println("NAMEUSER: -- "+us);
//                            nameUser = us.substring(us.indexOf("\"nombre\":"), us.lastIndexOf(",")).replace("\"nombre\":", "");
                            resp = true;
                        } else {
                            resp = false;
                        }
                    }
                    return resp;
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
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if(resultJSON != null){
                switch (resultJSON){
                    case "0":
                        if(s) {
                            pDialog.dismiss();
                            Intent i = new Intent(ChangePasswordActivity.this, Login.class);
                            i.putExtra("INFO", "Contraseña cambiada correctamente");
                            setResult(RESULT_OK, i);
                            finish();
                        }
                        break;
                    case "1":
                        Toast.makeText(ChangePasswordActivity.this,
                                "La contraseña actual no es correcta", Toast.LENGTH_SHORT).show();
                        break;
                    case "2":
                        Toast.makeText(ChangePasswordActivity.this,
                                "No existe un usuario con ese nombre", Toast.LENGTH_SHORT).show();
                        break;
                    case "3":
                        Toast.makeText(ChangePasswordActivity.this,
                                "La contraseña debe tener entre 4 y 10 caracteres", Toast.LENGTH_SHORT).show();
                        break;
                    case "4":
                        Toast.makeText(ChangePasswordActivity.this,
                                "El campo \"Nueva constraseña\" y \"Repetir contraseña\" deben coincidir",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }

        }
    }

}
