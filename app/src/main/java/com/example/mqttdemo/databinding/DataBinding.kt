package com.example.mqttdemo.databinding

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

object DataBinding {

	@JvmStatic
	@BindingAdapter("recyclerViewAdapter")
	fun RecyclerView.bindRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>) {
		this.adapter = adapter
	}

	@JvmStatic
	@BindingAdapter("imageLoad")
	fun ImageView.bindImageLoad(icon: Int) {
		setImageResource(icon)
	}

	@JvmStatic
	@BindingAdapter("setHasFixedSize")
	fun RecyclerView.bindHasFixedSize(flag: Boolean) {
		setHasFixedSize(flag)
	}

	@JvmStatic
	@BindingAdapter("visible")
	fun View.bindVisibility(visible: Boolean) {
		isVisible = visible
	}
}
