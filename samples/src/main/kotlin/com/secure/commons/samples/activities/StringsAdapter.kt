package com.secure.commons.samples.activities

import android.view.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.secure.commons.activities.BaseSimpleActivity
import com.secure.commons.adapters.MyRecyclerViewAdapter
import com.secure.commons.extensions.beVisibleIf
import com.secure.commons.interfaces.ItemMoveCallback
import com.secure.commons.interfaces.ItemTouchHelperContract
import com.secure.commons.interfaces.StartReorderDragListener
import com.secure.commons.R.id.cab_delete
import com.secure.commons.R.menu.cab_delete_only
import com.secure.commons.samples.databinding.ListItemBinding
import com.secure.commons.views.MyRecyclerView
import java.util.*

class StringsAdapter(
    activity: BaseSimpleActivity, var strings: MutableList<String>, recyclerView: MyRecyclerView, val swipeRefreshLayout: SwipeRefreshLayout,
    itemClick: (Any) -> Unit
) : MyRecyclerViewAdapter(activity, recyclerView, itemClick), ItemTouchHelperContract {

    private var isChangingOrder = false
    private var startReorderDragListener: StartReorderDragListener
    private lateinit var binding: ListItemBinding

    init {
        setupDragListener(true)

        val touchHelper = ItemTouchHelper(ItemMoveCallback(this, true))
        touchHelper.attachToRecyclerView(recyclerView)

        startReorderDragListener = object : StartReorderDragListener {
            override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
                touchHelper.startDrag(viewHolder)
            }
        }
    }

    override fun getActionMenuId() = cab_delete_only

    override fun prepareActionMode(menu: Menu) {}

    override fun actionItemPressed(id: Int) {
        if (selectedKeys.isEmpty()) {
            return
        }

        when (id) {
            cab_delete -> changeOrder()
        }
    }

    override fun getSelectableItemCount() = strings.size

    override fun getIsItemSelectable(position: Int) = true

    override fun getItemSelectionKey(position: Int) = strings.getOrNull(position)?.hashCode()

    override fun getItemKeyPosition(key: Int) = strings.indexOfFirst { it.hashCode() == key }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : Binding {
        //createViewHolder(R.layout.list_item, parent)
        binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return Binding(binding)
    }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {
        if (isChangingOrder) {
            notifyDataSetChanged()
        }

        isChangingOrder = false
    }

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val item = strings[position]
        holder.bindView(item, true, true) { itemView, layoutPosition ->
            setupView(itemView, item, holder)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = strings.size

    private fun changeOrder() {
        isChangingOrder = true
        notifyDataSetChanged()
    }

    private fun setupView(view: View, string: String, holder: ViewHolder) {
        val isSelected = selectedKeys.contains(string.hashCode())
        view.apply {
            binding.itemFrame.isSelected = isSelected
            binding.itemName.text = string

            binding.dragHandle.beVisibleIf(isChangingOrder)

            if (isChangingOrder) {
                binding.dragHandle.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        startReorderDragListener.requestDrag(holder)
                    }
                    false
                }
            }
        }
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(strings, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(strings, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(myViewHolder: ViewHolder?) {
        swipeRefreshLayout.isEnabled = false
    }

    override fun onRowClear(myViewHolder: ViewHolder?) {
        swipeRefreshLayout.isEnabled = true
    }

    inner class Binding(binding: ListItemBinding) : MyRecyclerViewAdapter.ViewHolder(binding.root)
}
