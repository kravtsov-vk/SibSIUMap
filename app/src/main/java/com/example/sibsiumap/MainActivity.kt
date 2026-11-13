package com.example.sibsiumap

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    var old_map_fragment:Fragment = MapFragment()
    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        loadFragment(MapFragment())
        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setSelectedItemId(R.id.map);
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.map -> {
                    loadFragment(old_map_fragment)
                    true
                }
                R.id.lib -> {
                    loadFragment(LibFragment())
                    true
                }
                R.id.canteen -> {
                    loadFragment(CanteenFragment())
                    true
                }
                R.id.info -> {
                    loadFragment(InfoFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    fun GT_click(view: View)
    {
        old_map_fragment = GornoTehnologyFragment()
        loadFragment(GornoTehnologyFragment())
    }
    fun M_click(view: View)
    {
        old_map_fragment = MetalurgicFragment()
        loadFragment(MetalurgicFragment())
    }

    fun HOME_click(view: View)
    {
        old_map_fragment = MapFragment()
        loadFragment(MapFragment())
    }


    fun Floor_click(view: View)
    {
        val id = view.getId()
        if (id == R.id.Mfl1) {
            old_map_fragment = M1Fragment()
        } else if (id == R.id.Mfl2)
        {
            old_map_fragment = M2Fragment()
        } else if (id == R.id.Mfl3)
        {
            old_map_fragment = M3Fragment()
        } else if (id == R.id.Mfl4)
        {
            old_map_fragment = M4Fragment()
        } else if (id == R.id.Mfl5)
        {
            old_map_fragment = M5Fragment()
        } else if (id == R.id.Mfl6)
        {
            old_map_fragment = M6Fragment()
        }

        else if (id == R.id.GTfl2)
        {
            old_map_fragment = GT2Fragment()
        } else if (id == R.id.GTfl3)
        {
            old_map_fragment = GT3Fragment()
        } else if (id == R.id.GTfl4)
        {
            old_map_fragment = GT4Fragment()
        } else if (id == R.id.GTfl5)
        {
            old_map_fragment = GT5Fragment()
        } else if (id == R.id.GTfl6)
        {
            old_map_fragment = GT6Fragment()
        }
        loadFragment(old_map_fragment)
    }

    fun Route_click(view: View) {
        old_map_fragment = RouteFragment()
        loadFragment(RouteFragment())
    }
}