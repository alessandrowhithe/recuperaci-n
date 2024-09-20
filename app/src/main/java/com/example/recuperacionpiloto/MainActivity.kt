package com.example.recuperacionpiloto

import Modelo.Connection
import Modelo.tbPiloto
import RecyclerViewhelper.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var adaptador: Adaptador
    private lateinit var rvPiloto: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtNombre = findViewById<EditText>(R.id.Nombre_Piloto)
        val txtEdad = findViewById<EditText>(R.id.Edad_Piloto)
        val txtPeso = findViewById<EditText>(R.id.Peso_Piloto)
        val txtCorreo = findViewById<EditText>(R.id.Correo_Piloto)
        val btnAgregar = findViewById<Button>(R.id.btn_agregarTick)
        rvPiloto = findViewById(R.id.rcvElementos)

        rvPiloto.layoutManager = LinearLayoutManager(this)

        // Función para obtener los pilotos de la base de datos
        fun obtenerPilotos(): List<tbPiloto> {
            val listaPiloto = mutableListOf<tbPiloto>()
            try {
                val objConexion = Connection().cadenaConexion()
                val statement = objConexion?.createStatement()
                val resultset = statement?.executeQuery("SELECT * FROM tbPiloto") ?: return listaPiloto

                while (resultset.next()) {
                    val uuid = resultset.getString("UUID_Piloto")
                    val nombre = resultset.getString("Nombre_Piloto")
                    val edad = resultset.getInt("Edad_Piloto")
                    val peso = resultset.getDouble("Peso_Piloto")
                    val correo = resultset.getString("Correo_Piloto")

                    val piloto = tbPiloto(uuid, nombre, edad, peso, correo)
                    listaPiloto.add(piloto)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Manejo de errores
            }
            return listaPiloto
        }

        // Cargar los pilotos y configurar el adaptador al iniciar
        CoroutineScope(Dispatchers.IO).launch {
            val pilotos = obtenerPilotos()
            withContext(Dispatchers.Main) {
                adaptador = Adaptador(pilotos)
                rvPiloto.adapter = adaptador
            }
        }

        // Acción al presionar el botón de agregar
        btnAgregar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = Connection().cadenaConexion()
                val addPiloto = objConexion?.prepareStatement(
                    "INSERT INTO tbPiloto (UUID_Piloto, Nombre_Piloto, Edad_Piloto, Peso_Piloto, Correo_Piloto) VALUES (?,?,?,?,?)"
                ) ?: return@launch

                addPiloto.setString(1, UUID.randomUUID().toString())
                addPiloto.setString(2, txtNombre.text.toString())
                addPiloto.setInt(3, txtEdad.text.toString().toInt())
                addPiloto.setDouble(4, txtPeso.text.toString().toDouble())
                addPiloto.setString(5, txtCorreo.text.toString())

                addPiloto.executeUpdate()

                // Después de agregar, actualizar el RecyclerView
                val nuevosPilotos = obtenerPilotos()
                withContext(Dispatchers.Main) {
                    adaptador = Adaptador(nuevosPilotos)
                    rvPiloto.adapter = adaptador
                }
            }
        }
    }
}
