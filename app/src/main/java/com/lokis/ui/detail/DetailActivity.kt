package com.lokis.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.lokis.R
import com.lokis.databinding.ActivityDetailBinding
import com.lokis.databinding.ContentDetailBinding

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_URL = "extra_url"
        const val EXTRA_RATING = "extra_rating"
        const val EXTRA_DESCRIPTION = "extra_description"
    }

    //private var menu: Menu? = null
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btn_back: ImageView = findViewById(R.id.backarrowHome)

        btn_back.setOnClickListener {
            onBackPressed()
        }

        val name = intent.getStringExtra(EXTRA_NAME)
        val url = intent.getStringExtra(EXTRA_URL)
        val rating = intent.getStringExtra(EXTRA_RATING)
        val deskripsi = intent.getStringExtra(EXTRA_DESCRIPTION)
        val bundle = Bundle()
        bundle.putString(EXTRA_NAME, name)
        bundle.putString(EXTRA_URL, url)
        bundle.putString(EXTRA_RATING, rating)
        bundle.putString(EXTRA_DESCRIPTION, deskripsi)

        viewModel = ViewModelProvider(this@DetailActivity, ViewModelProvider.NewInstanceFactory())[DetailViewModel::class.java]
        showLoading(true)
        viewModel.setLocationDetail()
        viewModel.getDetail().observe(this) {

            if (it != null) {
                showLoading(false)
                binding.apply {
                    tvNameDetail.text = it.name
                    tvRate.text = it.rating
                    tvDesc.text = it.deskripsi
                    Glide.with(this@DetailActivity)
                        .load(it.url)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .centerCrop()
                        .into(imgDetail)
                }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}