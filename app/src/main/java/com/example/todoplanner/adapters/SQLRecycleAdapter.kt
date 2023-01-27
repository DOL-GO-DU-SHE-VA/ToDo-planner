package com.example.todoplanner.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoplanner.R
import com.example.todoplanner.sql.Task
import java.text.SimpleDateFormat
import java.util.*

class SQLRecycleAdapter:
    RecyclerView.Adapter<SQLRecycleAdapter.VH>() {

    private var arrayList = emptyList<Task>()

    private lateinit var mListener: OnRecycleViewListener

    interface OnRecycleViewListener{
        fun onRecycleViewClick (position: Int)
    }

    fun setOnRecycleViewClick(listener: OnRecycleViewListener){
        mListener = listener
    }

    class VH(itemView: View, listener: OnRecycleViewListener) : RecyclerView.ViewHolder(itemView){
        var header: TextView = itemView.findViewById(R.id.header)
        var description: TextView = itemView.findViewById(R.id.description)
        var dateOfCreation: TextView = itemView.findViewById(R.id.dateOfCreation)
        var dateOfUpdater: TextView = itemView.findViewById(R.id.dateOfUpdater)
        var expirationDate: TextView = itemView.findViewById(R.id.expirationDate)
        var priority: TextView = itemView.findViewById(R.id.priority)
        init {
            itemView.setOnLongClickListener {
                listener.onRecycleViewClick(adapterPosition)
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false), mListener)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val currentTask = arrayList[position]
        holder.header.text = currentTask.header
        holder.description.text = currentTask.description
        holder.dateOfCreation.text = "Создано: ${currentTask.dateOfCreation}"
        holder.dateOfUpdater.text = "Последнее обновление: ${currentTask.dateOfUpdater}"
        holder.expirationDate.text = "Дата завершения: ${currentTask.expirationDate}"
        holder.priority.text = "Приоритет: ${currentTask.priority}"
        try{
            val dateTodayString = SimpleDateFormat("dd.M.yyyy", Locale.getDefault()).format(Date())
            val expirationDateString = currentTask.expirationDate
            val expirationDate = SimpleDateFormat("dd.M.yyyy").parse(expirationDateString)
            val dateToday = SimpleDateFormat("dd.M.yyyy").parse(dateTodayString)
            if(dateToday!! > expirationDate){
                holder.header.setTextColor(Color.RED)
            }
            else{
                holder.header.setTextColor(Color.BLACK)
            }
        } catch (_: Exception){}
    }

    fun setData(data : List<Task>){
        this.arrayList = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}