package com.suplidora.sistemas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by usuario on 20/3/2017.
 */

public class Login extends Activity {
    private String TAG = Login.class.getSimpleName();
    private Button btnIngresar;
    private EditText txtUsuario;
    private EditText txtPassword;
    private String Usuario= "";
    private String Contrasenia = "";
    private ProgressDialog pDialog;
    private String tipoBusqueda = "3";

    // URL to get contacts JSON

    final String url=variables_publicas.direccionIp + "/ServicioLogin.svc/BuscarUsuario/";
    final String urlClientes=variables_publicas.direccionIp + "/ServicioClientes.svc/BuscarClientes/";

    private ClientesHelper databaseHelper;

    ArrayList<HashMap<String, String>> listaUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciosesion);

        txtUsuario = (EditText)findViewById(R.id.txtUsuario);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        btnIngresar = (Button)findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(txtUsuario.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(txtPassword.getWindowToken(), 0);
                Usuario = txtUsuario.getText().toString();
                Contrasenia = txtPassword.getText().toString();

                if(TextUtils.isEmpty(Usuario)) {
                    txtUsuario.setError("Ingrese el nombre de usuario");
                    return;
                }
                if(TextUtils.isEmpty(Contrasenia)) {
                    txtPassword.setError("Ingrese la contraseña");
                    return;
                }

                new GetUser().execute();
                //AlertDialog.Builder builder = new AlertDialog.Builder(this);
            }
        });
    }
    private class GetUser extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            Intent intent = new Intent("android.intent.action.Barra_cargado");
            startActivity(intent);
            finish();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String urlString = url + Usuario + "/" + Contrasenia;
            String jsonStr = sh.makeServiceCall(urlString);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {

                try {
                    /***********************OBTENER USUARIOS*********************/
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    listaUsers = new ArrayList<>();
                    // Getting JSON Array node
                    JSONArray Usuarios = jsonObj.getJSONArray("BuscarUsuarioResult");

                    // looping through All Contacts
                    for (int i = 0; i < Usuarios.length(); i++) {
                        JSONObject c = Usuarios.getJSONObject(i);

                        variables_publicas.CodigoVendedor = c.getString("Codigo");
                        variables_publicas.NombreVendedor = c.getString("Nombre");
                        variables_publicas.UsarioLogin = c.getString("Usuario");
                        String Contrasenia = c.getString("Contrasenia");
                        String Tipo = c.getString("Tipo");
                        variables_publicas.RutaCliente = c.getString("Ruta");
                        variables_publicas.Canal = c.getString("Canal");
                        String TasaCambio = c.getString("TasaCambio");
                        //databaseHelper.EliminaClientes();

                        HashMap<String, String> user = new HashMap<>();

//                        // adding each child node to HashMap key => value
                        listaUsers.add(user);
                        variables_publicas.LoginOk = true;
//                        if(Usuario.equals(User) && Contrasenia.equals(Pass)) {
////                            Intent intent=new Intent("android.intent.action.Barra_cargado");
////                            startActivity(intent);
////                            finish();
//
//                        }
//                        else  {
//                            //Logger.d("Successfull image received.");
//                            //Logger.getLogger("dasdasdas");
//                            //Toast.makeText(getApplicationContext(),"suario Invalido",Toast.LENGTH_SHORT).show();
//                            //Log.e("Log", "Failed..");
//                        }
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
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            return  null;
        }
    }

}
