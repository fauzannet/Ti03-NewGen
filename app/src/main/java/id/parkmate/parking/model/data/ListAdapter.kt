package id.parkmate.parking.model.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.parkmate.R

class ListAdapter(private val userList : ArrayList<Data>) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_activity,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = userList[position]

        holder.platnomor.text = currentitem.nopol
        holder.checkin.text = currentitem.waktuin
        holder.checkout.text = currentitem.waktuout

    }

    override fun getItemCount(): Int {
        return userList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val platnomor : TextView = itemView.findViewById(R.id.platnomor)
        val checkin : TextView = itemView.findViewById(R.id.checkin)
        val checkout : TextView = itemView.findViewById(R.id.checkout)

    }

}