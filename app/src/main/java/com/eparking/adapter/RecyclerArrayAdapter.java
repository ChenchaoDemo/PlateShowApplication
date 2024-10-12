/*
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.eparking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eparking.adapter.holder.BaseViewHolder;
import com.eparking.adapter.inter.OnItemChildClickListener;
import com.eparking.adapter.inter.OnItemClickListener;
import com.eparking.adapter.inter.OnItemLongClickListener;
import com.eparking.adapter.utils.RecyclerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/5/2
 *     desc  : 自定义adapter
 *     revise: 注意这里使用泛型数据类型
 * </pre>
 */
public abstract class RecyclerArrayAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private List<T> mObjects;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private OnItemChildClickListener mOnItemChildClickListener;
    private final Object mLock = new Object();
    private boolean mNotifyOnChange = true;
    private Context mContext;

    public RecyclerArrayAdapter(Context context) {
        RecyclerUtils.checkContent(context);
        init(context, new ArrayList<T>());
    }


    public RecyclerArrayAdapter(Context context, T[] objects) {
        RecyclerUtils.checkContent(context);
        init(context, Arrays.asList(objects));
    }


    public RecyclerArrayAdapter(Context context, List<T> objects) {
        RecyclerUtils.checkContent(context);
        init(context, objects);
    }


    private void init(Context context, List<T> objects) {
        mContext = context;
        mObjects = new ArrayList<>(objects);
    }

    /**
     * https://www.jianshu.com/p/4f66c2c71d8c
     * 创建viewHolder，主要作用是创建Item视图，并返回相应的ViewHolder
     *
     * @param parent   parent
     * @param viewType type类型
     * @return 返回viewHolder
     */
    @NonNull
    @Override
    public final BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final BaseViewHolder viewHolder = OnCreateViewHolder(parent, viewType);
        //注意：点击事件放到onCreateViewHolder更好一些，不要放到onBindViewHolder或者ViewHolder中
        setOnClickListener(viewHolder);
        return viewHolder;
    }



    /**
     * 重写该方法的作用主要是返回该Adapter所持有的Item数量
     * 这个函数包含了头部和尾部view的个数，不是真正的item个数。
     * 包含item+header头布局数量+footer底布局数量
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Override
    public final int getItemCount() {
        return mObjects.size() ;
    }


    /**
     * 绑定viewHolder，主要作用是绑定数据到正确的Item视图上。当视图从不可见到可见的时候，会调用这个方法。
     *
     * @param holder   holder
     * @param position 索引
     */
    @Override
    public final void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.itemView.setId(position);
        OnBindViewHolder(holder, position , null);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        holder.itemView.setId(position);
        if (!payloads.isEmpty()) {
            OnBindViewHolder(holder, position, payloads);
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 通过重写 RecyclerView.onViewRecycled(holder) 来回收资源。
     *
     * @param holder holder
     */
    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {
        super.onViewRecycled(holder);
    }

    /*---------------------------------子类需要重写的方法---------------------------------------*/


    /**
     * 抽象方法，子类继承
     */
    public abstract BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType);


    @SuppressWarnings("unchecked")
    private void OnBindViewHolder(BaseViewHolder holder, final int position, List<Object> payloads) {
        holder.setData(getItem(position), payloads);
    }
    /**
     * 添加所有数据
     *
     * @param collection Collection集合数据
     */
    public void addAll(Collection<? extends T> collection) {

        if (collection != null && collection.size() != 0) {
            synchronized (mLock) {
                mObjects.addAll(collection);
            }
        }
        int dataCount = collection == null ? 0 : collection.size();
        if (mNotifyOnChange) {
            notifyItemRangeInserted( getCount() - dataCount, dataCount);
        }


    }

    /**
     * 添加所有数据
     *
     * @param items 数据
     */
    public void addAll(T[] items) {

        if (items != null && items.length != 0) {
            synchronized (mLock) {
                Collections.addAll(mObjects, items);
            }
        }
        int dataCount = items == null ? 0 : items.length;
        if (mNotifyOnChange) {
            notifyItemRangeInserted( getCount() - dataCount, dataCount);
        }
    }

    /**
     * 插入，不会触发任何事情
     *
     * @param object 数据
     * @param index  索引
     */
    public void insert(T object, int index) {
        synchronized (mLock) {
            mObjects.add(index, object);
        }
        if (mNotifyOnChange) {
            notifyItemInserted(index);
        }

    }

    /**
     * 插入数组，不会触发任何事情
     *
     * @param object 数据
     * @param index  索引
     */
    public void insertAll(T[] object, int index) {
        synchronized (mLock) {
            mObjects.addAll(index, Arrays.asList(object));
        }
        int dataCount = object.length;
        if (mNotifyOnChange) {
            notifyItemRangeInserted(index, dataCount);
        }

    }

    /**
     * 插入数组，不会触发任何事情
     *
     * @param object 数据
     * @param index  索引
     */
    public void insertAll(Collection<? extends T> object, int index) {
        synchronized (mLock) {
            mObjects.addAll(index, object);
        }
        int dataCount = object.size();
        if (mNotifyOnChange) {
            notifyItemRangeInserted( index, dataCount);
        }

    }


    /**
     * 更新数据
     *
     * @param object 数据
     * @param pos    索引
     */
    public void update(T object, int pos) {
        synchronized (mLock) {
            mObjects.set(pos, object);
        }
        if (mNotifyOnChange) {
            notifyItemChanged(pos);
        }

    }


    /**
     * 删除，不会触发任何事情
     *
     * @param object 要移除的数据
     */
    public void remove(T object) {
        int position = mObjects.indexOf(object);
        synchronized (mLock) {
            if (mObjects.remove(object)) {
                if (mNotifyOnChange) {
                    notifyItemRemoved(position);
                }

            }
        }
    }
    /**
     * 删除，不会触发任何事情
     *
     * @param position 要移除数据的索引
     */
    public void remove(int position) {
        synchronized (mLock) {
            mObjects.remove(position);
        }
        if (mNotifyOnChange) {
            notifyItemRemoved( position);
        }

    }

    /**
     * 触发清空所有的数据
     */
    public void clear() {
        int count = mObjects.size();
        synchronized (mLock) {
            mObjects.clear();
        }
        if (mNotifyOnChange) {
            notifyDataSetChanged();
        }

    }

    /**
     * 使用指定的比较器对此适配器的内容进行排序
     */
    public void sort(Comparator<? super T> comparator) {
        synchronized (mLock) {
            Collections.sort(mObjects, comparator);
        }
        if (mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    /**
     * 设置操作数据[增删改查]后，是否刷新adapter
     *
     * @param notifyOnChange 默认是刷新的true
     */
    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 应该使用这个获取item个数
     */
    public int getCount() {
        return mObjects.size();
    }

    /**
     * 获取所有的数据list集合
     *
     * @return list结合
     */
    public List<T> getAllData() {
        return new ArrayList<>(mObjects);
    }

    /**
     * 获取item
     */
    protected T getItem(int position) {
        return mObjects.get(position);
    }

    /**
     * 获取item索引位置
     *
     * @param item item
     * @return 索引位置
     */
    public int getPosition(T item) {
        //搜索
        return mObjects.indexOf(item);
    }


    /**---------------------------------点击事件---------------------------------------------------*/

    /**
     * 设置item条目点击事件，注意在onCreateViewHolder中设置要优于onBindViewHolder
     *
     * @param viewHolder viewHolder
     */
    private void setOnClickListener(final BaseViewHolder viewHolder) {
        //itemView 的点击事件
        if (mItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(
                            viewHolder.getAdapterPosition());
                }
            });
        }
        if (mItemLongClickListener != null) {
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mItemLongClickListener.onItemLongClick(
                            viewHolder.getAdapterPosition());
                }
            });
        }
    }

    /**
     * 设置条目点击事件
     *
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    /**
     * 设置条目长按事件
     *
     * @param listener 监听器
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    /**
     * 设置孩子点击事件
     *
     * @param listener 监听器
     */
    public void setOnItemChildClickListener(OnItemChildClickListener listener) {
        this.mOnItemChildClickListener = listener;
    }

    public OnItemChildClickListener getOnItemChildClickListener() {
        return mOnItemChildClickListener;
    }


}
