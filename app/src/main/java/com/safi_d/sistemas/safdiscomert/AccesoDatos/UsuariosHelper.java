package com.safi_d.sistemas.safdiscomert.AccesoDatos;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.safi_d.sistemas.safdiscomert.Auxiliar.variables_publicas;
import com.safi_d.sistemas.safdiscomert.Entidades.Ruta;
import com.safi_d.sistemas.safdiscomert.Entidades.Usuario;
import com.safi_d.sistemas.safdiscomert.Entidades.DiasCierre;

import java.util.ArrayList;
import java.util.List;

public class UsuariosHelper {

    private SQLiteDatabase database;

    public  UsuariosHelper(SQLiteDatabase db){
        database = db;
    }
    public boolean GuardarUsuario(String Codigo, String nombre, String Usuario,
                                   String Contrasenia, String Tipo, String Ruta,
                                      String Canal,String TasaCambio,String RutaForanea,String FechaActualiza,String EsVendedor,String Empresa_ID) {

        long rows =0;
        ContentValues contentValues = new ContentValues();
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Codigo, Codigo);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Nombre, nombre);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Usuario, Usuario);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Contrasenia, Contrasenia);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Tipo, Tipo);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Ruta, Ruta);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_Canal, Canal);
         contentValues.put(variables_publicas.USUARIOS_COLUMN_TasaCambio, TasaCambio);
        contentValues.put(variables_publicas.USUARIOS_COLUMN_RutaForanea, RutaForanea);
        contentValues.put(variables_publicas.USUARIOS_COLUMN_FechaActualiza, FechaActualiza);
        contentValues.put(variables_publicas.USUARIOS_COLUMN_EsVendedor, EsVendedor);
        contentValues.put(variables_publicas.USUARIOS_COLUMN_Empresa_ID, Empresa_ID);
         long inserted=database.insert(variables_publicas.TABLE_USUARIOS, null, contentValues);
        if(inserted!=-1)
            return true;
        else
            return false;
    }
    public Usuario BuscarUsuarios(String Usuario,String Contrasenia) {
        Usuario usuario=null;
        String selectQuery="SELECT * FROM " + variables_publicas.TABLE_USUARIOS
                + " WHERE UPPER("+variables_publicas.USUARIOS_COLUMN_Usuario +") = UPPER('"+Usuario+"') AND "+ variables_publicas.USUARIOS_COLUMN_Contrasenia+" = '"+Contrasenia+"'";
        Cursor c= database.rawQuery(selectQuery , null);
        if (c.moveToFirst()) {
            do {
                usuario = (new Usuario(c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Codigo)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Nombre)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Usuario)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Contrasenia)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Tipo)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Ruta)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Canal)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_TasaCambio)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_RutaForanea)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_FechaActualiza)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_EsVendedor)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Empresa_ID))
                ));
            } while (c.moveToNext());
        }
        c.close();
        return usuario;
    }
    @SuppressLint("Range")
    public int BuscarCodigoUsuarios(String Usuario) {
        String selectQuery="SELECT * FROM " + variables_publicas.TABLE_USUARIOS
                + " WHERE UPPER("+variables_publicas.USUARIOS_COLUMN_Usuario +") = UPPER('"+Usuario+"')";
        int vcodusuario=0;
        Cursor
                c= database.rawQuery(selectQuery , null);
        if (c.moveToFirst()) {
            do {
                vcodusuario= Integer.parseInt(c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Codigo)));
            } while (c.moveToNext());
        }
        c.close();
        return vcodusuario;
    }
    public Usuario BuscarUltimoUsuario() {
        Usuario usuario=null;
        String selectQuery="SELECT * FROM " + variables_publicas.TABLE_USUARIOS+" LIMIT 1";

        Cursor c= database.rawQuery(selectQuery , null);
        if (c.moveToFirst()) {
            do {
                usuario = (new Usuario(c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Codigo)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Nombre)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Usuario)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Contrasenia)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Tipo)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Ruta)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Canal)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_TasaCambio)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_RutaForanea)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_FechaActualiza)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_EsVendedor)),
                        c.getString(c.getColumnIndex(variables_publicas.USUARIOS_COLUMN_Empresa_ID))
                ));
            } while (c.moveToNext());
        }
        c.close();
        return usuario;
    }
    public  void EliminaUsuarios() {
        database.execSQL("DELETE FROM "+variables_publicas.TABLE_USUARIOS+";");
        Log.d("Usuario_elimina", "Datos eliminados");
    }

    public void GuardarDiasCierre(String diaInicio,
                             String diaFin,
                             String horaInicio,
                                String horaFin,String fechaInicio, String fechaFin) {

        long rows =0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(variables_publicas.DIASCIERRE_COLUMN_DiaInicio, diaInicio);
        contentValues.put(variables_publicas.DIASCIERRE_COLUMN_DiaFin, diaFin);
        contentValues.put(variables_publicas.DIASCIERRE_COLUMN_HoraInicio, horaInicio);
        contentValues.put(variables_publicas.DIASCIERRE_COLUMN_HoraFin, horaFin);
        contentValues.put(variables_publicas.DIASCIERRE_COLUMN_FechaInicio,fechaInicio);
        contentValues.put(variables_publicas.DIASCIERRE_COLUMN_FechaFin, fechaFin);
        database.insert(variables_publicas.TABLE_DIASCIERRE, null, contentValues);
    }

    public  void EliminaDiasCierre() {
        database.execSQL("DELETE FROM "+variables_publicas.TABLE_DIASCIERRE+";");
        Log.d("DiasCierre_elimina", "Datos eliminados");
    }


    public DiasCierre ObtenerDiasCierre() {
        DiasCierre diacierre=null;
        String selectQuery="SELECT * FROM " + variables_publicas.TABLE_DIASCIERRE + ";";
        Cursor c= database.rawQuery(selectQuery , null);
        if (c.moveToFirst()) {
            do {
                diacierre = (new DiasCierre(c.getString(c.getColumnIndex(variables_publicas.DIASCIERRE_COLUMN_DiaInicio)),
                        c.getString(c.getColumnIndex(variables_publicas.DIASCIERRE_COLUMN_DiaFin)),
                        c.getString(c.getColumnIndex(variables_publicas.DIASCIERRE_COLUMN_HoraInicio)),
                        c.getString(c.getColumnIndex(variables_publicas.DIASCIERRE_COLUMN_HoraFin)),
                        c.getString(c.getColumnIndex(variables_publicas.DIASCIERRE_COLUMN_FechaInicio)),
                        c.getString(c.getColumnIndex(variables_publicas.DIASCIERRE_COLUMN_FechaFin))
                ));
            } while (c.moveToNext());
        }
        c.close();
        return  diacierre;
    }
    }
