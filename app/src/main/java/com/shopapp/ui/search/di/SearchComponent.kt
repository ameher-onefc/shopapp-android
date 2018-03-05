package com.shopapp.ui.search.di

import com.shopapp.ui.search.SearchFragment
import com.shopapp.ui.category.CategoryListFragment
import dagger.Subcomponent

@Subcomponent(modules = [SearchModule::class])
interface SearchComponent {

    fun inject(activity: SearchFragment)
}