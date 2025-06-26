package com.safi_d.sistemas.safdiscomert.CodeBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.safi_d.sistemas.safdiscomert.Auxiliar.Funciones;
import com.safi_d.sistemas.safdiscomert.Auxiliar.variables_publicas;
import com.safi_d.sistemas.safdiscomert.HttpHandler;
import com.safi_d.sistemas.safdiscomert.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class ActivityScannPrincipal  extends AppCompatActivity {
    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    private Button scanBtn,ingresarBtn;
    private RadioButton rbActivo;
    private RadioButton rbDesactivado;
    private RadioGroup rgEstado;
    private EditText txtCodigo,txtCodigoBarra;
    private TextView  txtDescripcion, txtExistencia, txtPrecio1, txtPrecio1b,txtPrecio2,txtPrecio2b,txtPrecio3,txtPrecio3b,txtProveedor,txtBodega,txtPedido;
    final String urlDatosArticulo= variables_publicas.direccionIp + "/ServicioTotalArticulos.svc/BuscarInfoArticulo";
    private DecimalFormat df;
    private String TAG = ActivityScannPrincipal.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scann_articulo_layout);
        verificarYPedirPermisosDeCamara();
        df = new DecimalFormat("#0.00");
        DecimalFormatSymbols fmts = new DecimalFormatSymbols();
        fmts.setGroupingSeparator(',');
        df.setGroupingSize(3);
        df.setGroupingUsed(true);
        df.setDecimalFormatSymbols(fmts);
        scanBtn = (Button)findViewById(R.id.btnScanner);
        txtCodigo = (EditText)findViewById(R.id.txtCodigo);
        txtCodigoBarra = (EditText)findViewById(R.id.txtCodigoBarra);
        txtDescripcion = (TextView)findViewById(R.id.txtDescripcion);
        txtExistencia = (TextView)findViewById(R.id.txtExistencia);
        txtPrecio1 = (TextView)findViewById(R.id.txtPrecio1);
        txtPrecio1b = (TextView)findViewById(R.id.txtPrecio1b);
        txtPrecio2 = (TextView)findViewById(R.id.txtPrecio2);
        txtPrecio2b = (TextView)findViewById(R.id.txtPrecio2b);
        txtPrecio3 = (TextView)findViewById(R.id.txtPrecio3);
        txtPrecio3b = (TextView)findViewById(R.id.txtPrecio3b);
        txtProveedor = (TextView)findViewById(R.id.txtProveedor);
        txtBodega = (TextView)findViewById(R.id.txtBodega);
        rgEstado = (RadioGroup) findViewById(R.id.rgEstado);
        rbActivo = (RadioButton) findViewById(R.id.rbActivo);
        rbDesactivado = (RadioButton) findViewById(R.id.rbDesactivo);
        ingresarBtn = (Button) findViewById(R.id.btnIngresar);
        txtPedido = (TextView)findViewById(R.id.txtPedidos);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!permisoCamaraConcedido) {
                    //Toast.makeText(this, "Por favor permite que la app acceda a la cámara", Toast.LENGTH_SHORT).show();
                    permisoSolicitadoDesdeBoton = true;
                    verificarYPedirPermisosDeCamara();
                    return;
                }
                escanear();
            }
        });
                ingresarBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(txtCodigo.getText().toString()) && TextUtils.isEmpty(txtCodigoBarra.getText().toString()) ) {
                            txtCodigo.setError("Debe Ingresar un Código de Artículo o Código de Barra");
                            return;
                        }else if (!TextUtils.isEmpty(txtCodigo.getText().toString())){
                            GetArticuloService(txtCodigo.getText().toString(),"1");
                        }else {
                            GetArticuloService(txtCodigoBarra.getText().toString(),"2");
                        }

                    }
                });
    }
    private void escanear() {
        Intent i = new Intent(this, ActivityScannear.class);
        startActivityForResult(i, CODIGO_INTENT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String codigo = data.getStringExtra("codigo");
                    txtCodigoBarra.setText(codigo);
                    GetArticuloService(txtCodigoBarra.getText().toString(),"2");
                }
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODIGO_PERMISOS_CAMARA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Escanear directamten solo si fue pedido desde el botón
                    if (permisoSolicitadoDesdeBoton) {
                        escanear();
                    }
                    permisoCamaraConcedido = true;
                } else {
                    permisoDeCamaraDenegado();
                }
                break;
        }
    }
    private void verificarYPedirPermisosDeCamara() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            // En caso de que haya dado permisos ponemos la bandera en true
            // y llamar al método
            permisoCamaraConcedido = true;
        } else {
            // Si no, pedimos permisos. Ahora mira onRequestPermissionsResult
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CODIGO_PERMISOS_CAMARA);
        }
    }
    private void permisoDeCamaraDenegado() {
        // Esto se llama cuando el usuario hace click en "Denegar" o
        // cuando lo denegó anteriormente
        Toast.makeText(this, "No puedes escanear si no das permiso", Toast.LENGTH_SHORT).show();
    }
    private void GetArticuloService(String vCodigo,String vTipo)  {

        String encodeUrl = "";
        HttpHandler sh = new HttpHandler();
        String urlString = urlDatosArticulo + "/" + vCodigo + "/" + vTipo;
        try {
            URL Url = new URL(urlString);
            URI uri = new URI(Url.getProtocol(), Url.getUserInfo(), Url.getHost(), Url.getPort(), Url.getPath(), Url.getQuery(), Url.getRef());
            encodeUrl = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            String jsonStr = sh.makeServiceCall(encodeUrl);
            if (jsonStr == null) {
                new Funciones().SendMail("Ha ocurrido un error al obtener los datos del artículo, Respuesta nula GET", variables_publicas.info+urlString, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                txtCodigo.setText("");
                txtCodigoBarra.setText("");
                txtDescripcion.setText("Error al obtener los datos");
                rbActivo.setChecked(false);
                txtExistencia.setText("");
                txtPedido.setText("");
                txtPrecio1.setText("");
                txtPrecio1b.setText("");
                txtPrecio2.setText("");
                txtPrecio2b.setText("");
                txtPrecio3.setText("");
                txtPrecio3b.setText("");
                txtProveedor.setText("");
                txtBodega.setText("");
            } else {
                Log.e(TAG, "Response from url: " + jsonStr);

                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray DatoArticulo = jsonObj.getJSONArray("BuscarInfoArticuloResult");
                for (int i = 0; i < DatoArticulo.length(); i++) {
                    JSONObject c = DatoArticulo.getJSONObject(i);

                    String vCodigoArticulo = c.getString("CODIGO");
                    String vCodigoBarra = c.getString("BARRA");
                    String vDescripion= c.getString("DESCRIPCION");
                    String vEstado = c.getString("ESTADO");
                    double vExistencia = Double.parseDouble(c.getString("EXISTENCIA"));
                    double vPrecio1 = Double.parseDouble(c.getString("PRECIO3"));
                    double vPrecio1b = Double.parseDouble(c.getString("PRECIO3"));
                    double vPrecio2 = Double.parseDouble(c.getString("PRECIO4"));
                    double vPrecio2b = Double.parseDouble(c.getString("CAMBIOP4"));
                    double vPrecio3 = Double.parseDouble(c.getString("PRECIO6"));
                    double vPrecio3b = Double.parseDouble(c.getString("CAMBIOP6"));
                    double vPedido = Double.parseDouble(c.getString("EN_PEDIDO"));
                    String vProveedor = c.getString("PROVEEDOR");
                    String vBodega = c.getString("BODEGA");

                    txtCodigo.setText(vCodigoArticulo);
                    txtCodigoBarra.setText(vCodigoBarra);
                    txtDescripcion.setText(vDescripion);
                    if (vEstado.equals("INACTIVO")){
                        rbDesactivado.setChecked(true);
                    }else{
                        rbActivo.setChecked(true);
                    }
                    txtExistencia.setText(df.format(vExistencia));
                    txtPedido.setText(df.format(vPedido));
                    txtPrecio1.setText(df.format(vPrecio1));
                    txtPrecio1b.setText(df.format(vPrecio1b));
                    txtPrecio2.setText(df.format(vPrecio2));
                    txtPrecio2b.setText(df.format(vPrecio2b));
                    txtPrecio3.setText(df.format(vPrecio3));
                    txtPrecio3b.setText(df.format(vPrecio3b));
                    txtProveedor.setText(vProveedor);
                    txtBodega.setText(vBodega);
                }
                if (DatoArticulo.length()==0){
                    txtCodigo.setText("");
                    txtCodigoBarra.setText("");
                    txtDescripcion.setText("PRODUCTO NO ENCONTRADO");
                    rbActivo.setChecked(false);
                    txtExistencia.setText("");
                    txtPedido.setText("");
                    txtPrecio1.setText("");
                    txtPrecio1b.setText("");
                    txtPrecio2.setText("");
                    txtPrecio2b.setText("");
                    txtPrecio3.setText("");
                    txtPrecio3b.setText("");
                    txtProveedor.setText("");
                    txtBodega.setText("");
                }
            }
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al obtener los datos del artículo, Excepcion controlada", variables_publicas.info+ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);

        }
    }
}

