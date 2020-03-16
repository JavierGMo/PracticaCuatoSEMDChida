package com.mora.gonzalez.javier.softcuatro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mora.gonzalez.javier.softcuatro.bd.PagosClientes;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RadioGroup opcionesPagos;
    private EditText correoTxt, noTarjetaTxt, nombreCompletoTxt, montoTxt;
    private Button btnPagarJ;
    private LinearLayout layoutALlenar;
    private ListView listaDeLosPagos;
    private SQLiteDatabase bdPagos;
    private ArrayList<String[]> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PagosClientes pagosXClientes = new PagosClientes(this, "PagosChidos", null, 1);
        //Radio Group
        opcionesPagos = findViewById(R.id.radioGroupPagos);
        //ListView
        listaDeLosPagos = findViewById(R.id.listaPagos);
        //Layout que se tiene que llenar
        layoutALlenar = findViewById(R.id.linearLayoutCamposDinamicos);
        //Edit Texts de pagos
        correoTxt = findViewById(R.id.cuentaPayPal);
        noTarjetaTxt = findViewById(R.id.noCuentaBanco);
        nombreCompletoTxt = findViewById(R.id.nombreCompleto);
        montoTxt = findViewById(R.id.montoAPagar);
        //Boton para pagar
        btnPagarJ = findViewById(R.id.buttonDepositar);
        //Iniciar la base para los pagos
        bdPagos = pagosXClientes.getWritableDatabase();

        /*Opciones de radioGroup*/

        opcionesPagos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int check) {
                if(check == R.id.radioPagoBanco){
                    //Limpiar los campos y mostrarlos
                    limpiarCampos();
                    mostrarCampos(View.GONE, View.VISIBLE);
                    listaDeLosPagos.setVisibility(View.GONE);


                    Toast.makeText(MainActivity.this, "Pago en banco", Toast.LENGTH_SHORT).show();
                }else if(check == R.id.radioMostrarPagoBanco){
                    limpiarCampos();
                    desparecerCampos();

                    Cursor c = bdPagos.rawQuery("select numcuenta, nombrecompleto, monto from PagosBanco", null);
                    if(c != null){
                        if(c.moveToFirst()){
                            data = new ArrayList<>();
                            do{
                                data.add(
                                        new String[]{
                                                c.getString(0),
                                                c.getString(1),
                                                c.getString(2)
                                        }
                                );
                            }while (c.moveToNext());
                        }else{
                            data = null;
                        }
                        c.close();
                    }

                    if(data != null){
                        AdaptadorPagos adaptadorPagos = new AdaptadorPagos(MainActivity.this, R.layout.campos_pagos, data);
                        listaDeLosPagos.setAdapter(adaptadorPagos);
                        listaDeLosPagos.setVisibility(View.VISIBLE);
                    }

                    Toast.makeText(MainActivity.this, "Pagos directos de bancos", Toast.LENGTH_SHORT).show();
                }else if(check == R.id.radioPagoPayPal){
                    //Limpiar los campos y mostrarlos
                    limpiarCampos();
                    mostrarCampos(View.VISIBLE, View.GONE);
                    listaDeLosPagos.setVisibility(View.GONE);

                    Toast.makeText(MainActivity.this, "Pago en PayPal", Toast.LENGTH_SHORT).show();
                }else if(check == R.id.radioMostarPagoPayPal){
                    limpiarCampos();
                    desparecerCampos();

                    Cursor c = bdPagos.rawQuery("select correo, nombrecompleto, monto from PagosPayPal", null);
                    if(c != null){
                        if(c.moveToFirst()){
                            data = new ArrayList<>();
                            do{
                                data.add(
                                        new String[]{
                                                c.getString(0),
                                                c.getString(1),
                                                c.getString(2)
                                        }
                                );
                            }while (c.moveToNext());
                        }else{
                            data = null;
                        }
                        c.close();
                    }

                    if(data != null){
                        AdaptadorPagos adaptadorPagos = new AdaptadorPagos(MainActivity.this, R.layout.campos_pagos, data);
                        listaDeLosPagos.setAdapter(adaptadorPagos);
                        listaDeLosPagos.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(MainActivity.this, "Pagos en PayPal", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Evento para el boton de pago
        btnPagarJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noTarjetaTxt.getText() != null && !noTarjetaTxt.getText().toString().isEmpty()) {
                    ContentValues cv = new ContentValues();
                    cv.put("numcuenta", noTarjetaTxt.getText().toString());
                    cv.put("nombrecompleto", nombreCompletoTxt.getText().toString());
                    cv.put("monto", montoTxt.getText().toString());
                    System.out.println(noTarjetaTxt.getText().toString());
                    //pagosEnelBanc
                    bdPagos.insert("PagosBanco", null, cv);
                    Toast.makeText(MainActivity.this, "Hizo un pago", Toast.LENGTH_SHORT).show();

                } else if (correoTxt.getText() != null && !correoTxt.getText().toString().isEmpty()) {
                    ContentValues cv = new ContentValues();
                    cv.put("correo", correoTxt.getText().toString());
                    cv.put("nombrecompleto", nombreCompletoTxt.getText().toString());
                    cv.put("monto", montoTxt.getText().toString());
                    System.out.println(noTarjetaTxt.getText().toString());
                    bdPagos.insert("PagosPayPal", null, cv);
                    Toast.makeText(MainActivity.this, "Hizo un pago en PayPal", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void limpiarCampos(){
        this.correoTxt.setText("");
        this.noTarjetaTxt.setText("");
        this.nombreCompletoTxt.setText("");
        this.montoTxt.setText("");
    }
    private void mostrarCampos(int visibilidadCero, int visibilidadUno){

        correoTxt.setVisibility(visibilidadCero);
        noTarjetaTxt.setVisibility(visibilidadUno);
        nombreCompletoTxt.setVisibility(View.VISIBLE);
        montoTxt.setVisibility(View.VISIBLE);
        btnPagarJ.setVisibility(View.VISIBLE);
    }
    private void desparecerCampos(){
        correoTxt.setVisibility(View.GONE);
        noTarjetaTxt.setVisibility(View.GONE);
        nombreCompletoTxt.setVisibility(View.GONE);
        montoTxt.setVisibility(View.GONE);
        btnPagarJ.setVisibility(View.GONE);
    }
}
