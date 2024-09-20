package RecyclerViewhelper

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import Modelo.Connection
import Modelo.tbPiloto
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.recuperacionpiloto.R

class Adaptador(var datos: List<tbPiloto>) : RecyclerView.Adapter<Adaptador.ViewHolder>() {

    class ViewHolder(val vista: View) : RecyclerView.ViewHolder(vista) {
        val txtNombreCard: TextView = vista.findViewById(R.id.txtNombreCard)
        val imgDelete: ImageView = vista.findViewById(R.id.imgDelete)
        val imgEdit: ImageView = vista.findViewById(R.id.imgEdit)
    }

    fun eliminarPiloto(nombrePiloto: String, position: Int) {
        val listaDatos = datos.toMutableList()
        listaDatos.removeAt(position)
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = Connection().cadenaConexion()
            val deletePiloto = objConexion?.prepareStatement("DELETE FROM tbPiloto WHERE Nombre_Piloto = ?")!!
            deletePiloto.setString(1, nombrePiloto)
            deletePiloto.executeUpdate()
            val commit = objConexion.prepareStatement("COMMIT")!!
            commit.executeUpdate()
        }
        datos = listaDatos.toList()
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun editarPiloto(nombrePiloto: String, UUID_Piloto: String, edad: Int, peso: Double, correo: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = Connection().cadenaConexion()
            val updatePiloto = objConexion?.prepareStatement(
                "UPDATE tbPiloto SET Nombre_Piloto = ?, Edad_Piloto = ?, Peso_Piloto = ?, Correo_Piloto = ? WHERE UUID_Piloto = ?"
            )!!

            updatePiloto.setString(1, nombrePiloto)   // Nombre
            updatePiloto.setInt(2, edad)               // Edad
            updatePiloto.setDouble(3, peso)            // Peso
            updatePiloto.setString(4, correo)          // Correo
            updatePiloto.setString(5, UUID_Piloto)     // UUID

            updatePiloto.executeUpdate()

            val commit = objConexion.prepareStatement("COMMIT")!!
            commit.executeUpdate()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datos[position]
        holder.txtNombreCard.text = item.Nombre_Piloto // Ajusta el nombre del campo

        holder.imgDelete.setOnClickListener {
            val contexto = holder.itemView.context
            val builder = AlertDialog.Builder(contexto)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Seguro de eliminar?")

            builder.setPositiveButton("Sí") { dialog, wich ->
                eliminarPiloto(item.Nombre_Piloto, position) // Cambiado a eliminarPiloto
            }
            builder.setNeutralButton("No") { dialog, wich ->
                dialog.dismiss()
            }
            builder.show()
        }

        holder.imgEdit.setOnClickListener {
            val contexto = holder.itemView.context

            val builder = AlertDialog.Builder(contexto)
            builder.setTitle("Actualizar")
            builder.setMessage("¿Seguro de actualizar?")

            // Crear campos de edición para todos los datos
            val cuadroNombre = EditText(contexto)
            cuadroNombre.setHint(item.Nombre_Piloto)

            val cuadroEdad = EditText(contexto)
            cuadroEdad.setHint(item.Edad_Piloto.toString())

            val cuadroPeso = EditText(contexto)
            cuadroPeso.setHint(item.Peso_Piloto.toString())

            val cuadroCorreo = EditText(contexto)
            cuadroCorreo.setHint(item.Correo_Piloto)

            // Añadir todos los campos al AlertDialog
            val layout = LinearLayout(contexto)
            layout.orientation = LinearLayout.VERTICAL
            layout.addView(cuadroNombre)
            layout.addView(cuadroEdad)
            layout.addView(cuadroPeso)
            layout.addView(cuadroCorreo)
            builder.setView(layout)

            builder.setPositiveButton("Actualizar") { dialog, wich ->
                val nuevoNombre = cuadroNombre.text.toString()
                val nuevaEdad = cuadroEdad.text.toString().toInt()
                val nuevoPeso = cuadroPeso.text.toString().toDouble()
                val nuevoCorreo = cuadroCorreo.text.toString()

                editarPiloto(nuevoNombre, item.UUID_Piloto, nuevaEdad, nuevoPeso, nuevoCorreo)
            }

            builder.setNeutralButton("Cancelar") { dialog, wich ->
                dialog.dismiss()
            }
            builder.show()
        }
    }
}
