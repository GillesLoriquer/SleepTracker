package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.NightRowBinding

class SleepNightAdapter(private val clickListener: SleepNightListener) :
        ListAdapter<SleepNight, SleepNightAdapter.SleepNightViewHolder>(DIFF_CALLBACK) {

    companion object {
        // DIFF_CALLBACK est définie static car elle est utilisée pour appeler le constructeur parent
        // Si cette variable n'était pas static, elle ne pourrait être utilisée pour la passer au
        // constructeur parent car une instance de NoteAdapter devrait être créée pour pouvoir y accéder
        // Or, pour rappel, l'exécution du constructeur parent se fait avant celle du constructeur
        // de NoteAdapter
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SleepNight>() {
            override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
                return oldItem.nightId == newItem.nightId
            }

            override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepNightViewHolder {
        return SleepNightViewHolder.from(parent)
    }

    override fun onBindViewHolder(holderSleep: SleepNightViewHolder, position: Int) {
        holderSleep.bind(getItem(position), clickListener)
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
}