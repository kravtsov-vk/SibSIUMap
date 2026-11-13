package com.example.sibsiumap

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class InfoFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.textView18)
        textView.movementMethod = LinkMovementMethod.getInstance()

        val textView2 = view.findViewById<TextView>(R.id.textView19)
        textView2.movementMethod = LinkMovementMethod.getInstance()

        val textView3 = view.findViewById<TextView>(R.id.textView20)
        textView3.movementMethod = LinkMovementMethod.getInstance()

        val textView4 = view.findViewById<TextView>(R.id.textView21)
        textView4.movementMethod = LinkMovementMethod.getInstance()
    }
}