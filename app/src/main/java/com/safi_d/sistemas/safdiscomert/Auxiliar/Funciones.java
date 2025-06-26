package com.safi_d.sistemas.safdiscomert.Auxiliar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Spinner;

import com.safi_d.sistemas.safdiscomert.R;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sistemas on 24/5/2017.
 */

public class Funciones {
    private static boolean connectionOK = false;
    private static String fechaActual = "";

    public static String Codificar(String text) {
        return text.replace("+", "(plus)")
                .replace("#", "(hash)")
                .replace(".", "(dot)")
                .replace("@", "(at)")
                .replace("*", "(star)")
                .replace("-", "(minus)")
                .replace(",", "(comma)")
                .replace(":", "(semicolon)")
                .replace("!", "(exclamation)")
                .replace("?", "(question)")
                .replace("/", "(slash)")
                .replace("\\", "(backslash)")
                .replace("'", "(sigleQuote)")
                .replace("\"", "(doubleQuote)")
                .replace("~", "(tilde)");
    }

    public static String Decodificar(String text) {
        return text.replace("(plus)","+")
                .replace("(hash)","#")
                .replace("(dot)",".")
                .replace("(at)","@")
                .replace("(star)","*")
                .replace("(minus)","-")
                .replace("(comma)",",")
                .replace("(semicolon)",":")
                .replace("(exclamation)","!")
                .replace("(question)","?")
                .replace("(slash)","/")
                .replace("(backslash)","\\")
                .replace("(sigleQuote)","'")
                .replace("(doubleQuote)","\"")
                .replace("(tilde)","~");
    }

    private static Pattern p = Pattern.compile("\\((\\d+)([+-]\\d{2})(\\d{2})\\)");

    public static String  ObtenerIniDpto (String  vdepto) {
        String vIni="NI";
        if (vdepto.equals("BOACO")){
            vIni="BO";
        }else if (vdepto.equals("CARAZO")){
            vIni="CZ";
        }else if (vdepto.equals("CHINANDEGA")){
            vIni="CH";
        }else if (vdepto.equals("CHONTALES")){
            vIni="CT";
        }else if (vdepto.equals("ESTELI")){
            vIni="ES";
        }else if (vdepto.equals("GRANADA")){
            vIni="GR";
        }else if (vdepto.equals("JINOTEGA")){
            vIni="JI";
        }else if (vdepto.equals("LEON")){
            vIni="LE";
        }else if (vdepto.equals("MADRIZ")){
            vIni="MZ";
        }else if (vdepto.equals("MANAGUA")){
            vIni="MA";
        }else if (vdepto.equals("MASAYA")){
            vIni="MY";
        }else if (vdepto.equals("MATAGALPA")){
            vIni="MT";
        }else if (vdepto.equals("NUEVA SEGOVIA")){
            vIni="NS";
        }else if (vdepto.equals("RAAN")){
            vIni="RN";
        }else if (vdepto.equals("RAAS")){
            vIni="RS";
        }else if (vdepto.equals("RIO SAN JUAN")){
            vIni="RJ";
        }else if (vdepto.equals("RIVAS")){
            vIni="RI";
        }else {
            vIni="NI";
        }

        return vIni;
    }
    public static Date jd2d(String jsonDateString) {
        Matcher m = p.matcher(jsonDateString);
        if (m.find()) {
            long millis = Long.parseLong(m.group(1));
            long offsetHours = Long.parseLong(m.group(2));
            long offsetMinutes = Long.parseLong(m.group(3));
            if (offsetHours < 0) offsetMinutes *= -1;
            return new Date(
                    millis
                            + offsetHours * 60l * 60l * 1000l
                            + offsetMinutes * 60l * 1000l
            );
        }
        return null;
    }

