/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.ingest.adapter;

import android.app.Activity;
import android.content.Context;
import android.mtp.MtpObjectInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import com.android.gallery3d.R;
import com.android.gallery3d.ingest.MtpDeviceIndex;
import com.android.gallery3d.ingest.MtpDeviceIndex.SortOrder;
import com.android.gallery3d.ingest.SimpleDate;
import com.android.gallery3d.ingest.ui.DateTileView;
import com.android.gallery3d.ingest.ui.MtpThumbnailTileView;

public class MtpAdapter extends BaseAdapter implements SectionIndexer {
    public static final int ITEM_TYPE_MEDIA = 0;
    public static final int ITEM_TYPE_BUCKET = 1;

    private Context mContext;
    private MtpDeviceIndex mModel;
    private SortOrder mSortOrder = SortOrder.Descending;
    private LayoutInflater mInflater;

    public MtpAdapter(Activity context) {
        super();
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setMtpDeviceIndex(MtpDeviceIndex index) {
        mModel = index;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mModel != null ? mModel.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mModel.get(position, mSortOrder);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // If the position is the first in its section, then it corresponds to
        // a title tile, if not it's a media tile
        if (position == getPositionForSection(getSectionForPosition(position))) {
            return ITEM_TYPE_BUCKET;
        } else {
            return ITEM_TYPE_MEDIA;
        }
    }

    public boolean itemAtPositionIsBucket(int position) {
        return getItemViewType(position) == ITEM_TYPE_BUCKET;
    }

    public boolean itemAtPositionIsMedia(int position) {
        return getItemViewType(position) == ITEM_TYPE_MEDIA;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (type == ITEM_TYPE_MEDIA) {
            MtpThumbnailTileView imageView;
            if (convertView == null) {
                imageView = (MtpThumbnailTileView) mInflater.inflate(
                        R.layout.ingest_thumbnail, parent, false);
            } else {
                imageView = (MtpThumbnailTileView) convertView;
            }
            imageView.setMtpDeviceAndObjectInfo(mModel.getDevice(), (MtpObjectInfo)getItem(position));
            return imageView;
        } else {
            DateTileView dateTile;
            if (convertView == null) {
                dateTile = (DateTileView) mInflater.inflate(
                        R.layout.ingest_date_tile, parent, false);
            } else {
                dateTile = (DateTileView) convertView;
            }
            dateTile.setDate((SimpleDate)getItem(position));
            return dateTile;
        }
    }

    @Override
    public int getPositionForSection(int section) {
        if (getCount() == 0) {
            return 0;
        }
        int numSections = getSections().length;
        if (section >= numSections) {
            section = numSections - 1;
        }
        return mModel.getFirstPositionForBucketNumber(section, mSortOrder);
    }

    @Override
    public int getSectionForPosition(int position) {
        int count = getCount();
        if (count == 0) {
            return 0;
        }
        if (position >= count) {
            position = count - 1;
        }
        return mModel.getBucketNumberForPosition(position, mSortOrder);
    }

    @Override
    public Object[] getSections() {
        return getCount() > 0 ? mModel.getBuckets(mSortOrder) : null;
    }
}
