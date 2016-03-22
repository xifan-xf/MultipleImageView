package geek.fan.multipleimageview.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import geek.fan.multipleimageview.R;

/**
 * Created by fan on 16/3/17.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private ArrayList<ArrayList<String>> mUrls;
    private Context mContext;

    public PhotoAdapter(Context context, ArrayList<ArrayList<String>> urls) {
        mUrls = urls;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mMulipleIv.setImageUrls(mUrls.get(position));
        holder.mMulipleIv.setOnClickItemListener(new MultipleImageView.OnClickItemListener() {
            @Override
            public void onClick(int i, ArrayList<String> urls) {
                Toast.makeText(mContext, "click on item:" + i, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public int getItemCount() {
        return mUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MultipleImageView mMulipleIv;

        public ViewHolder(View itemView) {
            super(itemView);
            mMulipleIv = (MultipleImageView) itemView.findViewById(R.id.multiple_image);
        }
    }
}
