package com.example.saireddy.music.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.saireddy.music.R
import com.example.saireddy.music.activity.MainActivity
import com.example.saireddy.music.fragments.AboutUsFragment
import com.example.saireddy.music.fragments.FavoriteFragment
import com.example.saireddy.music.fragments.MainScreenFragment
import com.example.saireddy.music.fragments.SettingsFragment

class NavigationDrawerAdapter(_contentList: ArrayList<String>, _getImages: IntArray, _context:Context)
    : RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {

    var contentList: ArrayList<String>? = null
    var getImages: IntArray? = null
    var mContext: Context? = null

    /*This is the constructor initialisation of the parameters. This converts the data passed from the parameters as the local params, which are used in this class*/
    init {
        this.contentList = _contentList
        this.getImages = _getImages
        this.mContext = _context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {

        var itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_navigatordrawer, parent, false)
        val returnThis = NavViewHolder(itemView)
        return returnThis

    }

    override fun getItemCount(): Int {

        return (contentList as ArrayList).size

    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder?.icon_GET?.setBackgroundResource(getImages?.get(position) as Int)
        holder?.text_GET?.setText(contentList?.get(position))

        /*Now since we want to open a new fragment at the click for every item we place the click listener according to the position of the items*/
        holder?.contentHolder?.setOnClickListener {

            /*Loading the Main Screen Fragment as the first(remember that the index starts at 0) item is All songs and the fragment corresponding to it is the Main Screen fragment*/
            if (position == 0) {
                val mainScreenFragment = MainScreenFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, mainScreenFragment)
                        .commit()
            }

            /*The next item is the Favorites option and the fragment corresponding to it is the favorite fragment at position 1*/
            else if (position == 1) {
                val favoriteFragment = FavoriteFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, favoriteFragment)
                        .commit()
            }

            /*Similarly to the above we load the Settings and the About Us fragment respectively*/
            else if (position == 2) {
                val settingsFragment = SettingsFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, settingsFragment)
                        .commit()
            } else if (position == 3) {
                val aboutUsFragment = AboutUsFragment()
                (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, aboutUsFragment)
                        .commit()
            }

            /*As we tap on any item we want our drawer to close automatically as the fragment loads. The function closeDrawers() is used for doing the same
            * Note here that we have used the drawer layout in the exact similar way we did in our MainActivity as MainActivity.Statified.drawerLayout.
            * This is because we created an object of it and hence it can be used in a similar way anywhere in our project*/
            MainActivity.Statified.drawerLayout?.closeDrawers()
        }

    }

    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var icon_GET: ImageView?=null
        var text_GET: TextView?=null
        var contentHolder: RelativeLayout?=null
        init {
            icon_GET = itemView?.findViewById(R.id.icon_navdrawer)
            text_GET = itemView?.findViewById(R.id.text_navdrawer)
            contentHolder = itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }
    }
}