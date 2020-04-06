package com.mora.gonzalez.javier.softcuatro.archivo;

import android.content.Context;
import android.os.Environment;
import android.provider.DocumentsContract;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mora.gonzalez.javier.softcuatro.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ArchivoPDF {
    private final String NOMBRE_DIRECTORIO = "pdfs";
    private String nombreArchivo;
    private String tipoPago;
    private int numeroRegistros;
    private ArrayList<String[]> dataSQL;
    private Context context;
    public ArchivoPDF(){

    }
    public ArchivoPDF(Context context, String nombreArchivo, String tipoPago, int numeroRegistros){
        this.context = context;
        this.nombreArchivo = nombreArchivo;
        this.tipoPago = tipoPago;
        this.numeroRegistros = numeroRegistros;
    }
    public boolean generarPDF(){
        boolean var = false;
        Document documento = new Document();
        try{
            File file = crearArchivo(this.nombreArchivo);
            if(file != null && file.getAbsolutePath() != null){
                FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());
                PdfWriter writer = PdfWriter.getInstance(documento, ficheroPDF);
                documento.open();
                documento.add(new Paragraph("Pagos: "+ tipoPago + "\n\n"));
                for (int i = 0; i < numeroRegistros; i++) {
                    documento.add(new Paragraph("ID pago: " + this.dataSQL.get(i)[0]));
                    documento.add(new Paragraph("Cuenta: " + this.dataSQL.get(i)[1]));
                    documento.add(new Paragraph("Nombre: " + this.dataSQL.get(i)[2]));
                    documento.add(new Paragraph("monto" + this.dataSQL.get(i)[3]));
                    documento.add(new Paragraph("\n\n\n"));
                }
                var = true;
            }

        }catch (DocumentException | IOException e){
            e.printStackTrace();
        } finally {
            documento.close();
        }
        return var;
    }
    public File crearArchivo(String nombreFichero){
        File ruta = getRuta();
        File fichero = null;
        if (ruta != null) {
            fichero = new File(ruta, nombreFichero);
        }
        return fichero;
    }
    public File getRuta(){
        File ruta = null;
        boolean comprobacion = true;
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            //this.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
            ruta = new File(
                    this.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
                    NOMBRE_DIRECTORIO
            );
            if(ruta!=null){
                comprobacion = ruta.mkdir();
                if(!comprobacion){
                    if (!ruta.exists()) {
                        return null;
                    }
                }
            }
        }
        return ruta;
    }
    public void setNombreArchivo(String nombreArchivo){
        this.nombreArchivo = nombreArchivo;
    }
    public  String getNombreArchivo(){
        return this.nombreArchivo;
    }
    public void setDataSQL(ArrayList<String[]>dataSQL){
        this.dataSQL = dataSQL;
    }
    public ArrayList<String[]> getDataSQL(){
        return this.dataSQL;
    }
}
