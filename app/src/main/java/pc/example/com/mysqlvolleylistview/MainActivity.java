package pc.example.com.mysqlvolleylistview;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String URL_REGISTRA = "http://192.168.1.7/hotelejemplo/registrarReserva.php?idr=NULL&NHab=";
    private static final String URL_CONSULTA = "http://192.168.1.7/hotelejemplo/consultarReserva.php";

    // fecha
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int day, month, year;

    ArrayAdapter<String> adaptador;
    EditText etFechaEntrada, etFechaSalida, etHabitacion;
    Button btnGuardar, btnCargar;
    ImageButton btn1, btn2;
    ListView listaResultado;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etFechaEntrada = findViewById(R.id.etFechaEntrada);
        etFechaSalida = findViewById(R.id.etFechaSalida);
        etHabitacion = findViewById(R.id.etHabitacion);
        btnGuardar = findViewById(R.id.btnSave);
        btnCargar = findViewById(R.id.btnLoad);
        listaResultado = findViewById(R.id.lvLista);
        refreshLayout = findViewById(R.id.swipe_refresh_list);

        EnviarRecibirDatos(URL_CONSULTA, 0);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String hab = etHabitacion.getText().toString();
                String fe = etFechaEntrada.getText().toString();
                String fs = etFechaSalida.getText().toString();

                if(!hab.isEmpty() &&  !fe.isEmpty() &&  !fs.isEmpty()) {

                    String registro = URL_REGISTRA + etHabitacion.getText()+"&fe="+etFechaEntrada.getText()+"&fs="+etFechaSalida.getText();
                    Log.i("log->", registro);
                    EnviarRecibirDatos(registro, 1);

                    // limpia campos de entrada

                    etHabitacion.setText("");
                    etFechaEntrada.setText("");
                    etFechaSalida.setText("");

                }  else {
                    Toast.makeText(getApplicationContext(), "Debe ingresar los datos solicitados", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("log->", URL_CONSULTA);
                EnviarRecibirDatos(URL_CONSULTA, 0);

                // limpia campos de entrada

                etHabitacion.setText("");
                etFechaEntrada.setText("");
                etFechaSalida.setText("");

            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Log.i("log->", URL_CONSULTA);
                EnviarRecibirDatos(URL_CONSULTA, 0);
                refreshLayout.setRefreshing(false);
            }
        });

    }

    public void EnviarRecibirDatos(String URL, int opt){

        final int option = opt;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest  = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");

                if (response.length()>0){
                    try {
                        JSONArray ja = new JSONArray(response);
                        CargarListView(ja);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if ( option == 0) {
                    adaptador = null;
                    listaResultado.setAdapter(adaptador);
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                }
        });

        queue.add(stringRequest);

    }

    public void CargarListView(JSONArray ja){

        ArrayList<String> lista = new ArrayList<>();

        for(int i=0;i<ja.length();i+=4){

            try {

                lista.add(ja.getString(i)+"   "+ja.getString(i+1)+"   "+ja.getString(i+2)+"   "+ja.getString(i+3));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lista);
        listaResultado.setAdapter(adaptador);

    }

    protected void eventoClick(View view) {
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        switch (view.getId()) {
            case R.id.btn1:

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        etFechaEntrada.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                        datePicker.setMinDate(System.currentTimeMillis());
                    }
                }, day, month, year);
                datePickerDialog.show();

                break;

            case R.id.btn2:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        etFechaSalida.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, day, month, year);
                datePickerDialog2.show();
                break;
        }


    }

}

