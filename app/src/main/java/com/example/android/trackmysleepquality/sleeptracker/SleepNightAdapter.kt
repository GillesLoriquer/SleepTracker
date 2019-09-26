package com.example.android.trackmysleepquality.sleeptracker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.NightRowBinding

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class SleepNightAdapter(private val clickListener: SleepNightListener) :
        ListAdapter<SleepNightAdapter.DataItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        // DIFF_CALLBACK est définie static car elle est utilisée pour appeler le constructeur parent
        // Si cette variable n'était pas static, elle ne pourrait être utilisée pour la passer au
        // constructeur parent car une instance de NoteAdapter devrait être créée pour pouvoir y accéder
        // Or, pour rappel, l'exécution du constructeur parent se fait avant celle du constructeur
        // de NoteAdapter
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> SleepNightViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addHeaderAndSubmitList(list: List<SleepNight>?) {
        val items = when (list) {
            null -> listOf(DataItem.Header)
            else -> listOf(DataItem.Header) + list.map { DataItem.SleepNightItem(it) }
        }
        submitList(items)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is SleepNightViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                viewHolder.bind(nightItem.sleepNight, clickListener)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.header, parent, false)
                return TextViewHolder(view)
            }
        }
    }

    class SleepNightViewHolder private constructor(private val binding: NightRowBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): SleepNightViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NightRowBinding.inflate(layoutInflater, parent, false)
                return SleepNightViewHolder(binding)
            }
        }

        fun bind(sleepNight: SleepNight, clickListener: SleepNightListener) {
            binding.sleepNight = sleepNight
            binding.clickListener = clickListener
            // This call is an optimization that asks data binding to execute any pending bindings
            // right away. It's always a good idea to call executePendingBindings() when you use
            // binding adapters in a RecyclerView, because it can slightly speed up sizing the views.
            binding.executePendingBindings()

        }
    }

    class SleepNightListener(val clickListener: (sleepNightId: Long) -> Unit) {
        fun onClick(sleepNight: SleepNight) = clickListener(sleepNight.nightId)
    }

    sealed class DataItem {
        abstract val id: Long

        // Wrapper du type objet SleepNight
        class SleepNightItem(val sleepNight: SleepNight) : DataItem() {
            override val id = sleepNight.nightId
        }

        // Wrapper tu type objet Header (singleton car un seul header)
        object Header : DataItem() {
            // Long.MIn_VALUE is a very, very small number (literally, -2 to the power of 63).
            // So, this will never conflict with any nightId in existence.
            override val id = Long.MIN_VALUE
        }
    }
}