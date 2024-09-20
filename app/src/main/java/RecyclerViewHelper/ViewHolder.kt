package RecyclerViewhelper

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recuperacionpiloto.R

class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
    val txtNombreCard=view.findViewById<TextView>(R.id.txtNombreCard)
    val imgEdit=view.findViewById<ImageView>(R.id.imgEdit)
    val imgDelete=view.findViewById<ImageView>(R.id.imgDelete)

}