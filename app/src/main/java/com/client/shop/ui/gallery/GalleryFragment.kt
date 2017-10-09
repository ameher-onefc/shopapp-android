package com.client.shop.ui.gallery

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.client.shop.R
import com.client.shop.ui.custom.ClickableViewPager
import com.shopapicore.entity.Product
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : Fragment() {

    private var adapter: FragmentStatePagerAdapter? = null
    private lateinit var product: Product
    private var isThumbnailMode: Boolean = false

    companion object {

        private const val PRODUCT = "product"
        private const val IS_THUMBNAIL_MODE = "is_thumbnail_mode"
        private const val SELECTED_POSITION = "selected_position"

        fun newInstance(product: Product, isThumbnailMode: Boolean = false, selectedPosition: Int = 0): GalleryFragment {
            val fragment = GalleryFragment()
            val args = Bundle()
            args.putParcelable(PRODUCT, product)
            args.putBoolean(IS_THUMBNAIL_MODE, isThumbnailMode)
            args.putInt(SELECTED_POSITION, selectedPosition)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        product = arguments.getParcelable(PRODUCT)
        isThumbnailMode = arguments.getBoolean(IS_THUMBNAIL_MODE)
        val selectedPosition = arguments.getInt(SELECTED_POSITION)

        adapter = setupAdapter(fragmentManager)
        adapter?.registerDataSetObserver(indicator.dataSetObserver)
        pager.adapter = adapter
        pager.currentItem = selectedPosition
        indicator.setViewPager(pager)

        if (isThumbnailMode) {
            pager.setOnItemClickListener(object : ClickableViewPager.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    startActivity(GalleryActivity.getStartIntent(context, product, pager.currentItem))
                }
            })
        }
    }

    private fun setupAdapter(fragmentManager: FragmentManager) = object : FragmentStatePagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            return ImageFragment.newInstance(product.images[position], isThumbnailMode)
        }

        override fun getCount(): Int {
            val count = product.images.size
            indicator.visibility = if (count <= 1) View.INVISIBLE else View.VISIBLE
            return count
        }
    }

    fun updateProduct(product: Product) {
        this.product = product
        adapter?.notifyDataSetChanged()
    }
}