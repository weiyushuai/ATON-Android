package com.juzix.wallet.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.widget.CustomProgressBar;
import com.juzix.wallet.component.widget.ShadowDrawable;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.WalletEntity;

import java.util.List;

public class WalletHorizontalRecycleViewAdapter extends RecyclerView.Adapter<WalletHorizontalRecycleViewAdapter.ViewHolder> {

    private Context mContext;
    private List<WalletEntity> mList;
    private OnRecycleViewItemClickListener mOnItemClickListener;
    private WalletEntity mSelectedWallet;
    private int mShapeRadius;
    private int mShadowRadius;
    private int mSelectWidth;
    private int mSelectHeight;
    private int mNormalWidth;
    private int mNormalHeight;

    public WalletHorizontalRecycleViewAdapter(Context context, List<WalletEntity> walletList) {
        mContext = context;
        mList = walletList;
        mSelectedWallet = null;
        mShapeRadius = (int) context.getResources().getDimension(R.dimen.assetsWalletSelectedShapeRadius);
        mShadowRadius = (int) context.getResources().getDimension(R.dimen.assetsWalletSelectedShadowRadius);
        mSelectWidth = (int) context.getResources().getDimension(R.dimen.assetsWalletSelectedItemWidth);
        mSelectHeight = (int) context.getResources().getDimension(R.dimen.assetsWalletSelectedItemHeight);
        mNormalWidth = (int) context.getResources().getDimension(R.dimen.assetsWalletNormalItemWidth);
        mNormalHeight = (int) context.getResources().getDimension(R.dimen.assetsWalletNormalItemHeight);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final WalletEntity walletEntity = mList.get(position);
        if (walletEntity instanceof IndividualWalletEntity) {
            setIndividualWalletView(position, (IndividualWalletEntity) walletEntity, holder);
        } else {
            SharedWalletEntity sharedWalletEntity = (SharedWalletEntity) walletEntity;
            if (!sharedWalletEntity.isFinished()) {
                setLoadWalletView(position, sharedWalletEntity, holder);
            } else {
                setSharedWalletView(position, sharedWalletEntity, holder);
            }
        }
        holder.itemView.setTag(position);//将位置保存在tag中
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //负责创建视图
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_wallet_list1, null);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setSelectedWallet(WalletEntity selectedWallet) {
        mSelectedWallet = selectedWallet;
    }

    public void setOnItemClickListener(OnRecycleViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecycleViewItemClickListener {
        void onContentViewClick(WalletEntity walletEntity);
    }

    private void setLoadWalletView(final int position, final SharedWalletEntity walletEntity, ViewHolder holder) {
        holder.vShadow.setVisibility(View.GONE);
        holder.rlItem.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.progressBar.setProgress(walletEntity.getProgress(), walletEntity.getName(), R.drawable.icon_assets_joint_p, R.drawable.icon_assets_joint_n);
        holder.progressBar.setOnClickListener(null);
    }

    private void setIndividualWalletView(final int position, final IndividualWalletEntity walletEntity, ViewHolder holder) {
        holder.progressBar.setVisibility(View.GONE);
        holder.rlItem.setVisibility(View.VISIBLE);
        holder.rlItem.findViewById(R.id.v_new_msg).setVisibility(View.GONE);
        TextView tvName = holder.rlItem.findViewById(R.id.tv_item2_name);
        ImageView ivIcon = holder.rlItem.findViewById(R.id.iv_item2_icon);
        ShadowDrawable.setShadowDrawable(holder.vShadow,
                ContextCompat.getColor(mContext, R.color.color_660051ff),
                mShapeRadius,
                ContextCompat.getColor(mContext, R.color.color_660051ff),
                mShadowRadius,
                0,
                0);
        if (mSelectedWallet == walletEntity) {
            holder.vShadow.setVisibility(View.VISIBLE);
            holder.rlItem.setBackgroundResource(R.drawable.bg_assets_classic_h);
            ivIcon.setImageResource(R.drawable.icon_assets_classic_h);
            tvName.setTextColor(ContextCompat.getColor(mContext, R.color.color_ffffff));
            tvName.setText(walletEntity.getName());
        } else {
            holder.vShadow.setVisibility(View.GONE);
            holder.rlItem.setBackgroundResource(R.drawable.bg_assets_classic_n);
            ivIcon.setImageResource(R.drawable.icon_assets_classic_n);
            tvName.setTextColor(ContextCompat.getColor(mContext, R.color.color_105cfe));
            tvName.setText(walletEntity.getName());
        }
        holder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedWallet != walletEntity) {
                    setSelectedWallet(walletEntity);
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onContentViewClick(walletEntity);
                    }
                }
            }
        });
    }

    private void setSharedWalletView(final int position, final SharedWalletEntity walletEntity, ViewHolder holder) {
        holder.rlItem.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.GONE);
        TextView tvName = holder.rlItem.findViewById(R.id.tv_item2_name);
        ImageView ivIcon = holder.rlItem.findViewById(R.id.iv_item2_icon);
        ShadowDrawable.setShadowDrawable(holder.vShadow,
                ContextCompat.getColor(mContext, R.color.color_660051ff),
                mShapeRadius,
                ContextCompat.getColor(mContext, R.color.color_660051ff)
                , mShadowRadius,
                0,
                0);
        if (mSelectedWallet == walletEntity) {
            holder.rlItem.setBackgroundResource(R.drawable.bg_assets_joint_h);
            ivIcon.setImageResource(R.drawable.icon_assets_joint_h);
            tvName.setTextColor(ContextCompat.getColor(mContext, R.color.color_ffffff));
            tvName.setText(walletEntity.getName());
            holder.vShadow.setVisibility(View.VISIBLE);
        } else {
            holder.rlItem.setBackgroundResource(R.drawable.bg_assets_joint_n);
            ivIcon.setImageResource(R.drawable.icon_assets_joint_n);
            tvName.setTextColor(ContextCompat.getColor(mContext, R.color.color_105cfe));
            tvName.setText(walletEntity.getName());
            holder.vShadow.setVisibility(View.GONE);
        }
        holder.rlItem.findViewById(R.id.v_new_msg).setVisibility(walletEntity.isHasUnreadMessage() ? View.VISIBLE : View.GONE);
        holder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedWallet != walletEntity) {
                    setSelectedWallet(walletEntity);
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onContentViewClick(walletEntity);
                    }
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout flItem;
        public RelativeLayout rlItem;
        public CustomProgressBar progressBar;
        public View vShadow;

        public ViewHolder(View convertView) {
            super(convertView);
            flItem = (FrameLayout) convertView.findViewById(R.id.fl_item);
            rlItem = (RelativeLayout) convertView.findViewById(R.id.rl_item2);
            progressBar = (CustomProgressBar) convertView.findViewById(R.id.pb_create);
            vShadow = convertView.findViewById(R.id.v_shadow);
        }
    }
}
