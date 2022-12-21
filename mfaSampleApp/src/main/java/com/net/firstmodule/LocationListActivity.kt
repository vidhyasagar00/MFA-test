package com.net.firstmodule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.net.routee.setUp.ApplicationInteractionClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LocationListActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: LocationListViewAdapter
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    private val myReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.getBooleanExtra("isUpdated", false) == true) {
                scope.launch {
                    val locations =
                        ApplicationInteractionClass(this@LocationListActivity).getLocations()
                    adapter.addLocations(locations)
                    locations?.size?.let { recyclerView.scrollToPosition(it - 1) }

                    recyclerView.visibility = View.VISIBLE
                    findViewById<TextView>(R.id.txtEmptyView).visibility = View.GONE
                }
            } else if (p1?.getBooleanExtra("sendToServer", false) == true) {
                scope.launch {
                    hideRecyclerView()


                }
            }

        }

    }

    private fun hideRecyclerView() {
        recyclerView.visibility = View.INVISIBLE
        findViewById<TextView>(R.id.txtEmptyView).visibility = View.VISIBLE
        Toast.makeText(this@LocationListActivity,
            "Location send to Server",
            Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list)

        recyclerView = findViewById(R.id.locationList)
        adapter = LocationListViewAdapter(arrayListOf())
        scope.launch {
            val locations = ApplicationInteractionClass(this@LocationListActivity).getLocations()
            adapter.addLocations(locations)

            locations?.size?.let { if (it > 0) recyclerView.scrollToPosition(it - 1) else hideRecyclerView() }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


    }

    override fun onResume() {
        super.onResume()
        val intent = IntentFilter()
        intent.addAction("LocationUpdation")
        if (!myReceiver.isOrderedBroadcast)
            LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
    }
}