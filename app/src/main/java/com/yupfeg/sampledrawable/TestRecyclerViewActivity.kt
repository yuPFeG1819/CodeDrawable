package com.yupfeg.sampledrawable

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yupfeg.drawable.selectorDrawable
import com.yupfeg.drawable.shapeDrawable
import com.yupfeg.drawable.shapeStroke

/**
 *
 * @author yuPFeG
 * @date 2022/03/08
 */
class TestRecyclerViewActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_recycler_view)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_test)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TestRecyclerViewActivity)
            adapter = TestAdapter()
            setHasFixedSize(true)
        }
    }

}

class TestAdapter : RecyclerView.Adapter<TestViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_test_recycler_drawable,parent,false)
        val viewHolder = TestViewHolder(itemView)
        viewHolder.viewBg.setOnClickListener {
            val position = viewHolder.layoutPosition
            Toast.makeText(parent.context,"点击item${position}",Toast.LENGTH_SHORT).show()
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.tvName.text = "测试Item Drawable $position"
    }

    override fun getItemCount() = 30

}

class TestViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    val tvName : TextView = itemView.findViewById(R.id.tv_test_recycler_drawable_name)
    val viewBg : View = itemView.findViewById(R.id.view_test_recycler_drawable_bg)

    init {
        //在RecyclerView内复用，不能给Item赋值同一个`Drawable对象实例，
        // 在触发<selector>标签状态时会导致复用的另一个item同时触发状态显示
        viewBg.background = createDrawable()
    }

    private fun createDrawable() : Drawable{
        return selectorDrawable {
            normal = shapeDrawable {
                solid = Color.parseColor("#EDEDED")
                radius = 15f
                stroke = shapeStroke {
                    width = 1f
                    color = Color.parseColor("#DCDCDC")
                }
            }

            pressed = shapeDrawable {
                solid = Color.parseColor("#EF9A9A")
                radius = 15f
                stroke = shapeStroke {
                    width = 1f
                    color = Color.parseColor("#DCDCDC")
                }
            }
        }
    }
}