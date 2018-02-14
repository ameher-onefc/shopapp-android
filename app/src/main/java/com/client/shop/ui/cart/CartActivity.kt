package com.client.shop.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.client.shop.R
import com.client.shop.ShopApplication
import com.client.shop.ui.base.lce.BaseLceActivity
import com.client.shop.ui.base.lce.view.LceEmptyView
import com.client.shop.ui.base.recycler.SwipeToDeleteCallback
import com.client.shop.ui.cart.adapter.CartAdapter
import com.client.shop.ui.cart.contract.CartPresenter
import com.client.shop.ui.cart.contract.CartView
import com.client.shop.ui.cart.di.CartModule
import com.client.shop.ui.checkout.CheckoutActivity
import com.client.shop.ui.home.HomeActivity
import com.client.shop.ui.item.cart.CartItem
import com.client.shop.ui.product.ProductDetailsActivity
import com.client.shop.getaway.entity.CartProduct
import com.client.shop.ui.base.recycler.OnItemClickListener
import com.client.shop.ui.base.recycler.divider.SpaceDecoration
import kotlinx.android.synthetic.main.activity_cart.*
import javax.inject.Inject

class CartActivity :
    BaseLceActivity<List<CartProduct>, CartView, CartPresenter>(),
    CartView,
    OnItemClickListener,
    CartItem.ActionListener {

    @Inject
    lateinit var cartPresenter: CartPresenter
    private val data: MutableList<CartProduct> = mutableListOf()
    private lateinit var adapter: CartAdapter

    companion object {
        fun getStartIntent(context: Context) = Intent(context, CartActivity::class.java)
    }

    //ANDROID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkoutButton.setOnClickListener { startActivity(CheckoutActivity.getStartIntent(this)) }
        setupRecyclerView()

        loadData()
        setTitle(getString(R.string.my_cart))
    }

    //INIT

    override fun inject() {
        ShopApplication.appComponent.attachCartComponent(CartModule()).inject(this)
    }

    override fun getContentView() = R.layout.activity_cart

    override fun createPresenter() = cartPresenter

    override fun useModalStyle() = true

    //SETUP

    private fun setupRecyclerView() {
        adapter = CartAdapter(data, this)
        adapter.setHasStableIds(true)
        adapter.actionListener = this
        val decoration =
            SpaceDecoration(topSpace = resources.getDimensionPixelSize(R.dimen.cart_item_divider))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(decoration)
        val swipeHandler = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                data.getOrNull(viewHolder.adapterPosition)?.let {
                    presenter.removeProduct(it.productVariant.id)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    override fun setupEmptyView(emptyView: LceEmptyView) {
        emptyView.customiseEmptyImage(R.drawable.ic_cart_empty)
        emptyView.customiseEmptyMessage(R.string.empty_cart_message)
        emptyView.customiseEmptyButtonText(R.string.start_shopping)
        emptyView.customiseEmptyButtonVisibility(true)
    }

    //LCE

    override fun loadData(pullToRefresh: Boolean) {
        super.loadData(pullToRefresh)
        presenter.loadCartItems()
    }

    override fun showContent(data: List<CartProduct>) {
        super.showContent(data)
        this.data.clear()
        this.data.addAll(data)
        adapter.notifyDataSetChanged()
        totalPriceView.setData(this.data)
    }

    //CALLBACK

    override fun emptyButtonClicked() {
        startActivity(HomeActivity.getStartIntent(this, true))
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
    }

    override fun onItemClicked(position: Int) {
        data.getOrNull(position)?.let {
            startActivity(ProductDetailsActivity.getStartIntent(this, it.productVariant))
        }
    }

    override fun onRemoveButtonClicked(productVariantId: String) {
        presenter.removeProduct(productVariantId)
    }

    override fun onQuantityChanged(productVariantId: String, newQuantity: Int) {
        presenter.changeProductQuantity(productVariantId, newQuantity)
    }
}