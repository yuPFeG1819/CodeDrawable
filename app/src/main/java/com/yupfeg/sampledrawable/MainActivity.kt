package com.yupfeg.sampledrawable

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.yupfeg.drawable.*
import com.yupfeg.sampledrawable.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mViewBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mViewBinding.viewConfig = BindConfig()


        mViewBinding.apply {

            tvSolidShape.setOnClickListener {
                showToast("single solid")
            }
            tvSolidStateListShape.setOnClickListener {
                showToast("color state list Shape")
            }
            tvGradientShape.setOnClickListener {
                showToast("gradient shape")
            }
            tvDashStrokeShapeDrawable.setOnClickListener {
                showToast("dash stroke shape")
            }
            tvOvalShapeDrawable.setOnClickListener {
                showToast("oval shape")
            }
            tvSelectorShape.setOnClickListener {
                showToast("selector shape")
            }
            tvLayerListDrawable.setOnClickListener {
                showToast("layer list Drawable")
            }
            tvRippleDrawable.setOnClickListener {
                showToast("ripple layer list drawable")
            }

            btnChangeProgress.setOnClickListener {
                val randomProgress = (0 until 100).random()
                mViewBinding.progressBarClipLayer.progress = randomProgress
            }

            btnTestRecyclerView.setOnClickListener {
                startActivity(Intent(this@MainActivity,TestRecyclerViewActivity::class.java))
            }
        }

    }

    private fun showToast(text : String){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
    }

    inner class BindConfig{

        /**
         * 文本的颜色状态集
         * */
        val textColorState : ColorStateList = selectorColor {
            pressed = Color.WHITE
            normal = Color.BLACK
        }

        val textColorState2 : ColorStateList = selectorColor {
            pressed = Color.YELLOW
            normal = Color.WHITE
        }

        /**单色矩形*/
        val singleSolidShape : Drawable = shapeDrawable {
            solid = Color.parseColor("#4DD0E1")
            radius = 8f
        }

        /**颜色状态集矩形*/
        @SuppressLint("ObsoleteSdkInt")
        val colorStateSolidShape : Drawable = shapeDrawable {
            solid = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                selectorColor {
                    normal = Color.parseColor("#8BC34A")
                    pressed = Color.parseColor("#C5E1A5")
                }
            }else{
                //注意在api 21以下不支持selector color
                Color.parseColor("#8BC34A")
            }
            radius = 8f
        }

        /**渐变颜色矩形*/
        val gradientShape : Drawable = shapeDrawable {
            gradient = gradientColor {
                startColor = Color.parseColor("#E040FB")
                endColor = Color.parseColor("#7986CB")
            }
            stroke = shapeStroke {
                width = 1f
                color = Color.parseColor("#49599A")
            }
            radius = 8f
        }

        /**虚线描边矩形*/
        val dashStrokeShape : Drawable = shapeDrawable {
            solid = Color.GRAY
            stroke = shapeStroke {
                width = 1f
                dashWidth = 4f
                dashGap = 3f
                color = Color.parseColor("#6F79A8")
            }
            radius = 8f
        }

        /**圆形Drawable*/
        val ovalShape : Drawable = shapeDrawable {
            shapeType = ShapeType.OVAL
            solid = Color.parseColor("#40C4FF")
        }

        /**
         * 选择器状态集Drawable
         * */
        val selectorShapeDrawable : Drawable = selectorDrawable {
            normal = shapeDrawable {
                solid = Color.parseColor("#42A5F5")
                radius = 8f
            }
            pressed = shapeDrawable {
                solid = Color.parseColor("#90CAF9")
                radius = 8f
            }
        }

        /**
         * 图层叠加Drawable
         * */
        val layerListDrawable : Drawable = layerDrawable {
            //底层阴影
            addItem {
                left = 3
                top = 3
                drawable = shapeDrawable {
                    solid = Color.parseColor("#CFD8DC")
                    radius = 12f
                }
            }
            //前景显示drawable
            addItem {
                right = 3
                bottom = 3
                drawable = shapeDrawable {
                    solid = selectorColor {
                        pressed = Color.parseColor("#A5D6A7") //按下时显示颜色
                        normal = Color.parseColor("#66BB6A") //默认状态
                    }
                    radius = 12f
                    stroke = shapeStroke {
                        color = Color.parseColor("#FFCA28")
                        width = 1f
                        dashWidth = 3f
                        dashGap = 2f
                    }
                }
            }
        }

        /**
         * 水波纹Drawable
         * - 只在API 21以上生效，否则只显示原始Drawable，没有水波纹
         * */
        val rippleDrawable : Drawable = rippleDrawable {
            color = Color.parseColor("#F9FBE7")
            item = selectorDrawable {
                normal = shapeDrawable {
                    solid = Color.parseColor("#FF5252")
                    radius = 8f
                }
                pressed = shapeDrawable {
                    solid = Color.parseColor("#F48FB1")
                    radius = 8f
                }
            }
        }

        /**
         * 作为原生ProgressBar的进度条背景
         * */
        val progressClipLayerDrawable : Drawable = layerDrawable {
            val progressDrawable = clipDrawable {
                drawable = shapeDrawable {
                    radius = 20f
                    gradient = gradientColor {
                        angle = 270
                        startColor = Color.parseColor("#AA00FF")
                        endColor = Color.parseColor("#2962FF")
                    }
                }
            }

            //设置作为进度条默认背景drawable
            addItem {
                id = android.R.id.background
                drawable = shapeDrawable {
                    radius = 20f
                    gradient = gradientColor {
                        orientation = GradientDrawable.Orientation.TOP_BOTTOM
                        startColor = Color.parseColor("#D4E2FF")
                        endColor = Color.parseColor("#E3F2FD")
                    }
                }
            }
            //设置作为二级进度条的绘制drawable
            addItem {
                id = android.R.id.secondaryProgress
                drawable = progressDrawable
            }
            //设置作为一级进度条的绘制drawable
            addItem {
                id = android.R.id.progress
                drawable = progressDrawable
            }

        }

        /**
         * 旋转<rotate>标签的代码Drawable
         * */
        val rotateDrawable : Drawable by lazy {
            rotateDrawable{
                fromDegrees = 0f
                drawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.loading)
            }
        }


    }
}