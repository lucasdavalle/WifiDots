package com.example.safesporting

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment(val listener:(String) -> Unit) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        if (hourOfDay<10&&minute<10)
            listener("0$hourOfDay:0$minute")
        if (hourOfDay<10&&minute>10)
            listener("0$hourOfDay:$minute")
        if (hourOfDay>10&&minute<10)
            listener("$hourOfDay:0$minute")
        if (hourOfDay>10&&minute>10)
            listener("$hourOfDay:$minute")
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val picker = TimePickerDialog(activity as Context, this, hour, minute, true)
        return picker
    }
}