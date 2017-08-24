package com.suplidora.sistemas.sisago.Menu;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.suplidora.sistemas.sisago.AccesoDatos.ArticulosHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.ClientesHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.DataBaseOpenHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.UsuariosHelper;
import com.suplidora.sistemas.sisago.Auxiliar.variables_publicas;
import com.suplidora.sistemas.sisago.Entidades.Cliente;
import com.suplidora.sistemas.sisago.HttpHandler;
import com.suplidora.sistemas.sisago.Pedidos.PedidosActivity;
import com.suplidora.sistemas.sisago.Principal.Login;
import com.suplidora.sistemas.sisago.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by usuario on 20/3/2017.
 */

public class PedidosFragment extends Fragment {
    View myView;
    private DataBaseOpenHelper DbOpenHelper;
    private ClientesHelper ClientesH;
    private String TAG = PedidosFragment.class.getSimpleName();
    private String busqueda = "";
    private String tipoBusqueda = "2";
    private ProgressDialog pDialog;
    private ListView lv;
    private TextView lblFooter;
    private EditText txtBusqueda;
    private RadioGroup rgGrupo;
    private Button btnBuscar;
    public static ArrayList<HashMap<String, String>> listaClientes;
    private ArticulosHelper databaseHelper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        myView= inflater.inflate(R.layout.masterclientes_layout,container,false);
        getActivity().setTitle("Nuevo Pedido");
        lv = (ListView) myView.findViewById(R.id.list);
        registerForContextMenu(lv);
        btnBuscar = (Button) myView.findViewById(R.id.btnBuscar);
        lblFooter = (TextView) myView.findViewById(R.id.lblFooter);
        rgGrupo = (RadioGroup) myView.findViewById(R.id.rgGrupo);

        rgGrupo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(group.getCheckedRadioButtonId()== R.id.rbCodigo){
                    txtBusqueda.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                else {
                    txtBusqueda.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });
        txtBusqueda = (EditText)myView.findViewById(R.id.txtBusqueda);
        txtBusqueda.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btnBuscar.performClick();
                }
                return false;
            }
        });
        listaClientes = new ArrayList<>();
        new GetClientesPedidos().execute();
        lblFooter.setText("Cliente encontrados: " + String.valueOf(listaClientes.size()));

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(txtBusqueda.getWindowToken(), 0);
                busqueda = txtBusqueda.getText().toString();
                tipoBusqueda = rgGrupo.getCheckedRadioButtonId() == R.id.rbCodigo ? "1" : "2";

               /* if(TextUtils.isEmpty(busqueda)) {
                    txtBusqueda.setError("Ingrese un valor");
                    return;
                }*/
                new GetClientesPedidos().execute();
                lblFooter.setText("Cliente encontrados: " + String.valueOf(listaClientes.size()));
            }
        });
        // Launching new screen on Selecting Single ListItem
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String IdCliente = ((TextView) view.findViewById(R.id.IdCliente)).getText().toString();
                String Nombre = ((TextView) view.findViewById(R.id.Nombre)).getText().toString();
                // Starting new intent
                Intent in = new Intent(getActivity().getApplicationContext(), PedidosActivity.class);

                in.putExtra(variables_publicas.CLIENTES_COLUMN_IdCliente, IdCliente );
                in.putExtra(variables_publicas.CLIENTES_COLUMN_Nombre, Nombre );
                startActivity(in);
            }
        });
        return myView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
//
    private class GetClientesPedidos extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

                try {
                    DbOpenHelper = new DataBaseOpenHelper(getActivity().getApplicationContext());
                    ClientesH = new ClientesHelper(DbOpenHelper.database);
                    switch (tipoBusqueda){
                        case "1":
                            listaClientes=ClientesH.BuscarClientesCodigo(busqueda);
                            break;
                        case  "2":
                            listaClientes=ClientesH.BuscarClientesNombre(busqueda);
                            break;
                    }
                } catch (final Exception e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
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
                    getActivity(), listaClientes,
                    R.layout.list_cliente, new String[]{variables_publicas.CLIENTES_COLUMN_IdCliente,"CodCv2", "NombreCompleto", variables_publicas.CLIENTES_COLUMN_Direccion}, new int[]{R.id.IdCliente,R.id.CodCv, R.id.Nombre,
                    R.id.Direccion});
            lv.setAdapter(adapter);
            lblFooter.setText("Cliente Encontrados encontrados: " + String.valueOf(listaClientes.size()));
        }
    }
}