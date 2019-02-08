package com.sprinthubmobile.example.pluralsightcustomviewdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sprinthubmobile.example.sprintviews.CompletionStatusView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var completionStatusView: CompletionStatusView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        completionStatusView = completion_status

        loadCompletionStatusValues()
    }

    private fun loadCompletionStatusValues() {
        val totalNumberOfItems = 10
        val completedItems = 7
        val completedStatus = BooleanArray(totalNumberOfItems)
        for (index in 0 until completedItems) {
            completedStatus[index] = true
        }

        completionStatusView.completionStatus = completedStatus
    }
}
