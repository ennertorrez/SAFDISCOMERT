package com.suplidora.sistemas.Prinsipal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.suplidora.sistemas.AccesoDatos.ClientesHelper;
import com.suplidora.sistemas.AccesoDatos.DataBaseOpenHelper;
import com.suplidora.sistemas.AccesoDatos.UsuariosHelper;
import com.suplidora.sistemas.HttpHandler;
import com.suplidora.sistemas.R;
import com.suplidora.sistemas.Auxiliar.variables_publicas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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

    final String url= variables_publicas.direccionIp + "/ServicioLogin.svc/BuscarUsuario/";
    final String urlClientes=variables_publicas.direccionIp + "/ServicioClientes.svc/BuscarClientes";
    final String urlPedidos=variables_publicas.direccionIp + "/ServicioPedidos.svc/FormasPago/";

    private DataBaseOpenHelper DbOpenHelper ;
    private UsuariosHelper UsuariosH ;
    private ClientesHelper ClientesH ;


    ArrayList<HashMap<String, String>> listaUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciosesion);

        DbOpenHelper=new DataBaseOpenHelper(Login.this);
        ClientesH = new ClientesHelper(DbOpenHelper.database);
        UsuariosH = new UsuariosHelper(DbOpenHelper.database);

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

//                if (isOnlineNet()==false)
//                {
//                    mensajeAviso("Esta offline");
//                }
//                if (isOnlineNet()==true)
//                {
                   //mensajeAviso("Esta online");
                    new GetUser().execute();
//                }

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
            //************USUARIOS
            HttpHandler sh = new HttpHandler();
            String urlString = url + Usuario + "/" + Contrasenia;
            String jsonStr = sh.makeServiceCall(urlString);
            Log.e(TAG, "Response from url: " + jsonStr);

            /**********************************USUARIOS**************************************/
            if (jsonStr != null) {
                UsuariosH.EliminaUsuarios();
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    listaUsers = new ArrayList<>();
                    // Getting JSON Array node
                    JSONArray Usuarios = jsonObj.getJSONArray("BuscarUsuarioResult");

                    // looping through All Contacts
                    for (int i = 0; i < Usuarios.length(); i++) {
                        JSONObject c = Usuarios.getJSONObject(i);

                        variables_publicas.CodigoVendedor = c.getString("Codigo");
                        variables_publicas.NombreVendedor = c.getString("Nombre");
                        variables_publicas.UsuarioLogin = c.getString("Usuario");
                        String Contrasenia = c.getString("Contrasenia");
                        String Tipo = c.getString("Tipo");
                        variables_publicas.RutaCliente = c.getString("Ruta");
                        variables_publicas.Canal = c.getString("Canal");
                        String TasaCambio = c.getString("TasaCambio");
                        UsuariosH.GuardarUsuario(variables_publicas.CodigoVendedor,variables_publicas.NombreVendedor,
                                variables_publicas.UsuarioLogin,Contrasenia,Tipo,variables_publicas.RutaCliente,variables_publicas.Canal,TasaCambio);


                        variables_publicas.LoginOk = true;
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
            /*******************************CLIENTES******************************/
            //************CLIENTES
            HttpHandler shC = new HttpHandler();
            String urlStringC = urlClientes + "/" + variables_publicas.CodigoVendedor + "/" + tipoBusqueda;
            String jsonStrC = shC.makeServiceCall(urlStringC);
            Log.e(TAG, "Response from url: " + jsonStrC);

            if (jsonStrC != null) {
                ClientesH.EliminaClientes();
                try {
                    JSONObject jsonObjC = new JSONObject(jsonStrC);
                    // Getting JSON Array node
                    JSONArray clientes = jsonObjC.getJSONArray("BuscarClientesResult");

                    // looping through All Contacts
                    for (int i = 0; i < clientes.length(); i++) {
                        JSONObject c = clientes.getJSONObject(i);

                        String IdCliente = c.getString("IdCliente");
                        String CodCv = c.getString("CodCv");
                        String Cliente = c.getString("Cliente");
                        String Nombre = c.getString("Nombre");
                        String FechaIngreso = c.getString("FechaIngreso");
                        String ClienteNuevo = c.getString("ClienteNuevo");
                        String Ruta = c.getString("Ruta");
                        String Direccion = c.getString("Direccion");
                        String Cedula = c.getString("Cedula");
                        String IdVendedor = c.getString("IdVendedor");
                        String Vendedor = c.getString("Vendedor");
                        String IdSupervisor = c.getString("IdSupervisor");
                        String Supervisor = c.getString("Supervisor");
                        String Subruta = c.getString("Subruta");
                        String FechaUltimaCompra = c.getString("FechaUltimaCompra");
                        String Frecuencia = c.getString("Frecuencia");
                        ClientesH.GuardarTotalClientes(IdCliente,CodCv,Cliente,Nombre,FechaIngreso,ClienteNuevo,Ruta,Direccion,Cedula,IdVendedor,Vendedor,IdSupervisor,Supervisor,Subruta,FechaUltimaCompra,Frecuencia);
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
    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    public void mensajeAviso(String texto){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(texto);
        dlgAlert.setPositiveButton(R.string.aceptar,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}