package com.example.sibsiumap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView

class SearchFragment : Fragment() {
    var Pos:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinner: Spinner = view.findViewById(R.id.spinner)

        // 1. Создаем адаптер из массива в strings.xml
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Buildings,
            android.R.layout.simple_spinner_item
        )

        // 2. Указываем стандартный вид для выпадающего меню
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // 3. Привязываем адаптер к Spinner
        spinner.adapter = adapter



        // Исходные данные с иконками
        val items = listOf(
            ListItem(R.drawable.room, "201М Аудитория", 2, "М", "201"),
            ListItem(R.drawable.room, "202М Аудитория", 2, "М", "202"),
            ListItem(R.drawable.canteen, "Буфет \"Студент\"", 2, "М", "C"),
            ListItem(R.drawable.museum, "Музей авторской песни", 2, "М", "M"),
            ListItem(R.drawable.wc, "Мужской туалет", 2, "М", "2M"),
            ListItem(R.drawable.wc, "Женский туалет", 2, "М", "2V")
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val searchView = view.findViewById<SearchView>(R.id.searchView)

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        // Добавляем стандартный горизонтальный разделитель
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            layoutManager.orientation
        )

        recyclerView.addItemDecoration(dividerItemDecoration)
        val adapter_rec = MyAdapter(items) { clickedItem ->
            // Здесь пишем действие при нажатии на элемент списка
            val info = "${clickedItem.title} (Этаж: ${clickedItem.floor ?: "-"}, Корпус: ${clickedItem.building ?: "-"})"

            //Toast.makeText(requireContext(), "Выбрано: $info", Toast.LENGTH_SHORT).show()

        }
        recyclerView.adapter = adapter_rec

        // Слушатель ввода текста в поисковую строку
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter_rec.filter.filter(newText)
                return true
            }
        })



        val radioGroup = view.findViewById<RadioGroup>(R.id.sel_floor)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedView = radioGroup.findViewById<View>(checkedId)
            val index = radioGroup.indexOfChild(selectedView)
            if (Pos == 0) {
                adapter_rec.filterByLocation(index+1, "ГТ")
            }
            else if (Pos == 1) {
                adapter_rec.filterByLocation(index+1, "М")
            }
        }

        // 4. Обрабатываем выбор элемента
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                Pos = position
                val selectedId = radioGroup.checkedRadioButtonId
                val selectedView = radioGroup.findViewById<View>(selectedId)
                val index = radioGroup.indexOfChild(selectedView)

                if (position == 0) {
                    adapter_rec.filterByLocation(index+1, "ГТ")
                }
                else if (position == 1) {
                    adapter_rec.filterByLocation(index+1, "М")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Действие, если ничего не выбрано
            }
        }
    }
}

data class ListItem(
    val iconResId: Int,
    val title: String,
    val floor: Int? = null,       // Номер этажа (например, 2)
    val building: String? = null, // Корпус (например, "М")
    val classroom: String? = null // Аудиория
)

class MyAdapter(
    private var itemList: List<ListItem>,
    private val onItemClick: (ListItem) -> Unit
) : RecyclerView.Adapter<MyAdapter.ViewHolder>(), Filterable {

    private var fullList: List<ListItem> = ArrayList(itemList)

    // Переменные для хранения активных фильтров
    private var currentQuery: String = ""
    private var selectedFloor: Int? = null
    private var selectedBuilding: String? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.itemIcon)
        val title: TextView = view.findViewById(R.id.itemTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.icon.setImageResource(item.iconResId)

        // Обработка нажатия на всю строку элемента
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = itemList.size

    // Новый метод для выбора этажа и корпуса (передайте null, чтобы сбросить фильтр)
    fun filterByLocation(floor: Int?, building: String?) {
        selectedFloor = floor
        selectedBuilding = building
        filter.filter(currentQuery) // Перезапускаем фильтрацию с учетом текущего текста в поиске
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                currentQuery = constraint?.toString()?.trim()?.lowercase() ?: ""

                val filteredList = fullList.filter { item ->
                    // 1. Проверка совпадения по названию
                    val matchesText = currentQuery.isEmpty() ||
                            item.title.lowercase().contains(currentQuery)

                    // 2. Проверка совпадения по этажу (если фильтр включен)
                    val matchesFloor = selectedFloor == null ||
                            item.floor == selectedFloor

                    // 3. Проверка совпадения по корпусу (если фильтр включен)
                    val matchesBuilding = selectedBuilding == null ||
                            item.building.equals(selectedBuilding, ignoreCase = true)

                    matchesText && matchesFloor && matchesBuilding
                }

                return FilterResults().apply { values = filteredList }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                itemList = results?.values as? List<ListItem> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}