    public static void MensajeAviso(final Context context, String texto) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
        dlgAlert.setMessage(texto);
        dlgAlert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public static boolean checkInternetConnection(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return isInternetAvailable();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return isInternetAvailable();
            }
        } else {
            return false;
        }
        return false;
    }

    public static int getIndexSpinner(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
            }
        }
        return index;
    }


    private static boolean isInternetAvailable() {
        try {
            HttpURLConnection conn=null;
            try {
                connectionOK = false;
                URL url = new URL("http://www.google.com.ni");
                conn= (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setUseCaches(false);
                conn.connect();
                InputStream is = conn.getInputStream();
                if (is != null) {
                    connectionOK = true;
                }
            } catch (Exception e) {

                connectionOK = false;
            }finally {
                conn.disconnect();
            }
            return connectionOK;
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            return false;
        }
    }



/*
    public static boolean TestInternetConectivity() {
        HttpURLConnection conn=null;
        try {
            URL url = new URL("http://www.google.com.ni");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setUseCaches(false);
            conn.connect();
            InputStream is = conn.getInputStream();
            if (is != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
        finally {
            conn.disconnect();
        }
    }*/

    public static boolean TestServerConectivity() {
        /*HttpURLConnection conn=null;
        try {
            URL url = new URL(variables_publicas.direccionIp+"/ServicioPedidos.svc/ObtenerInventarioArticulo/01-3-001");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setUseCaches(false);
            conn.connect();
            InputStream is = conn.getInputStream();
            if (is != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
        finally {
            conn.disconnect();
        }*/
        HttpURLConnection conn=null;
        try {
            URL url = new URL(variables_publicas.direccionIp+"/ServicioTotalArticulos.svc/BuscarArticulo/1/0");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(200000);
            conn.setReadTimeout(200000);
            conn.setUseCaches(false);
            conn.connect();
            InputStream is = conn.getInputStream();
            int responceCode = conn.getResponseCode();
            if (is != null) {
                if (responceCode==200){
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
        finally {
            conn.disconnect();
        }
    }

    public static boolean TestServerConectivity2() {
        HttpURLConnection conn=null;
        try {
            URL url = new URL(variables_publicas.direccionIp+"/ServicioPedidos.svc/ObtenerInventarioArticulo/4000-01-01-01-001");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setUseCaches(false);
            conn.connect();
            InputStream is = conn.getInputStream();
            int responceCode = conn.getResponseCode();
            if (is != null) {
                if (responceCode==200){
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
        finally {
            conn.disconnect();
        }
    }
    public static String getDatePhone() {
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formatteDate = df.format(date);
        return formatteDate;
    }

    public static Date StringToDate(String sDate){
        String dtStart = sDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dtStart);
           return  date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DateToString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return  sdf.format(date);
    }

    public boolean SendMail(String subject, String body, String from, String recipients) {

        try {
            GMailSender sender = new GMailSender("dlunasistemas@gmail.com", "ezssfzadjjytzopt");
            sender.sendMail(subject,
                    body,
                    from,
                    recipients);
            return true;
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
            return false;
        }

    }

    public static String GetLocalDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        variables_publicas.FechaActual = sdf.format(c.getTime());
        return variables_publicas.FechaActual;
    }


    /*Esta funcion sirve para obtener la hora de internet*/
    public static String GetDateTime() {
        try {
            String timeServer = "0.north-america.pool.ntp.org";
            Calendar cal = Calendar.getInstance();

            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = InetAddress.getByName(timeServer);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            long returnTime = timeInfo.getReturnTime();
            cal.setTimeInMillis(returnTime);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            variables_publicas.FechaActual = sdf.format(cal.getTime());
            fechaActual = variables_publicas.FechaActual;
            return fechaActual;
        } catch (Exception e) {
            Log.e("Error:", e.getMessage());
            return GetLocalDateTime();
        }

    }

    public static boolean TestInternetConectivity() {
        InetAddress inetAddress = null;
        int timeOut=5000;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress!=null && !inetAddress.equals("");
    }

   /* public static boolean TestServerConectivity() {
        InetAddress inetAddress = null;
        int timeOut=5000;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName(variables_publicas.direccionIp+"/ServicioPedidos.svc/ObtenerInventarioArticulo/4000-01-01-01-001");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress!=null && !inetAddress.equals("");
    }*/
   private static final String[] UNIDADES = {"", "un ", "dos ", "tres ", "cuatro ", "cinco ", "seis ", "siete ", "ocho ", "nueve "};
    private static final String[] DECENAS = {"diez ", "once ", "doce ", "trece ", "catorce ", "quince ", "dieciseis ",
            "diecisiete ", "dieciocho ", "diecinueve", "veinte ", "treinta ", "cuarenta ",
            "cincuenta ", "sesenta ", "setenta ", "ochenta ", "noventa "};
    private static final String[] CENTENAS = {"", "ciento ", "doscientos ", "trecientos ", "cuatrocientos ", "quinientos ", "seiscientos ",
            "setecientos ", "ochocientos ", "novecientos "};

    public static String Convertir(String numero, boolean mayusculas) {
       String literal = "";
       String parte_decimal;
       //si el numero utiliza (.) en lugar de (,) -> se reemplaza
       numero = numero.replace(",", ".");
       //si el numero no tiene parte decimal, se le agrega ,00
       if(numero.indexOf(".")==-1){
           numero = numero + ".00";
       }
       //se valida formato de entrada -> 0,00 y 999 999 999,00
       if (Pattern.matches("\\d{1,9}.\\d{1,2}", numero)) {
           //se divide el numero 0000000,00 -> entero y decimal
           String[] Num = numero.split("\\.");
           //de da formato al numero decimal
           parte_decimal ="con " +  Num[1] + "/100 Cordobas.";
           //se convierte el numero a literal
           if (Integer.parseInt(Num[0]) == 0) {//si el valor es cero
               literal = "cero ";
           } else if (Integer.parseInt(Num[0]) > 999999) {//si es millon
               literal = getMillones(Num[0]);
           } else if (Integer.parseInt(Num[0]) > 999) {//si es miles
               literal = getMiles(Num[0]);
           } else if (Integer.parseInt(Num[0]) > 99) {//si es centena
               literal = getCentenas(Num[0]);
           } else if (Integer.parseInt(Num[0]) > 9) {//si es decena
               literal = getDecenas(Num[0]);
           } else {//sino unidades -> 9
               literal = getUnidades(Num[0]);
           }
           //devuelve el resultado en mayusculas o minusculas
           if (mayusculas) {
               return (literal + parte_decimal).toUpperCase();
           } else {
               return (literal + parte_decimal);
           }
       } else {//error, no se puede convertir
           return literal = null;
       }
   }

    /* funciones para convertir los numeros a literales */

    private static String getUnidades(String numero) {// 1 - 9
        //si tuviera algun 0 antes se lo quita -> 09 = 9 o 009=9
        String num = numero.substring(numero.length() - 1);
        return UNIDADES[Integer.parseInt(num)];
    }

    private static String getDecenas(String num) {// 99
        int n = Integer.parseInt(num);
        if (n < 10) {//para casos como -> 01 - 09
            return getUnidades(num);
        } else if (n > 19) {//para 20...99
            String u = getUnidades(num);
            if (u.equals("")) { //para 20,30,40,50,60,70,80,90
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8];
            } else {
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8] + "y " + u;
            }
        } else {//numeros entre 11 y 19
            return DECENAS[n - 10];
        }
    }

    private static String getCentenas(String num) {// 999 o 099
        if( Integer.parseInt(num)>99 ){//es centena
            if (Integer.parseInt(num) == 100) {//caso especial
                return " cien ";
            } else {
                return CENTENAS[Integer.parseInt(num.substring(0, 1))] + getDecenas(num.substring(1));
            }
        }else{//por Ej. 099
            //se quita el 0 antes de convertir a decenas
            return getDecenas(Integer.parseInt(num)+"");
        }
    }

    private static String getMiles(String numero) {// 999 999
        //obtiene las centenas
        String c = numero.substring(numero.length() - 3);
        //obtiene los miles
        String m = numero.substring(0, numero.length() - 3);
        String n="";
        //se comprueba que miles tenga valor entero
        if (Integer.parseInt(m) > 0) {
            n = getCentenas(m);
            return n + "mil " + getCentenas(c);
        } else {
            return "" + getCentenas(c);
        }

    }

    private static String getMillones(String numero) { //000 000 000
        //se obtiene los miles
        String miles = numero.substring(numero.length() - 6);
        //se obtiene los millones
        String millon = numero.substring(0, numero.length() - 6);
        String n = "";
        if(millon.length()>1){
            n = getCentenas(millon) + "millones ";
        }else{
            n = getUnidades(millon) + "millon ";
        }
        return n + getMiles(miles);
    }
}
