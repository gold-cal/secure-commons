package com.secure.commons.adapters

import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.secure.commons.activities.BaseSimpleActivity
import com.secure.commons.databinding.FilepickerFavoriteBinding
import com.secure.commons.extensions.getTextSize
import com.secure.commons.views.MyRecyclerView

class FilepickerFavoritesAdapter(
    activity: BaseSimpleActivity, val paths: List<String>, recyclerView: MyRecyclerView,
    itemClick: (Any) -> Unit
) : MyRecyclerViewAdapter(activity, recyclerView, itemClick) {

    private var fontSize = 0f

    init {
        fontSize = activity.getTextSize()
    }

    override fun getActionMenuId() = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        return createViewHolder(FilepickerFavoriteBinding.inflate(layoutInflater, parent, false).root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = paths[position]
        holder.bindView(path, true, false) { itemView, _ ->
            setupView(itemView, path)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = paths.size

    override fun prepareActionMode(menu: Menu) {}

    override fun actionItemPressed(id: Int) {}

    override fun getSelectableItemCount() = paths.size

    override fun getIsItemSelectable(position: Int) = false

    override fun getItemKeyPosition(key: Int) = paths.indexOfFirst { it.hashCode() == key }

    override fun getItemSelectionKey(position: Int) = paths[position].hashCode()

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    private fun setupView(view: View, path: String) {
        FilepickerFavoriteBinding.bind(view).apply {
            filepickerFavoriteLabel.text = path
            filepickerFavoriteLabel.setTextColor(textColor)
            filepickerFavoriteLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
        }
    }
}
